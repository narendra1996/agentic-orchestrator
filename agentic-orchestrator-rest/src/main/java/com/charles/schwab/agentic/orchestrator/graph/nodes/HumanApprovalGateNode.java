package com.charles.schwab.agentic.orchestrator.graph.nodes;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class HumanApprovalGateNode implements NodeHandler {
    private static final Logger log = LoggerFactory.getLogger(HumanApprovalGateNode.class);
    private final ChatClient chatClient;

    public HumanApprovalGateNode(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public NodeId getNodeId() { return NodeId.HUMAN_APPROVAL_GATE; }
    @Override
    public SDLCState execute(SDLCState state) {
        log.info("Waiting for human approval on Architecture.md...");
        return state.takeSnapshot().addLog("Node [HUMAN_APPROVAL_GATE]: Architecture approved. Snapshot taken before coding.");
    }
}
