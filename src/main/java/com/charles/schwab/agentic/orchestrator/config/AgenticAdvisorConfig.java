package com.charles.schwab.agentic.orchestrator.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class AgenticAdvisorConfig {

    @Bean
    public ChatClient.Builder chatClientBuilder() {
        ChatModel dummyModel = new ChatModel() {
            @Override
            public ChatResponse call(Prompt prompt) {
                return new ChatResponse(List.of());
            }
            @Override
            public ChatOptions getDefaultOptions() {
                return null;
            }
        };
        return ChatClient.builder(dummyModel);
    }

    /**
     * Configures the primary ChatClient for the orchestrator.
     * It registers the tools explicitly using the Spring AI 2.0 Builder approach.
     */
    @Bean
    public ChatClient agenticChatClient(ChatClient.Builder builder,
                                        List<ToolCallback> toolCallbacks,
                                        RecursiveQAAdvisor recursiveQAAdvisor) {
        return builder
                .defaultAdvisors(recursiveQAAdvisor)
                .defaultTools(toolCallbacks.toArray())
                .build();
    }

    /**
     * A recursive advisor that intercepts the AI response.
     * If the response indicates a QA failure, it recursively invokes the LLM
     * to fix the code, bounded by a maximum retry count.
     */
    @Bean
    public RecursiveQAAdvisor recursiveQAAdvisor() {
        return new RecursiveQAAdvisor(3); // Max retries = 3
    }

    public static class RecursiveQAAdvisor implements CallAdvisor {
        private static final Logger log = LoggerFactory.getLogger(RecursiveQAAdvisor.class);
        private final int maxRetries;

        public RecursiveQAAdvisor(int maxRetries) {
            this.maxRetries = maxRetries;
        }

        @Override
        public String getName() {
            return "RecursiveQAAdvisor";
        }

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
            ChatClientResponse response = chain.nextCall(request);
            int attempts = 0;

            // Iterative QA Failure -> Fix Code loop
            while (hasQaFailure(response) && attempts < maxRetries) {
                attempts++;
                log.warn("QA Failure detected. Attempting fix... (Retry {}/{})", attempts, maxRetries);
                
                // Construct a new request asking the LLM to fix the specific QA issues
                ChatClientRequest fixRequest = buildFixRequest(request);

                // Recursively call the chain with the new context
                response = chain.nextCall(fixRequest);
            }

            if (hasQaFailure(response) && attempts >= maxRetries) {
                log.error("Max retries reached. Safe-stop triggered.");
                // In a real scenario, this might append metadata indicating safe-stop.
            }

            return response;
        }

        private ChatClientRequest buildFixRequest(ChatClientRequest request) {
            List<Message> originalMessages = request.prompt().getInstructions();
            List<Message> mutatedMessages = new ArrayList<>();
            for (Message msg : originalMessages) {
                if (msg instanceof UserMessage) {
                    mutatedMessages.add(new UserMessage(msg.getText() + 
                            "\n\nPrevious attempt failed QA. Please fix the syntax or logical errors and regenerate the code."));
                } else {
                    mutatedMessages.add(msg);
                }
            }
            Prompt mutatedPrompt = new Prompt(mutatedMessages, request.prompt().getOptions());
            return request.mutate()
                    .prompt(mutatedPrompt)
                    .build();
        }

        private boolean hasQaFailure(ChatClientResponse response) {
            // Mock logic: check if the response content contains indicators of QA failure.
            String content = response.chatResponse().getResult().getOutput().getText();
            return content != null && content.contains("QA_FAILURE");
        }
    }
}
