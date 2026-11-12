package com.neuedu.test;

import com.neuedu.App;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = App.class)
public class RedisTest {
    @Resource(name = "wyRedisTemplate")
    RedisTemplate<String, Object> redisTemplate;
    @Test
    public void test() {
        redisTemplate.opsForValue().set("0001", "张三", 100, TimeUnit.SECONDS);
    }
}
