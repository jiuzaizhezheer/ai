package com.hzy.client;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TestAdvisor {
    @Test
    public void testAdvisor(@Autowired DeepSeekChatModel deepSeekChatModel) {

        ChatClient client = ChatClient.builder(deepSeekChatModel).build();

        System.out.println(client.prompt()
                .user("蔡徐坤作文200字")
                .advisors(new SimpleLoggerAdvisor(),new SafeGuardAdvisor(List.of("蔡徐坤")))
                .call()
                .content());
    }
    @Test
    public void testReReadingAdvisor(@Autowired DeepSeekChatModel deepSeekChatModel) {

        ChatClient client = ChatClient.builder(deepSeekChatModel).build();

        System.out.println(client.prompt()
                .user("诸葛亮作文200字")
                .advisors(new SimpleLoggerAdvisor()
                        ,new SafeGuardAdvisor(List.of("蔡徐坤"))
                        ,new ReReadingAdvisor())
                .call()
                .content());


    }
}
