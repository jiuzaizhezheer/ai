package com.hzy.client;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

public class ReReadingAdvisor implements BaseAdvisor {
    private static final String TEMPLATE = "{Input_Query}\n" +
            "再次阅读问题：{Input_Query}";
    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        //获取用户输入信息
        String contents = chatClientRequest.prompt().getUserMessage().getText();
        //获取完整的信息，包含系统提示词和用户提示词等
        //String contents1 = chatClientRequest.prompt().getContents();
        //定义重复输入模板 并替换其中的占位符  返回新的  content
        String inputQuery = PromptTemplate.builder().template(TEMPLATE).build().render(Map.of("Input_Query", contents));

        //构建新的请求
        ChatClientRequest newChatClientRequest = ChatClientRequest.builder()
                .prompt(Prompt.builder()
                        .content(inputQuery)
                        .build())
                .build();
        //ChatClientRequest build = chatClientRequest.mutate().prompt(Prompt.builder().content(inputQuery).build()).build();
        return newChatClientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
