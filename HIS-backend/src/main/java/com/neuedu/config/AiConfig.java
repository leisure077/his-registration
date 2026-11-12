package com.neuedu.config;

import com.neuedu.chat.RedisChatMemoryStore;
import com.neuedu.service.Assistant;
import com.neuedu.tool.HospitalTool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 助手装配：模型 + 按会话隔离的 Redis 记忆 + 只读业务工具。
 */
@Configuration
public class AiConfig {

    @Resource
    private ChatModel chatModel;

    @Resource
    private StreamingChatModel streamingChatModel;

    @Resource
    private RedisChatMemoryStore chatMemoryStore;

    @Resource
    private HospitalTool hospitalTool;

    @Bean
    public Assistant assistant() {
        return AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(20)
                        .chatMemoryStore(chatMemoryStore)
                        .build())
                .tools(hospitalTool)
                .build();
    }
}
