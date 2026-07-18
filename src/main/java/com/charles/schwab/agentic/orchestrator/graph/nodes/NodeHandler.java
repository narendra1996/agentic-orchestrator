package com.charles.schwab.agentic.orchestrator.graph.nodes;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;

public interface NodeHandler {
    NodeId getNodeId();
    SDLCState execute(SDLCState state);
}
