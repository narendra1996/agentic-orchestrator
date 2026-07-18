package com.charles.schwab.agentic.orchestrator.state;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class WorkflowRepository {
    private final ConcurrentHashMap<UUID, WorkflowSession> store = new ConcurrentHashMap<>();

    public void save(WorkflowSession session) {
        store.put(session.getId(), session);
    }

    public Optional<WorkflowSession> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    public Collection<WorkflowSession> findAll() {
        return store.values();
    }
}
