package com.neuedu.test;

import com.neuedu.App;
import com.neuedu.service.Assistant;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentLoader;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.parser.apache.poi.ApachePoiDocumentParser;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.message.Message;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = App.class)
public class LangChainTest {
    @Resource
    ChatModel chatModel;
    @Resource
    ChatMemoryStore chatMemoryStore;
    @Test
    public void test() throws IOException {
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(10)
                .id("001")
                .chatMemoryStore(chatMemoryStore)
                .build();
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .build();
        String template = """
                <context>%s</context>
                <question>%s</question>
                上面context标签中的内容为校规的上下文内容，
                上面的question标签中的内容为用户提问，请基于上下文的内容来回答用户的提问。
                如果找不到答案，不要乱编，直接告诉用户，无法回答，找不到答案。
                """;
        File file = new File("/Users/wangyu/code/java/henanshida/src/main/resources/docs/校规.txt");
        String context = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        String question = "北方工业大学校规第十条是什么";
        String userMessage = String.format(template, context, question);
        System.out.println(assistant.chat(userMessage));

    }

    @Test
    public void read () throws IOException {
        Document doc = ClassPathDocumentLoader.loadDocument("docs/高敬杰河北工程大学.pdf");
        ApachePdfBoxDocumentParser parser = new ApachePdfBoxDocumentParser();
        String url = URLDecoder.decode(doc.metadata().getString("url"), StandardCharsets.UTF_8);
        Document doc2 = parser.parse(FileUtils.openInputStream(new File(url)));
        //        ApachePoiDocumentParser parser = new ApachePoiDocumentParser();
//        String url = URLDecoder.decode(docs.get(0).metadata().getString("url"), StandardCharsets.UTF_8);
//        InputStream inputStream = FileUtils.openInputStream(new File(url));
//        Document document = parser.parse(inputStream);
        System.out.println(doc2 );
    }
}
