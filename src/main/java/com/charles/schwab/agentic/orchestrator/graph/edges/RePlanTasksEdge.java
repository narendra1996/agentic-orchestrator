package com.charles.schwab.agentic.orchestrator.graph.edges;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RePlanTasksEdge implements EdgeRouter {

    @Override
    public NodeId getSourceNode() {
        return NodeId.RE_PLAN_TASKS;
    }

    @Override
    public List<NodeId> route(SDLCState state) {
        return List.of(NodeId.DESIGN_ARCHITECTURE);
    }
}
