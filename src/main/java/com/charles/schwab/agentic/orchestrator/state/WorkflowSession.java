package com.charles.schwab.agentic.orchestrator.state;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;

import java.util.List;
import java.util.UUID;

public class WorkflowSession {
    private final UUID id;
    private volatile WorkflowStatus status;
    private volatile SDLCState state;
    private volatile List<NodeId> currentNodes;

    public WorkflowSession(UUID id, SDLCState state, List<NodeId> currentNodes) {
        this.id = id;
        this.state = state;
        this.currentNodes = currentNodes;
        this.status = WorkflowStatus.RUNNING;
    }

    public UUID getId() { return id; }
    
    public WorkflowStatus getStatus() { return status; }
    public void setStatus(WorkflowStatus status) { this.status = status; }

    public SDLCState getState() { return state; }
    public void setState(SDLCState state) { this.state = state; }

    public List<NodeId> getCurrentNodes() { return currentNodes; }
    public void setCurrentNodes(List<NodeId> currentNodes) { this.currentNodes = currentNodes; }
}
