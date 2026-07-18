package com.charles.schwab.agentic.orchestrator.graph.edges;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WriteBackendCodeEdge implements EdgeRouter {

    @Override
    public NodeId getSourceNode() {
        return NodeId.WRITE_BACKEND_CODE;
    }

    @Override
    public List<NodeId> route(SDLCState state) {
        return List.of(NodeId.END); // Join handled by engine
    }
}
