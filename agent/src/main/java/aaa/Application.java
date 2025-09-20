package aaa;

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
		new SimpleOrchestratorWorkers(chatClient)
					 .process("设计一个企业级的员工考勤系统，支持多种打卡方式和报表生成");

		};
	}
}