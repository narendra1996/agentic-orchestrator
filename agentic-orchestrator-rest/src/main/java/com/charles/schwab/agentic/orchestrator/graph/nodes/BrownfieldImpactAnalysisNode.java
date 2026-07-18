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
public class BrownfieldImpactAnalysisNode implements NodeHandler {
    private static final Logger log = LoggerFactory.getLogger(BrownfieldImpactAnalysisNode.class);
    private final ChatClient chatClient;
    private final AgenticProperties properties;
    
    @Value("classpath:/prompts/brownfield_impact.st")
    private Resource systemPrompt;

    public BrownfieldImpactAnalysisNode(ChatClient chatClient, AgenticProperties properties) {
        this.chatClient = chatClient;
        this.properties = properties;
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.BROWNFIELD_IMPACT_ANALYSIS;
    }

    @Override
    public SDLCState execute(SDLCState state) {
        
        log.info("Performing Codebase Reasoning using tools...");
        String impact = chatClient.prompt()
            .system(systemPrompt)
            .user("Analyze impact of: " + state.decomposedTasks().toString())
            .call().content();
        state.codeArtifacts().put("ImpactAnalysis.md", impact);
        return state.addLog("Node [BROWNFIELD_IMPACT_ANALYSIS]: Completed impact analysis.");
        
    }
}
