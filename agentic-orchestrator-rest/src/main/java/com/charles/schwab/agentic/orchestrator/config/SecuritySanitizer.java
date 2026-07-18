package com.charles.schwab.agentic.orchestrator.config;

import com.charles.schwab.agentic.orchestrator.exception.OrchestrationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SecuritySanitizer {

    private final AgenticProperties properties;

    // Common prompt injection attack signatures
    private static final List<String> BLACKLISTED_PATTERNS = List.of(
        "ignore previous instructions",
        "system:",
        "you are now",
        "bypass",
        "disregard all prior",
        "forget everything"
    );

    public SecuritySanitizer(AgenticProperties properties) {
        this.properties = properties;
    }

    public void sanitizeInput(String input) {
        if (!properties.isSanitizeInput()) return;

        if (input == null || input.trim().isEmpty()) {
            throw new OrchestrationException("Input requirements cannot be empty.");
        }

        String lowerInput = input.toLowerCase();
        for (String pattern : BLACKLISTED_PATTERNS) {
            if (lowerInput.contains(pattern)) {
                throw new OrchestrationException("SECURITY ALERT: Malicious prompt injection detected. Workflow halted.");
            }
        }
    }
}
