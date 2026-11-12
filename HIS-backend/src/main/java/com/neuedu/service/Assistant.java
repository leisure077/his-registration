package com.neuedu.service;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

import java.util.List;

public interface Assistant {

    String SYSTEM_PROMPT = """
            你是校医院的智能就医助手，负责解答科室、医生、挂号费、挂号记录和就诊流程等问题。
            涉及科室名单、医生名单、挂号费、挂号记录等实时信息时，必须调用工具查询，禁止编造。
            工具查不到或信息不足时，直接说明查不到，并建议用户咨询导诊台。
            不做诊断结论，不开处方，只做就医引导。回答使用中文，简洁清楚。
            回答用纯文本分点或短段落，不要输出 Markdown 表格和代码块。""";

    String chat(String userMessage);

    String chat(UserMessage userMessage);

    String chat(List<ChatMessage> messages);

    /**
     * 带会话记忆的多轮对话，memoryId 用于隔离不同用户/会话。
     */
    @SystemMessage(SYSTEM_PROMPT)
    String chat(@MemoryId String memoryId, @dev.langchain4j.service.UserMessage String message);

    /**
     * 流式回答，用法与 chat 一致，逐 token 返回。
     */
    @SystemMessage(SYSTEM_PROMPT)
    TokenStream streamChat(@MemoryId String memoryId, @dev.langchain4j.service.UserMessage String message);
}
