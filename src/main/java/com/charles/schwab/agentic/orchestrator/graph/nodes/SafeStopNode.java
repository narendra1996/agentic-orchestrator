package com.charles.schwab.agentic.orchestrator.graph.nodes;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SafeStopNode implements NodeHandler {
    private static final Logger log = LoggerFactory.getLogger(SafeStopNode.class);
    private final ChatClient chatClient;

    public SafeStopNode(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public NodeId getNodeId() { return NodeId.SAFE_STOP; }
    @Override
    public SDLCState execute(SDLCState state) {
        log.error("SAFE-STOP TRIGGERED: Halting execution due to excessive QA failures.");
        return state.addLog("Node [SAFE_STOP]: Triggered after multiple failures.");
    }
}
