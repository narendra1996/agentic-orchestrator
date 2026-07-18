package com.charles.schwab.agentic.orchestrator.exception;

public class OrchestrationException extends RuntimeException {
    public OrchestrationException(String message) { super(message); }
    public OrchestrationException(String message, Throwable cause) { super(message, cause); }
}
