package com.charles.schwab.agentic.orchestrator.graph.nodes;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;
import com.charles.schwab.agentic.orchestrator.config.AgenticProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class WriteFrontendCodeNode implements NodeHandler {
    private static final Logger log = LoggerFactory.getLogger(WriteFrontendCodeNode.class);
    private final ChatClient chatClient;
    private final AgenticProperties properties;
    
    @Value("classpath:/prompts/write_frontend.st")
    private Resource systemPrompt;

    public WriteFrontendCodeNode(ChatClient chatClient, AgenticProperties properties) {
        this.chatClient = chatClient;
        this.properties = properties;
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.WRITE_FRONTEND_CODE;
    }

    @Override
    public SDLCState execute(SDLCState state) {
        
        String code = chatClient.prompt()
            .system(systemPrompt)
            .user("Implement frontend code based on Architecture: " + state.codeArtifacts().get("Architecture.md"))
            .call().content();
        state.codeArtifacts().put("Frontend.jsx", code);
        return state.addLog("Node [WRITE_FRONTEND_CODE]: Frontend code generated.");
        
    }
}
