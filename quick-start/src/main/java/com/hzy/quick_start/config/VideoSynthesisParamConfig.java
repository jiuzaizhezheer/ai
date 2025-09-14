package com.hzy.quick_start.config;

import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class VideoSynthesisParamConfig {
    @Value("${spring.ai.dashscope.api-key}")
    private String apiKEY;
    @Bean
    public VideoSynthesisParam videoSynthesisParam() {
        return  VideoSynthesisParam.builder()
                //设置一些基础配置属性
                .model("wanx2.1-t2v-turbo")
                .size("1280*720")
                .apiKey(apiKEY)
                .build();
    }
}
