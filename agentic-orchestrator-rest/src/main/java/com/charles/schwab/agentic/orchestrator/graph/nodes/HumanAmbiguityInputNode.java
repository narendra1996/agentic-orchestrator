package com.charles.schwab.agentic.orchestrator.graph.nodes;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class HumanAmbiguityInputNode implements NodeHandler {
    private static final Logger log = LoggerFactory.getLogger(HumanAmbiguityInputNode.class);
    private final ChatClient chatClient;

    public HumanAmbiguityInputNode(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public NodeId getNodeId() { return NodeId.HUMAN_AMBIGUITY_INPUT; }
    @Override
    public SDLCState execute(SDLCState state) {
        log.info("PM/Human Input required to resolve ambiguity in requirements...");
        return state.withRawRequirements(state.rawRequirements() + "\nHuman clarification added.")
                    .withIsAmbiguous(false)
                    .addLog("Node [HUMAN_AMBIGUITY_INPUT]: Human resolved ambiguity.");
    }
}
