package com;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringAiTranslationController {
    @Autowired
    private ChatClient chatModel;

    @RequestMapping("/test")
    public String translate(String msg) {

        System.out.println(msg);

        String content = chatModel.prompt().system("翻译为英文").user(msg
        ).call().content();

        return content;
    }
}

