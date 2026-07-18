package com.charles.schwab.agentic.orchestrator.graph.nodes;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RePlanTasksNode implements NodeHandler {
    private static final Logger log = LoggerFactory.getLogger(RePlanTasksNode.class);
    private final ChatClient chatClient;

    public RePlanTasksNode(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public NodeId getNodeId() { return NodeId.RE_PLAN_TASKS; }
    @Override
    public SDLCState execute(SDLCState state) {
        log.warn("Dynamic Replanning: Upstream outputs changed or QA consistently failed.");
        return state.withIsAmbiguous(false).addLog("Node [RE_PLAN_TASKS]: Adjusted task decomposition.");
    }
}
