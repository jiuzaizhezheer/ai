package com.hzy.client;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
public class TestJDBCMemory {

    @TestConfiguration
    static class TestMemoryConfig {
      @Bean
      public ChatMemory chatMemory(JdbcChatMemoryRepository chatMemoryRepository){

          return MessageWindowChatMemory.builder()
                  .maxMessages(10)
                  .chatMemoryRepository(chatMemoryRepository)
                  .build();

      }
    }

    @Test
    public void testJDBCMemory(@Autowired DeepSeekChatModel deepSeekChatModel,
                               @Autowired ChatMemory chatMemory) {

        ChatClient client = ChatClient.builder(deepSeekChatModel)
                .defaultAdvisors(PromptChatMemoryAdvisor
                        .builder(chatMemory)
                        .build())
                .build();

        String content1 = client.prompt()
                .user("请记住，我是蔡旭坤")
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, 1))
                .call()
                .content();
        System.out.println(content1);

        System.out.println("------------------------------");

        String content2 = client.prompt()
                .user("我是谁？")
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, 1))
                .call()
                .content();
        System.out.println(content2);

    }

}
