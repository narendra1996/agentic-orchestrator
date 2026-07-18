package com.charles.schwab.agentic.orchestrator.graph.nodes;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SynthesizeSummaryNode implements NodeHandler {
    private static final Logger log = LoggerFactory.getLogger(SynthesizeSummaryNode.class);
    private final ChatClient chatClient;

    public SynthesizeSummaryNode(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public NodeId getNodeId() { return NodeId.SYNTHESIZE_SUMMARY; }
    @Override
    public SDLCState execute(SDLCState state) {
        log.info("Synthesizing final summary of the execution...");
        return state.withFinalSummary("Success. MTTR: " + state.latencyMs() + "ms").addLog("Node [SYNTHESIZE_SUMMARY]: Summary generated.");
    }
}
