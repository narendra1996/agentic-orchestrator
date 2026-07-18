package com.charles.schwab.agentic.orchestrator.graph.edges;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RunQAEdge implements EdgeRouter {

    @Override
    public NodeId getSourceNode() {
        return NodeId.RUN_QA;
    }

    @Override
    public List<NodeId> route(SDLCState state) {
        if (state.qaPassed()) { return List.of(NodeId.SYNTHESIZE_SUMMARY); } else if (state.retryCount() >= 3) { return List.of(NodeId.ROLLBACK); } else { return List.of(NodeId.WRITE_BACKEND_CODE, NodeId.WRITE_FRONTEND_CODE); }
    }
}
