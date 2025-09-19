package com.hzy.client;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
public class PlatformsAndModelsController {
    private final Map<String, ChatModel> modelMap;
    public PlatformsAndModelsController(Map<String, ChatModel> modelMap){
        this.modelMap = modelMap;
    }
    @RequestMapping(value = "/chat", produces = "text/stream;charset=UTF-8")
    public Flux<String> chat(PlatformsAndModelsDTO platformsAndModelsDTO){
        //得到对应大模型平台
        ChatClient.Builder builder = ChatClient.builder(modelMap.get(platformsAndModelsDTO.getPlatform()));
        //构建配置得到对应client
        ChatClient client = builder.defaultOptions(ChatOptions.builder()
                .model(platformsAndModelsDTO.getModel())
                .temperature(platformsAndModelsDTO.getTemperature())
                .build()).build();
        //调用client
        Flux<String> content = client.prompt().user(platformsAndModelsDTO.getMsg()).stream().content();
        return content;
    }


}
