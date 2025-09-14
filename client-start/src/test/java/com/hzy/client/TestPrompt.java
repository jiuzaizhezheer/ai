package com.hzy.client;


import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class TestPrompt {


    @Test
    public void testPrompt(@Autowired DeepSeekChatModel deepSeekChatModel) {

        String prompt = "请在每一段话后面加上一个“喵”";
        //全局系统提示词
        ChatClient client = ChatClient.builder(deepSeekChatModel).defaultSystem(prompt).build();

        String content = client.prompt().user("你好").call().content();
        System.out.println(content);

        //局部系统提示词, 局部会覆盖全局
        System.out.println(client.prompt().system("请在每一段话后面加上一个“汪”").user("你好").call().content());

    }


}
