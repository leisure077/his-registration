package com.neuedu.test;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.neuedu.App;
import com.neuedu.entity.SysDept;
import com.neuedu.service.SysDeptService;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = App.class)
public class MybatisTest {
    @Resource
    SysDeptService sysDeptService;

    @Test
    public void test1() {
        sysDeptService.page(Page.of(1, 5));
        System.out.println(sysDeptService.list());

    }
}
