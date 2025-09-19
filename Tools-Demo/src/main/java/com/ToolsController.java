package com;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ToolsController {



    private final ChatClient chatClient;

    public ToolsController(DeepSeekChatModel chatModel, ChatMemory chatMemory , ToolsService toolsService, ToolCallbackProvider toolCallbackProvider){
         this.chatClient = ChatClient
                 .builder(chatModel)
                 .defaultAdvisors(PromptChatMemoryAdvisor
                         .builder(chatMemory).build())
                 //.defaultTools(toolsService)//绑定静态tools
                 .defaultToolCallbacks(toolsService.getToolCallList(toolsService))//绑定动态tools
                 .defaultToolCallbacks(toolCallbackProvider)
                 .build();
    }

    @RequestMapping("/tools")
    public Flux<String> tools(@RequestParam(value = "msg",defaultValue = "讲个笑话")
                            String msg){
        return chatClient.prompt()
                .user(msg)
                .stream().content();
        }

}
