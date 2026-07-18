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
public class WriteBackendCodeNode implements NodeHandler {
    private static final Logger log = LoggerFactory.getLogger(WriteBackendCodeNode.class);
    private final ChatClient chatClient;
    private final AgenticProperties properties;
    
    @Value("classpath:/prompts/write_backend.st")
    private Resource systemPrompt;

    public WriteBackendCodeNode(ChatClient chatClient, AgenticProperties properties) {
        this.chatClient = chatClient;
        this.properties = properties;
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.WRITE_BACKEND_CODE;
    }

    @Override
    public SDLCState execute(SDLCState state) {
        
        String code = chatClient.prompt()
            .system(systemPrompt)
            .user("Implement backend code based on Architecture: " + state.codeArtifacts().get("Architecture.md"))
            .call().content();
        state.codeArtifacts().put("Backend.java", code);
        return state.addLog("Node [WRITE_BACKEND_CODE]: Backend code generated.");
        
    }
}
