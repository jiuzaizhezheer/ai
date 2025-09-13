package com.hzy.quick_start;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
public class TestDeepSeek {

    @Test
    public void testDeepSeek(@Autowired DeepSeekChatModel deepSeekChatModel) {
        //判断是否注入成功
        if(deepSeekChatModel == null)  return;
        System.out.println("DeepSeek大模型接入成功！！！");
        //同步阻塞式输出
//        String msg = deepSeekChatModel.call("你是我的我是你的谁，这句歌词是谁唱的？？");
//        System.out.println(msg);
        //配置一些属性
        //temperature(温度 0 ~ 2,越高越激情 ),可在yml配置
        //DeepSeekChatOptions build = DeepSeekChatOptions.builder().temperature(1.2d).build();
        //流式输出
        Flux<String> stream = deepSeekChatModel.stream("薛之谦是谁？？？200字");
        //输出
        stream.toIterable().forEach(x->{
            if(x==null){
                return;
            }
            System.out.println(x);

        });


    }

}
