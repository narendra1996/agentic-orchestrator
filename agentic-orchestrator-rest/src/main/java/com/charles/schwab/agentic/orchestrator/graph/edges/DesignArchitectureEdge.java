package com.charles.schwab.agentic.orchestrator.graph.edges;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DesignArchitectureEdge implements EdgeRouter {

    @Override
    public NodeId getSourceNode() {
        return NodeId.DESIGN_ARCHITECTURE;
    }

    @Override
    public List<NodeId> route(SDLCState state) {
        return List.of(NodeId.HUMAN_APPROVAL_GATE);
    }
}
