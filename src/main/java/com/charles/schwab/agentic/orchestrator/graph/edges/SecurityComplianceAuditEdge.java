package com.charles.schwab.agentic.orchestrator.graph.edges;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SecurityComplianceAuditEdge implements EdgeRouter {

    @Override
    public NodeId getSourceNode() {
        return NodeId.SECURITY_COMPLIANCE_AUDIT;
    }

    @Override
    public List<NodeId> route(SDLCState state) {
        return List.of(NodeId.RUN_QA);
    }
}
