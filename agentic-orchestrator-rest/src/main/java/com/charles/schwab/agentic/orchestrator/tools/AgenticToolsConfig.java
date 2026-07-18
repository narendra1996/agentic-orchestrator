package com.charles.schwab.agentic.orchestrator.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

@Configuration
public class AgenticToolsConfig {

    private static final Logger log = LoggerFactory.getLogger(AgenticToolsConfig.class);

    public record CodebaseReaderRequest(String filePath) {}
    public record CodebaseReaderResponse(String content) {}

    @Bean
    public ToolCallback codebaseReaderTool() {
        Function<CodebaseReaderRequest, CodebaseReaderResponse> function = request -> {
            // Mock implementation: In a real scenario, this reads from the filesystem.
            log.info("Reading file: {}", request.filePath());
            return new CodebaseReaderResponse("Mocked content of " + request.filePath());
        };

        return FunctionToolCallback.builder("codebaseReader", function)
                .description("Reads existing files from the codebase for brownfield analysis.")
                .inputType(CodebaseReaderRequest.class)
                .build();
    }

    public record CompilerRequest(String sourceCode) {}
    public record CompilerResponse(boolean success, String output) {}

    @Bean
    public ToolCallback sandboxedCompilerTool() {
        Function<CompilerRequest, CompilerResponse> function = request -> {
            // Mock implementation: simulates running mvn compile.
            log.info("Compiling source code snippet...");
            boolean success = !request.sourceCode().contains("SYNTAX_ERROR");
            return new CompilerResponse(success, success ? "BUILD SUCCESS" : "BUILD FAILURE: Syntax error detected.");
        };

        return FunctionToolCallback.builder("sandboxedCompiler", function)
                .description("Compiles generated code in a sandboxed environment to verify correctness.")
                .inputType(CompilerRequest.class)
                .build();
    }
}
