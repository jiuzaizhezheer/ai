package bbb;

import com.SimpleEvaluatorOptimizer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(DeepSeekChatModel deepSeekChatModel) {
		var chatClient =  ChatClient.create(deepSeekChatModel);
		return args -> {


			String requirements = """  
            需要编写一份关于微服务架构设计的技术文档，包括：  
            1. 架构概述  
            2. 服务拆分策略  
            3. 数据一致性方案  
            4. 监控和运维  
            目标读者：技术团队和架构师  
            """;

			DocumentGenerationChainWorkflow.DocumentResult result = new DocumentGenerationChainWorkflow(chatClient)
					.processDocumentGeneration(requirements);

			System.out.println("生成结果: " + (result.success() ? "成功" : "失败"));
			System.out.println("最终文档:" + result.finalDocument());
			System.out.println("处理步骤: " + result.steps());
		};
	}

}
