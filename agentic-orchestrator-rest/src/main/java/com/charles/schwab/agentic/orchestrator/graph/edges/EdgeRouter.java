package com.charles.schwab.agentic.orchestrator.graph.edges;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;
import java.util.List;

public interface EdgeRouter {
    NodeId getSourceNode();
    List<NodeId> route(SDLCState state);
}
