package com.charles.schwab.agentic.orchestrator.controller;

import com.charles.schwab.agentic.orchestrator.graph.engine.SDLCWorkflowEngine;
import com.charles.schwab.agentic.orchestrator.state.WorkflowRepository;
import com.charles.schwab.agentic.orchestrator.state.WorkflowSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/workflows")
@CrossOrigin(origins = "*") // For local Vite React dev server
public class WorkflowController {

    private final SDLCWorkflowEngine engine;
    private final WorkflowRepository repository;

    public WorkflowController(SDLCWorkflowEngine engine, WorkflowRepository repository) {
        this.engine = engine;
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> startWorkflow(@RequestBody Map<String, String> payload) {
        String requirements = payload.get("requirements");
        if (requirements == null || requirements.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Requirements cannot be empty"));
        }
        
        UUID id = engine.startWorkflow(requirements);
        return ResponseEntity.ok(Map.of("workflowId", id));
    }

    @GetMapping
    public Collection<WorkflowSession> getAllWorkflows() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkflowSession> getWorkflow(@PathVariable UUID id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<Map<String, String>> resumeWorkflow(@PathVariable UUID id, @RequestBody Map<String, String> payload) {
        String humanInput = payload.get("input");
        try {
            engine.resumeWorkflow(id, humanInput);
            return ResponseEntity.ok(Map.of("status", "Resumed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
