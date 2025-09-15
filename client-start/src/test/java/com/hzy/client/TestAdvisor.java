package com.hzy.client;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootTest
public class TestAdvisor {
    /**
     * 日志 , 敏感词拦截
     * @param deepSeekChatModel
     */
    @Test
    public void testAdvisor(@Autowired DeepSeekChatModel deepSeekChatModel) {

        ChatClient client = ChatClient.builder(deepSeekChatModel).build();

        System.out.println(client.prompt()
                .user("蔡徐坤作文200字")
                .advisors(new SimpleLoggerAdvisor(),new SafeGuardAdvisor(List.of("蔡徐坤")))
                .call()
                .content());
    }

    /**
     * 重写拦截器
     * @param deepSeekChatModel
     */
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

    /**
     * 会话记忆功能
     * @param deepSeekChatModel
     * @param chatMemory
     */
    @Test
    public void testMemoryAdvisor(@Autowired DeepSeekChatModel deepSeekChatModel,
                                  @Autowired ChatMemory chatMemory) {

        ChatClient client = ChatClient
                .builder(deepSeekChatModel)
                .defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory).build()).build();
        //第一轮对话
        System.out.println(client.prompt().user("记住我是我是蔡徐坤").call().content());
        //第二轮对话
        System.out.println(client.prompt().user("请问我是谁？").call().content());

    }

    @TestConfiguration
    static class TestMemoryConfig {
        /**
         * 自定义会话记忆参数
         * @param chatMemoryRepository
         * @return
         */
        @Bean
        public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
            return MessageWindowChatMemory
                    .builder()
                    .maxMessages(10)
                    .chatMemoryRepository(chatMemoryRepository)
                    .build();
        }

    }

    /**
     * 多个用户会话记忆隔离
     * @param deepSeekChatModel
     *
     */
    @Test
    public void testChatOptions(@Autowired DeepSeekChatModel deepSeekChatModel,
                                @Autowired ChatMemory chatMemory) {
        ChatClient chatClient = ChatClient.builder(deepSeekChatModel).defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory).build()).build();
        String content = chatClient.prompt()
                .user("我叫蔡徐坤 ？")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,"1"))
                .call()
                .content();
        System.out.println(content);
        System.out.println("--------------------------------------------------------------------------");

        content = chatClient.prompt()
                .user("我叫什么 ？")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,"1"))
                .call()
                .content();
        System.out.println(content);


        System.out.println("--------------------------------------------------------------------------");

        content = chatClient.prompt()
                .user("我叫什么 ？")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,"2"))
                .call()
                .content();
        System.out.println(content);
    }
}
