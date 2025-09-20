package com;

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
			 new SimpleEvaluatorOptimizer(chatClient).loop("""
					<user input>
					 面试被问： 怎么高效的将100行list<User>数据，转化成map<id，user>，不是用stream.
					</user input>
					""");
		};
	}
}
