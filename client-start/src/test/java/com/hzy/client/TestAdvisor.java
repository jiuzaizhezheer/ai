package com.hzy.client;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestAdvisor {
    @Test
    public void testAdvisor(@Autowired DeepSeekChatModel deepSeekChatModel) {

        ChatClient client = ChatClient.builder(deepSeekChatModel).build();

        System.out.println(client.prompt()
                .user("风景作文200字")
                .advisors(new SimpleLoggerAdvisor())
                .call()
                .content());


    }
}
