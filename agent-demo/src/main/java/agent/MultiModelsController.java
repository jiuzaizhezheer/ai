package agent;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
public class MultiModelsController {

    @Resource
    ChatClient planningChatClient;

    @Resource
    ChatClient botChatClient;




    @RequestMapping(value = "/stream", produces = "text/stream;charset=UTF8")
    public Flux<String> stream(@RequestParam String message) {
        // 创建一个用于接收多条消息的 Sink
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        // 推送消息
        sink.tryEmitNext("正在计划任务...<br/>");


        new Thread(() -> {
        AiJob.Job job = planningChatClient.prompt().user(message)
                .call().entity(AiJob.Job.class);

        switch (job.jobType()){
            case CANCEL ->{
                System.out.println(job);
                // todo.. 执行业务
                if(job.keyInfos().size()!=2){
                    sink.tryEmitNext("还需输入姓名或订单号");
                }
                else {
                    sink.tryEmitNext("退票成功!");
                }
            }
            case QUERY -> {
                System.out.println(job);
                // todo.. 执行业务
                sink.tryEmitNext("查询预定信息...");
            }
            case OTHER -> {
                //得到的结果
                Flux<String> content = botChatClient.prompt().user(message).stream().content();

                content.doOnNext(sink::tryEmitNext) // 推送每条AI流内容
                        .doOnComplete(sink::tryEmitComplete)
                        .subscribe();
            }
            default -> {
                System.out.println(job);
                sink.tryEmitNext("解析失败");
            }
        }
        }).start();

        return sink.asFlux();
    }



    @RequestMapping(value = "/stream1", produces = "text/stream;charset=UTF8")
    public Flux<String> stream1(@RequestParam String message) {
        // 提示信息
        Flux<String> initialResponse = Flux.just("正在计划任务...<br/>");

        //ai返回的流

        Flux<String> aiResponse = messageHandler(message);
        return Flux.concat(initialResponse, aiResponse);
    }

    private Flux<String> messageHandler( String message) {
        AiJob.Job job = planningChatClient.prompt().user(message).call().entity(AiJob.Job.class);
        // 为OTHER类型单独处理message参数
        if (job== null ) return null;
        return switch (job.jobType()) {
            case CANCEL -> {
                System.out.println(job);
                if (job.keyInfos().size() != 2) {
                    yield Flux.just("还需输入姓名或订单号");
                } else {
                    yield Flux.just("退票成功!");
                }
            }
            case QUERY -> {
                System.out.println(job);
                yield Flux.just("查询预定信息...");
            }
            case OTHER -> botChatClient.prompt()
                        .user(message)
                        .stream()
                        .content();

        };
    }


}