package com.charles.schwab.agentic.orchestrator.graph.edges;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RollbackEdge implements EdgeRouter {

    @Override
    public NodeId getSourceNode() {
        return NodeId.ROLLBACK;
    }

    @Override
    public List<NodeId> route(SDLCState state) {
        return List.of(NodeId.RE_PLAN_TASKS);
    }
}
