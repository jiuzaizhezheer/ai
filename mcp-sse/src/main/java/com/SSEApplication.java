package com;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class SSEApplication {
    public static void main(String[] args) {
        SpringApplication.run(SSEApplication.class, args);
        System.err.println("Client启动~~~");
    }

    @Bean
    public ToolCallbackProvider sseToolCallbackProvider(UserToolService userToolService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(userToolService)
                .build();
    }

}