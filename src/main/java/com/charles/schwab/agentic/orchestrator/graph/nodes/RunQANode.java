package com.charles.schwab.agentic.orchestrator.graph.nodes;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RunQANode implements NodeHandler {
    private static final Logger log = LoggerFactory.getLogger(RunQANode.class);
    private final ChatClient chatClient;

    public RunQANode(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public NodeId getNodeId() { return NodeId.RUN_QA; }
    @Override
    public SDLCState execute(SDLCState state) {
        boolean qaPassed = !state.codeArtifacts().toString().contains("QA_FAILURE");
        if (qaPassed) {
            state.qaResults().add("QA Passed on attempt " + (state.retryCount() + 1));
            return state.withQaPassed(true).addLog("Node [RUN_QA]: QA passed.");
        } else {
            return state.withQaPassed(false).incrementRetry("Node [RUN_QA]: QA Validation failed.");
        }
    }
}
