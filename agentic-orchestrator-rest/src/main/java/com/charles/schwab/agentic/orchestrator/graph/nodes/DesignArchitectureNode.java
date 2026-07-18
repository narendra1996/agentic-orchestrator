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
public class DesignArchitectureNode implements NodeHandler {
    private static final Logger log = LoggerFactory.getLogger(DesignArchitectureNode.class);
    private final ChatClient chatClient;
    private final AgenticProperties properties;
    
    @Value("classpath:/prompts/design_architecture.st")
    private Resource systemPrompt;

    public DesignArchitectureNode(ChatClient chatClient, AgenticProperties properties) {
        this.chatClient = chatClient;
        this.properties = properties;
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.DESIGN_ARCHITECTURE;
    }

    @Override
    public SDLCState execute(SDLCState state) {
        
        String response = chatClient.prompt()
            .system(systemPrompt)
            .user("Create an architectural design for tasks: " + state.decomposedTasks())
            .call().content();
        state.codeArtifacts().put("Architecture.md", response);
        return state.addLog("Node [DESIGN_ARCHITECTURE]: Designed architecture artifacts.");
        
    }
}
