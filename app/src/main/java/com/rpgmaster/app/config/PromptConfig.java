package com.rpgmaster.app.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * Loads prompt templates from {@code src/main/resources/prompts/}.
 * Never hardcode prompts in Java — .st files allow prompt tuning without recompiling.
 */
@Configuration
public class PromptConfig {

    /**
     * RAG system prompt — defines the LLM's persona and constraints.
     * Source: {@code resources/prompts/rag-system.st}
     */
    @Bean
    public String ragSystemPrompt() throws IOException {
        return new ClassPathResource("prompts/rag-system.st")
                .getContentAsString(StandardCharsets.UTF_8);
    }
}
