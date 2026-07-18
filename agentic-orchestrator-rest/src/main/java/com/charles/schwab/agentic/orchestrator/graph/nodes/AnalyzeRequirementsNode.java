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
public class AnalyzeRequirementsNode implements NodeHandler {
    private static final Logger log = LoggerFactory.getLogger(AnalyzeRequirementsNode.class);
    private final ChatClient chatClient;
    private final AgenticProperties properties;
    
    @Value("classpath:/prompts/analyze_requirements.st")
    private Resource systemPrompt;

    public AnalyzeRequirementsNode(ChatClient chatClient, AgenticProperties properties) {
        this.chatClient = chatClient;
        this.properties = properties;
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.ANALYZE_REQUIREMENTS;
    }

    @Override
    public SDLCState execute(SDLCState state) {
        
        String response = chatClient.prompt()
            .system(systemPrompt)
            .user("Analyze these requirements. Are they ambiguous? " + state.rawRequirements())
            .call().content();
        boolean isAmbiguous = response != null && response.toLowerCase().contains("ambiguous");
        state.decomposedTasks().add(response);
        return state.withIsAmbiguous(isAmbiguous).addLog("Node [ANALYZE_REQUIREMENTS]: Analyzed requirements.");
        
    }
}
