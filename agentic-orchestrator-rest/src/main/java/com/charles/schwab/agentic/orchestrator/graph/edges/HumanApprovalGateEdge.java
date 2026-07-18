package com.charles.schwab.agentic.orchestrator.graph.edges;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HumanApprovalGateEdge implements EdgeRouter {

    @Override
    public NodeId getSourceNode() {
        return NodeId.HUMAN_APPROVAL_GATE;
    }

    @Override
    public List<NodeId> route(SDLCState state) {
        return List.of(NodeId.WRITE_BACKEND_CODE, NodeId.WRITE_FRONTEND_CODE);
    }
}
