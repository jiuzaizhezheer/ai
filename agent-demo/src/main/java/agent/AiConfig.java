package agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    /**
     * 信息预处理 的client
     * @param chatModel
     * @param chatMemory
     * @return
     */
    @Bean
    public ChatClient planningChatClient(DeepSeekChatModel chatModel,
                                         ChatMemory chatMemory) {
        DeepSeekChatOptions options = DeepSeekChatOptions.builder().temperature(0.7).build();

        return  ChatClient.builder(chatModel)
                    .defaultSystem("""
                            
                            # 票务助手任务拆分规则
                            
                            ## 1.要求
                            ### 1.1 根据用户内容识别任务类型
       
                            ## 2. 任务
                            ### 2.1 JobType:退票(CANCEL) 要求用户提供姓名和预定号， 或者从对话中提取,保存为key-value形式；
                            ### 2.2 JobType:查票(QUERY) 要求用户提供预定号， 或者从对话中提取，保存为key-value形式；
                            ### 2.3 JobType:其他(OTHER)
                            
                            """)
                    .defaultAdvisors(
                            MessageChatMemoryAdvisor.builder(chatMemory).build()
                    )
                    .defaultOptions(options)
                    .build();
    }

    @Bean
    public ChatClient botChatClient(DeepSeekChatModel chatModel,
                                         ChatMemory chatMemory) {

        DeepSeekChatOptions options = DeepSeekChatOptions.builder().temperature(1.2).build();

        return  ChatClient.builder(chatModel)
                .defaultSystem("""
                           你是一个航空智能客服代理， 请以友好的语气服务用户。
                            """)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .defaultOptions(options)
                .build();
    }

}