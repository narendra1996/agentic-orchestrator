package com.charles.schwab.agentic.orchestrator.graph.edges;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AnalyzeRequirementsEdge implements EdgeRouter {

    @Override
    public NodeId getSourceNode() {
        return NodeId.ANALYZE_REQUIREMENTS;
    }

    @Override
    public List<NodeId> route(SDLCState state) {
        return List.of(state.isAmbiguous() ? NodeId.HUMAN_AMBIGUITY_INPUT : NodeId.BROWNFIELD_IMPACT_ANALYSIS);
    }
}
