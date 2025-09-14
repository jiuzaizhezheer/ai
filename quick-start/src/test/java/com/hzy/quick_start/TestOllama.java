package com.hzy.quick_start;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

@SpringBootTest
public class TestOllama {
    @Test
    public void testOllama(@Autowired OllamaChatModel ollamaChatModel) {

        Flux<String> stream = ollamaChatModel.stream("写一篇风景作文2000字");
        //  软 关闭深度思考
//        Flux<String> stream = ollamaChatModel.stream("写一篇风景作文2000字/no_think");
        stream.toIterable().forEach(System.out::print);
    }

    /**
     * 多模态  图像识别，  采用的gemma3
     * @param ollamaChatModel
     */
    @Test
    public void testMultimodality(@Autowired OllamaChatModel ollamaChatModel) {
        var imageResource = new ClassPathResource("/files/hollow.png");

        OllamaOptions ollamaOptions = OllamaOptions.builder()
                .model("gemma3")
                .build();


        Media media = new Media(MimeTypeUtils.IMAGE_JPEG, imageResource);


        ChatResponse response = ollamaChatModel.call(
                new Prompt(
                        UserMessage.builder().media(media)
                                .text("识别图片").build(),
                        ollamaOptions
                )
        );

        System.out.println(response.getResult().getOutput().getText());
    }

}
