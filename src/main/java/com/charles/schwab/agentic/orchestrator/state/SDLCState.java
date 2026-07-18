package com.charles.schwab.agentic.orchestrator.state;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * State Management Object for the SDLC workflow orchestration.
 * This record tracks cross-stage context for the agentic software engineering system.
 */
public record SDLCState(
    String rawRequirements,
    boolean isAmbiguous,
    List<String> decomposedTasks,
    Map<String, String> codeArtifacts, // e.g. "SDLCState.java" -> "package com..."
    List<String> qaResults,
    boolean qaPassed,
    String finalSummary,
    int retryCount,
    int rollbackCount,
    int dagIterationCount,
    long startTimeMs,
    long latencyMs,
    SDLCState previousSnapshot,
    List<String> auditLog
) {
    /**
     * Creates an initial empty state from raw requirements.
     */
    public static SDLCState initialState(String requirements) {
        return new SDLCState(
            requirements,
            false,
            new ArrayList<>(),
            new HashMap<>(),
            new ArrayList<>(),
            false,
            null,
            0,
            0,
            0,
            System.currentTimeMillis(),
            0,
            null,
            new ArrayList<>(List.of("Initialized state with requirements."))
        );
    }

    /**
     * Helper to increment the retry count and log it.
     */
    public SDLCState incrementRetry(String reason) {
        List<String> newAuditLog = new ArrayList<>(this.auditLog);
        newAuditLog.add("Incrementing retry count due to: " + reason);
        return new SDLCState(
            this.rawRequirements,
            this.isAmbiguous,
            this.decomposedTasks,
            this.codeArtifacts,
            this.qaResults,
            this.qaPassed,
            this.finalSummary,
            this.retryCount + 1,
            this.rollbackCount,
            this.dagIterationCount + 1,
            this.startTimeMs,
            System.currentTimeMillis() - this.startTimeMs,
            this.previousSnapshot,
            newAuditLog
        );
    }

    /**
     * Helper to add a new audit log entry.
     */
    public SDLCState addLog(String message) {
        List<String> newAuditLog = new ArrayList<>(this.auditLog);
        newAuditLog.add(message);
        return new SDLCState(
            this.rawRequirements,
            this.isAmbiguous,
            this.decomposedTasks,
            this.codeArtifacts,
            this.qaResults,
            this.qaPassed,
            this.finalSummary,
            this.retryCount,
            this.rollbackCount,
            this.dagIterationCount + 1,
            this.startTimeMs,
            System.currentTimeMillis() - this.startTimeMs,
            this.previousSnapshot,
            newAuditLog
        );
    }

    /**
     * Builder-like copy methods for modifying state fields immutably.
     */
    public SDLCState withIsAmbiguous(boolean isAmbiguous) {
        return new SDLCState(this.rawRequirements, isAmbiguous, this.decomposedTasks, this.codeArtifacts, this.qaResults, this.qaPassed, this.finalSummary, this.retryCount, this.rollbackCount, this.dagIterationCount, this.startTimeMs, System.currentTimeMillis() - this.startTimeMs, this.previousSnapshot, this.auditLog);
    }

    public SDLCState withRawRequirements(String rawRequirements) {
        return new SDLCState(rawRequirements, this.isAmbiguous, this.decomposedTasks, this.codeArtifacts, this.qaResults, this.qaPassed, this.finalSummary, this.retryCount, this.rollbackCount, this.dagIterationCount, this.startTimeMs, System.currentTimeMillis() - this.startTimeMs, this.previousSnapshot, this.auditLog);
    }

    public SDLCState withQaPassed(boolean qaPassed) {
        return new SDLCState(this.rawRequirements, this.isAmbiguous, this.decomposedTasks, this.codeArtifacts, this.qaResults, qaPassed, this.finalSummary, this.retryCount, this.rollbackCount, this.dagIterationCount, this.startTimeMs, System.currentTimeMillis() - this.startTimeMs, this.previousSnapshot, this.auditLog);
    }

    public SDLCState withFinalSummary(String finalSummary) {
        return new SDLCState(this.rawRequirements, this.isAmbiguous, this.decomposedTasks, this.codeArtifacts, this.qaResults, this.qaPassed, finalSummary, this.retryCount, this.rollbackCount, this.dagIterationCount, this.startTimeMs, System.currentTimeMillis() - this.startTimeMs, this.previousSnapshot, this.auditLog);
    }

    public SDLCState takeSnapshot() {
        // Creates a new state where previousSnapshot points to this current instance
        return new SDLCState(this.rawRequirements, this.isAmbiguous, new ArrayList<>(this.decomposedTasks), new HashMap<>(this.codeArtifacts), new ArrayList<>(this.qaResults), this.qaPassed, this.finalSummary, 0, this.rollbackCount, this.dagIterationCount, this.startTimeMs, System.currentTimeMillis() - this.startTimeMs, this, new ArrayList<>(this.auditLog)).addLog("Snapshot taken.");
    }

    public SDLCState rollback() {
        if (this.previousSnapshot == null) {
            return this; // Cannot rollback
        }
        // Restore from previous snapshot but increment rollback count and keep audit log
        List<String> newAuditLog = new ArrayList<>(this.auditLog);
        newAuditLog.add("Rolled back to previous snapshot.");
        return new SDLCState(
            previousSnapshot.rawRequirements(),
            previousSnapshot.isAmbiguous(),
            new ArrayList<>(previousSnapshot.decomposedTasks()),
            new HashMap<>(previousSnapshot.codeArtifacts()),
            new ArrayList<>(previousSnapshot.qaResults()),
            previousSnapshot.qaPassed(),
            previousSnapshot.finalSummary(),
            0,
            this.rollbackCount + 1,
            this.dagIterationCount + 1,
            this.startTimeMs,
            System.currentTimeMillis() - this.startTimeMs,
            previousSnapshot.previousSnapshot(),
            newAuditLog
        );
    }
}
