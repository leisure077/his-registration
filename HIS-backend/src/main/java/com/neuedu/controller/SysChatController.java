package com.neuedu.controller;

import com.neuedu.chat.RedisChatMemoryStore;
import com.neuedu.service.Assistant;
import com.neuedu.vo.Result;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * AI 就医助手接口。
 */
@RestController
@RequestMapping("/sysChat")
public class SysChatController {

    private static final Logger log = LoggerFactory.getLogger(SysChatController.class);

    @Resource
    private Assistant assistant;

    @Resource
    private RedisChatMemoryStore chatMemoryStore;

    /**
     * 多轮对话，memoryId 由前端生成并保持同一次会话不变。
     */
    @PostMapping("chat")
    public Result chat(String memoryId, String message) {
        if (StringUtils.isBlank(memoryId)) {
            return Result.fail("会话ID不能为空");
        }
        if (StringUtils.isBlank(message)) {
            return Result.fail("提问内容不能为空");
        }
        try {
            return Result.success(assistant.chat(memoryId, message), "回答成功");
        } catch (Exception e) {
            return Result.fail("AI 服务暂时不可用：" + e.getMessage());
        }
    }

    /**
     * 流式回答，SSE 推送：content 为增量文本，error 表示失败，流结束即回答完成。
     */
    @PostMapping(value = "stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(String memoryId, String message) {
        SseEmitter emitter = new SseEmitter(300000L);
        if (StringUtils.isBlank(memoryId) || StringUtils.isBlank(message)) {
            sendEvent(emitter, Map.of("error", "会话ID和提问内容不能为空"));
            emitter.complete();
            return emitter;
        }
        assistant.streamChat(memoryId, message)
                .onPartialResponse(token -> {
                    if (StringUtils.isNotEmpty(token)) {
                        sendEvent(emitter, Map.of("content", token));
                    }
                })
                .onCompleteResponse(response -> emitter.complete())
                .onError(error -> {
                    log.error("AI 流式回答失败", error);
                    sendEvent(emitter, Map.of("error", "AI 服务暂时不可用，请稍后重试"));
                    emitter.complete();
                })
                .start();
        return emitter;
    }

    private void sendEvent(SseEmitter emitter, Map<String, Object> data) {
        try {
            emitter.send(SseEmitter.event().data(data, MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            // 客户端主动断开时会到这里，忽略即可
            log.warn("SSE 推送失败：{}", e.getMessage());
        }
    }

    /**
     * 清空指定会话的记忆。
     */
    @PostMapping("clear")
    public Result clear(String memoryId) {
        if (StringUtils.isBlank(memoryId)) {
            return Result.fail("会话ID不能为空");
        }
        chatMemoryStore.deleteMessages(memoryId);
        return Result.success(true, "会话已清空");
    }
}
