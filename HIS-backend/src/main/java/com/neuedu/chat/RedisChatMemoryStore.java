package com.neuedu.chat;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisChatMemoryStore implements ChatMemoryStore {

    /**
     * 会话记忆统一前缀，避免与业务缓存 key 冲突。
     */
    private static final String KEY_PREFIX = "ai:memory:";

    @Resource(name = "wyRedisTemplate")
    RedisTemplate<String, Object> redisTemplate;

    private String key(Object memoryId) {
        return KEY_PREFIX + memoryId;
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        Object value = redisTemplate.opsForValue().get(key(memoryId));
        if (value == null) {
            return List.of();
        }
        return ChatMessageDeserializer.messagesFromJson(value.toString());
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        String messagesJson = ChatMessageSerializer.messagesToJson(messages);
        redisTemplate.opsForValue().set(key(memoryId), messagesJson);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        redisTemplate.delete(key(memoryId));
    }
}
