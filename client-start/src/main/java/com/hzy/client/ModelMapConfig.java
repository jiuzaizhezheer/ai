package com.hzy.client;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ModelMapConfig {

    //自动装配一个map map里面是 模型名称 和 模型对象
    @Bean
    public Map<String, ChatModel> modelMap(
            DeepSeekChatModel deepSeekChatModel,
            DashScopeChatModel dashScopeChatModel,
            OllamaChatModel ollamaChatModel
    ) {
        Map<String, ChatModel> modelMap = new HashMap<>();
        //可将key封装到常量类中，不容易出错
        modelMap.put("deepseek", deepSeekChatModel);
        modelMap.put("dashscope", dashScopeChatModel);
        modelMap.put("ollama", ollamaChatModel);
        return modelMap;
    }
}
