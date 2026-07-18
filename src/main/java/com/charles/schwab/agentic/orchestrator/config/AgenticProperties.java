package com.charles.schwab.agentic.orchestrator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "agentic.orchestrator")
public class AgenticProperties {
    
    private int maxTokens = 2000;
    private int maxDagIterations = 50;
    private boolean sanitizeInput = true;

    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }

    public int getMaxDagIterations() { return maxDagIterations; }
    public void setMaxDagIterations(int maxDagIterations) { this.maxDagIterations = maxDagIterations; }

    public boolean isSanitizeInput() { return sanitizeInput; }
    public void setSanitizeInput(boolean sanitizeInput) { this.sanitizeInput = sanitizeInput; }
}
