import os

base_pkg = "com.charles.schwab.agentic.orchestrator"
base_dir = "/Users/narendra/IdeaProjects/agentic-orchestrator/src/main/java/com/charles/schwab/agentic/orchestrator"

exceptions = {
    "OrchestrationException": "public class OrchestrationException extends RuntimeException {\n    public OrchestrationException(String message) { super(message); }\n    public OrchestrationException(String message, Throwable cause) { super(message, cause); }\n}",
    "SafeStopTriggeredException": "public class SafeStopTriggeredException extends OrchestrationException {\n    public SafeStopTriggeredException(String message) { super(message); }\n}"
}

interfaces = {
    "graph/nodes/NodeHandler": "import com.charles.schwab.agentic.orchestrator.graph.NodeId;\nimport com.charles.schwab.agentic.orchestrator.state.SDLCState;\n\npublic interface NodeHandler {\n    NodeId getNodeId();\n    SDLCState execute(SDLCState state);\n}",
    "graph/edges/EdgeRouter": "import com.charles.schwab.agentic.orchestrator.graph.NodeId;\nimport com.charles.schwab.agentic.orchestrator.state.SDLCState;\nimport java.util.List;\n\npublic interface EdgeRouter {\n    NodeId getSourceNode();\n    List<NodeId> route(SDLCState state);\n}"
}

os.makedirs(os.path.join(base_dir, "exception"), exist_ok=True)
os.makedirs(os.path.join(base_dir, "graph", "nodes"), exist_ok=True)
os.makedirs(os.path.join(base_dir, "graph", "edges"), exist_ok=True)

for name, content in exceptions.items():
    with open(os.path.join(base_dir, "exception", f"{name}.java"), "w") as f:
        f.write(f"package {base_pkg}.exception;\n\n{content}\n")

for name, content in interfaces.items():
    with open(os.path.join(base_dir, f"{name}.java"), "w") as f:
        pkg = name.split('/')[0] + "." + name.split('/')[1]
        f.write(f"package {base_pkg}.{pkg};\n\n{content}\n")

nodes = [
    "AnalyzeRequirements", "HumanAmbiguityInput", "BrownfieldImpactAnalysis",
    "DesignArchitecture", "HumanApprovalGate", "WriteBackendCode",
    "WriteFrontendCode", "SecurityComplianceAudit", "RunQA",
    "RePlanTasks", "SafeStop", "SynthesizeSummary"
]

for node in nodes:
    content = f"""package {base_pkg}.graph.nodes;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class {node}Node implements NodeHandler {{
    private static final Logger log = LoggerFactory.getLogger({node}Node.class);
    private final ChatClient chatClient;

    public {node}Node(ChatClient chatClient) {{
        this.chatClient = chatClient;
    }}

    @Override
    public NodeId getNodeId() {{
        return NodeId.valueOf(camelToSnake("{node}"));
    }}

    @Override
    public SDLCState execute(SDLCState state) {{
        // TODO: Move implementation here
        return state;
    }}

    private String camelToSnake(String str) {{
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toUpperCase();
    }}
}}
"""
    with open(os.path.join(base_dir, "graph", "nodes", f"{node}Node.java"), "w") as f:
        f.write(content)

edges = [
    "AnalyzeRequirements", "HumanAmbiguityInput", "BrownfieldImpactAnalysis",
    "DesignArchitecture", "HumanApprovalGate", "WriteBackendCode",
    "WriteFrontendCode", "SecurityComplianceAudit", "RunQA", "Rollback",
    "RePlanTasks", "SafeStop", "SynthesizeSummary"
]

for edge in edges:
    content = f"""package {base_pkg}.graph.edges;

import com.charles.schwab.agentic.orchestrator.graph.NodeId;
import com.charles.schwab.agentic.orchestrator.state.SDLCState;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class {edge}Edge implements EdgeRouter {{

    @Override
    public NodeId getSourceNode() {{
        return NodeId.valueOf(camelToSnake("{edge}"));
    }}

    @Override
    public List<NodeId> route(SDLCState state) {{
        // TODO: Move implementation here
        return List.of(NodeId.END);
    }}

    private String camelToSnake(String str) {{
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toUpperCase();
    }}
}}
"""
    with open(os.path.join(base_dir, "graph", "edges", f"{edge}Edge.java"), "w") as f:
        f.write(content)

print("Scaffolded successfully")
