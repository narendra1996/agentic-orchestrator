package com.charles.schwab.agentic.orchestrator.graph.engine;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.graph.nodes.NodeHandler;
import com.charles.schwab.agentic.orchestrator.graph.edges.EdgeRouter;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;
import com.charles.schwab.agentic.orchestrator.state.WorkflowSession;
import com.charles.schwab.agentic.orchestrator.state.WorkflowStatus;
import com.charles.schwab.agentic.orchestrator.state.WorkflowRepository;
import com.charles.schwab.agentic.orchestrator.exception.SafeStopTriggeredException;
import com.charles.schwab.agentic.orchestrator.config.AgenticProperties;
import com.charles.schwab.agentic.orchestrator.config.SecuritySanitizer;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class SDLCWorkflowEngine {

    private static final Logger log = LoggerFactory.getLogger(SDLCWorkflowEngine.class);

    private final Map<NodeId, NodeHandler> nodes = new EnumMap<>(NodeId.class);
    private final Map<NodeId, EdgeRouter> edges = new EnumMap<>(NodeId.class);
    private final AgenticProperties properties;
    private final SecuritySanitizer sanitizer;
    private final WorkflowRepository repository;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public SDLCWorkflowEngine(List<NodeHandler> nodeHandlers, List<EdgeRouter> edgeRouters, AgenticProperties properties, SecuritySanitizer sanitizer, WorkflowRepository repository) {
        this.properties = properties;
        this.sanitizer = sanitizer;
        this.repository = repository;
        for (NodeHandler handler : nodeHandlers) {
            nodes.put(handler.getNodeId(), handler);
        }
        for (EdgeRouter router : edgeRouters) {
            edges.put(router.getSourceNode(), router);
        }
        log.info("Initialized DAG Orchestrator with {} Nodes and {} Edge Routers", nodes.size(), edges.size());
    }

    public UUID startWorkflow(String rawRequirements) {
        log.info("=== Starting DAG Orchestrator ===");
        sanitizer.sanitizeInput(rawRequirements);
        
        UUID id = UUID.randomUUID();
        SDLCState state = SDLCState.initialState(rawRequirements);
        WorkflowSession session = new WorkflowSession(id, state, List.of(NodeId.ANALYZE_REQUIREMENTS));
        repository.save(session);
        
        executor.submit(() -> executeLoop(session));
        return id;
    }

    public void resumeWorkflow(UUID id, String humanInput) {
        WorkflowSession session = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Workflow ID"));
        if (session.getStatus() != WorkflowStatus.WAITING_FOR_INPUT) {
            throw new IllegalStateException("Workflow is not waiting for input.");
        }
        
        log.info("Resuming workflow {} with human input...", id);
        SDLCState state = session.getState();
        
        // Apply human input depending on where we paused
        if (session.getCurrentNodes().contains(NodeId.HUMAN_AMBIGUITY_INPUT)) {
            state = state.withRawRequirements(state.rawRequirements() + "\\nHuman Clarification: " + humanInput)
                         .withIsAmbiguous(false)
                         .addLog("Received Human Disambiguation: " + humanInput);
        } else if (session.getCurrentNodes().contains(NodeId.HUMAN_APPROVAL_GATE)) {
            state = state.addLog("Received Human Approval: " + humanInput);
        }
        
        session.setState(state);
        session.setStatus(WorkflowStatus.RUNNING);
        repository.save(session);
        
        executor.submit(() -> executeLoop(session));
    }

    private void executeLoop(WorkflowSession session) {
        try {
            while (!session.getCurrentNodes().isEmpty() && !session.getCurrentNodes().contains(NodeId.END)) {
                
                SDLCState state = session.getState();
                List<NodeId> currentNodes = session.getCurrentNodes();

                if (state.dagIterationCount() > properties.getMaxDagIterations()) {
                    throw new SafeStopTriggeredException("Workflow halted: Exceeded max DAG iterations.");
                }

                if (currentNodes.size() == 1) {
                    NodeId node = currentNodes.get(0);
                    
                    // CHECK FOR HITL GATES
                    if (node == NodeId.HUMAN_AMBIGUITY_INPUT || node == NodeId.HUMAN_APPROVAL_GATE) {
                        log.info("HITL Gate hit: {}. Pausing workflow.", node);
                        session.setStatus(WorkflowStatus.WAITING_FOR_INPUT);
                        repository.save(session);
                        return; // Halt thread
                    }

                    log.info("-> Executing Sequential Node: {}", node);
                    long start = System.currentTimeMillis();

                    if (node == NodeId.ROLLBACK) {
                        state = state.rollback();
                    } else {
                        NodeHandler handler = nodes.get(node);
                        if (handler != null) {
                            state = handler.execute(state);
                        } else {
                            log.warn("No handler found for {}", node);
                        }
                    }

                    log.info("[Telemetry] {} latency: {}ms", node, (System.currentTimeMillis() - start));

                    if (node == NodeId.SAFE_STOP) {
                        throw new SafeStopTriggeredException("Workflow halted safely due to repeated failures.");
                    }

                    EdgeRouter router = edges.get(node);
                    if (router == null) throw new IllegalStateException("No edge for " + node);
                    session.setCurrentNodes(router.route(state));
                    session.setState(state);
                    repository.save(session);

                } else {
                    log.info("-> Executing Parallel Nodes: {}", currentNodes);
                    long start = System.currentTimeMillis();
                    List<Callable<SDLCState>> tasks = new ArrayList<>();

                    for (NodeId n : currentNodes) {
                        final SDLCState captureState = state;
                        tasks.add(() -> {
                            NodeHandler handler = nodes.get(n);
                            return handler != null ? handler.execute(captureState) : captureState;
                        });
                    }

                    try {
                        List<Future<SDLCState>> results = executor.invokeAll(tasks);
                        for (Future<SDLCState> f : results) {
                            SDLCState branchState = f.get();
                            state.codeArtifacts().putAll(branchState.codeArtifacts());
                            state.auditLog().addAll(branchState.auditLog());
                        }
                    } catch (Exception e) {
                        log.error("Parallel execution failed", e);
                        throw new RuntimeException(e);
                    }

                    log.info("[Telemetry] Parallel branch latency: {}ms", (System.currentTimeMillis() - start));
                    
                    session.setCurrentNodes(List.of(NodeId.SECURITY_COMPLIANCE_AUDIT));
                    session.setState(state);
                    repository.save(session);
                }
            }
            
            session.setStatus(WorkflowStatus.COMPLETED);
            repository.save(session);
            log.info("=== DAG Orchestrator Completed Successfully ===");
            
        } catch (Exception e) {
            log.error("Workflow failed", e);
            session.setStatus(WorkflowStatus.FAILED);
            session.getState().addLog("FAILED: " + e.getMessage());
            repository.save(session);
        }
    }
}
