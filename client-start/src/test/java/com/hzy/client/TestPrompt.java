package com.hzy.client;


import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;


@SpringBootTest
public class TestPrompt {

    /**
     * 提示词工程和模板设置
     * @param deepSeekChatModel
     */
    @Test
    public void testPrompt1(@Autowired DeepSeekChatModel deepSeekChatModel) {

        String prompt = "请在每一段话后面加上一个“喵”，姓名 {name} ,年龄 {age} ";
        //全局系统提示词
        ChatClient client = ChatClient.builder(deepSeekChatModel).defaultSystem(prompt).build();

        String content = client.prompt().user("你好").system(p->p.param("name","张三")
                .param("age",18)).call().content();
        System.out.println(content);

        //局部系统提示词, 局部会覆盖全局
        System.out.println(client.prompt().system("请在每一段话后面加上一个“汪”").user("你好").call().content());

    }


    /**
     * 通过文件引入提示词
     * @param deepSeekChatModel
     */
    /* @Value("classpath:/files/prompt.st")
    private Resource prompt;*/
    @Test
    public void testPrompt2(@Autowired DeepSeekChatModel deepSeekChatModel,
                            @Value("classpath:/files/prompt.st") Resource prompt) {
        //全局系统提示词
        ChatClient client = ChatClient.builder(deepSeekChatModel).defaultSystem(prompt).build();

        String content = client.prompt().user("你好").system(p->p.param("name","张三")
                .param("age",18)).call().content();
        System.out.println(content);

        //局部系统提示词, 局部会覆盖全局
        System.out.println(client.prompt().system("请在每一段话后面加上一个“汪”").user("你好").call().content());

    }
}
