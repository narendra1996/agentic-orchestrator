# Agentic Orchestrator

Agentic Orchestrator is a robust, stateful, and non-linear Agentic Software Engineering framework designed to automate the Software Development Life Cycle (SDLC). By leveraging AI agents, the orchestrator systematically navigates through requirements analysis, architectural design, implementation, and Quality Assurance (QA). 

Built on a Directed Acyclic Graph (DAG) state machine model, it provides resilient execution with human-in-the-loop (HITL) checkpoints, bounded retry mechanisms, parallel execution of tasks, and failure-driven state transitions.

## 🚀 Tech Stack

*   **Language**: Java 21
*   **Framework**: Spring Boot 4.1.0
*   **AI Integration**: Spring AI 2.0.0
*   **Frontend**: React + Vite + TypeScript
*   **Build Tool**: Maven (Multi-Module)
*   **Database**: H2 (In-Memory Database for local testing/state persistence)

## 🏗️ Project Architecture

This is a Maven Multi-Module project cleanly separating the domain logic, frontend assets, and packaging configurations.

*   **`agentic-orchestrator-rest`**: The core backend domain module. Contains all the Java code for the SDLC orchestration framework, including Node Handlers, Edge Routers, the `SDLCWorkflowEngine`, Spring AI 2.0 Advisors, and explicit MCP/ToolCallback registrations.
*   **`agentic-orchestrator-ui`**: The frontend Vite + React application. Configured with the `frontend-maven-plugin` to automatically download Node.js, run `npm install`, and build the static assets into `dist/`.
*   **`agentic-orchestrator-app`**: The executable entry point. This module depends on both `rest` and `ui`. It uses the `spring-boot-maven-plugin` to create a fat jar that simultaneously serves the REST APIs and the compiled React UI on port `8080`.

## ⚙️ Core Concepts

### Stateful DAG Orchestration
The framework replaces simple linear prompt chains with an explicit dependency graph (`SDLCWorkflowEngine`). State is passed and mutated seamlessly across nodes.

*   **`NodeHandler`**: Implementations (e.g., `AnalyzeRequirementsNode`, `DesignArchitectureNode`, `RunQANode`) execute specific discrete tasks using the Spring AI `ChatClient`.
*   **`EdgeRouter`**: Evaluates conditions (e.g., "Did QA pass?") and determines the next node to execute.

### Resiliency & Governance
*   **Human-In-The-Loop (HITL)**: Execution pauses at specific gates (e.g., `HUMAN_APPROVAL_GATE`, `HUMAN_AMBIGUITY_INPUT`) to wait for explicit human approval or clarification before resuming.
*   **Safe-Stop Controls & Bounded Retries**: Incorporates fallback logic that safely halts the execution (`SAFE_STOP`) if self-correction loops fail consecutively beyond a configured threshold.
*   **Context Preservation**: The `SDLCState` object acts as the system's memory, tracking requirements, code artifacts, QA results, and a comprehensive audit log.

## 🛠️ Getting Started

### Prerequisites
*   Java 21 installed.
*   Maven installed.
*   (Node.js and npm are *not* strictly required to be pre-installed globally; the `frontend-maven-plugin` will handle this automatically during the build phase.)

### Building the Project

Navigate to the root directory and run the following Maven command. This will compile the backend, download Node.js, build the React frontend, and package everything into an executable fat jar.

```bash
./mvnw clean install
```

### Running the Application

After a successful build, you can run the Spring Boot application using the generated jar located in the `app` module:

```bash
java -jar agentic-orchestrator-app/target/agentic-orchestrator-app-0.0.1-SNAPSHOT.jar
```

Once started, the application will be accessible at:
*   **Frontend UI & API**: `http://localhost:8080`

## 🧠 Spring AI 2.0 Integration

The orchestrator leverages the latest Spring AI 2.0 GA API standards:
*   Utilizes the `ChatClient` builder pattern for fluent prompt construction.
*   Implements `CallAdvisor` and recursive advising chains for resilient iterative processes (like the QA Fix loops).
*   Manages explicit tool execution via `ToolCallback` beans mapped directly to the `ChatClient`.
