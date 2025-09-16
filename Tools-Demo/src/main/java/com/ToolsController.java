package com;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ToolsController {



    private final ChatClient chatClient;


    public ToolsController(DeepSeekChatModel chatModel, ChatMemory chatMemory ,ToolsService toolsService){
         this.chatClient = ChatClient
                 .builder(chatModel)
                 .defaultAdvisors(PromptChatMemoryAdvisor
                         .builder(chatMemory).build())
                 .defaultTools(toolsService)//将tools绑定到client
                 .build();
    }

    @RequestMapping("/tools")
    public String tools(@RequestParam(value = "msg",defaultValue = "讲个笑话")
                            String msg){
        return chatClient.prompt()
                .user(msg)
                .call().content();
        }
}
