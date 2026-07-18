package com.charles.schwab.agentic.orchestrator.graph.edges;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HumanAmbiguityInputEdge implements EdgeRouter {

    @Override
    public NodeId getSourceNode() {
        return NodeId.HUMAN_AMBIGUITY_INPUT;
    }

    @Override
    public List<NodeId> route(SDLCState state) {
        return List.of(NodeId.ANALYZE_REQUIREMENTS);
    }
}
