package app.controller;



import com.hzy.quick_start.config.VideoSynthesisParamConfig;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@RestController
public class OpenAiController {

    private final ChatClient chatClient;
    public OpenAiController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @RequestMapping(value = "/ai/generateStreamAsString", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generateStreamAsString(@RequestParam(value = "message", defaultValue = "讲个笑话") String message) {

        Flux<String> content = chatClient.prompt()
                .user(message)
                .system(p->p.param("current_date", LocalDateTime.now()))
                .stream()
                .content();

        return  content
                .doOnNext(System.out::println)
                //添加一个结束标志，便于前端知晓
                .concatWith(Flux.just("[complete]"));

    }



}