package com.hzy.client;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
public class TestChatClient {
    /**
     *  xml里只有单模型依赖  全部输出
     * @param chatClientBuilder
     */
    @Test
    public void testChatClient(@Autowired ChatClient.Builder chatClientBuilder) {
        ChatClient chatClient = chatClientBuilder.build();
        String content = chatClient.prompt()
                .user("你好呀！500字")
                .call()
                .content();
        System.out.println(content);

    }

    /**
     *  xml里只有单模型依赖  流式输出
     * @param chatClientBuilder
     */
    @Test
    public void testStreamChatClient(@Autowired ChatClient.Builder chatClientBuilder) {
        ChatClient chatClient = chatClientBuilder.build();
        Flux<String> content = chatClient.prompt()
                .user("小作文！500字")
                .stream()
                .content();
        content.toIterable().forEach(System.out::println);

    }

    /**
     * 多模型的时候，手动注入对应的模型名称
     *
     */
    @Test
    public void testMultiModelChatClient(@Autowired DeepSeekChatModel deepSeekChatModel) {
        ChatClient chatClient = ChatClient.builder(deepSeekChatModel).build();
        Flux<String> content = chatClient.prompt()
                .user("风景作文200字")
                .stream()
                .content();
        for (String s : content.toIterable()) {
            System.out.print(s);
        }

    }
}
