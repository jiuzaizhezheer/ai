package app.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Bean
    public ChatClient chatClient(DeepSeekChatModel deepSeekChatModel, ChatMemory chatMemory) {
        return ChatClient
                .builder(deepSeekChatModel)//模型
                .defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory).build())//会话
                .defaultAdvisors(new SimpleLoggerAdvisor())//日志拦截
                .defaultSystem("您是“鸡哥”航空公司的客户聊天支持代理。请以友好、乐于助人且愉快的方式来回复。\n" +
                        "\t\t\t\t\t   您正在通过在线聊天系统与客户互动。\n" +
                        "\t\t\t\t\t   请讲中文。\n" +
                        "\t\t\t\t\t   今天的日期是 {current_date}")//系统提示词
                .build();
        //todo VectorStore vectorStore
    }
}
