package com.hzy.quick_start;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


@SpringBootTest
public class TestQwen {
    /**
     * 文本对话
     * @param dashScopeChatModel
     */
    @Test
    public void testQwen(@Autowired DashScopeChatModel dashScopeChatModel){
        if(dashScopeChatModel==null) return;
        System.out.println("通义大模型接入成功！！！");
//        System.out.println(dashScopeChatModel.call("你是谁"));
        Flux<String> stream = dashScopeChatModel.stream("你是谁");
        stream.toIterable().forEach(System.out::print);
    }

    /**
     * 文生图
     * @param imageModel
     * @throws MalformedURLException
     */

    @Test
    public void text2Img(
            @Autowired DashScopeImageModel imageModel) throws MalformedURLException {
        DashScopeImageOptions imageOptions = DashScopeImageOptions.builder()
                .withModel("wanx2.1-t2i-turbo")
//                .withHeight()
//                .withN()
                .withResponseFormat("both") // 如果服务支持同时返回url,base64
                .build();

        ImageResponse imageResponse = imageModel.call(
                new ImagePrompt("一只鸡", imageOptions));
        String imageUrl = imageResponse.getResult().getOutput().getUrl();

        // 图片url
        System.out.println(imageUrl);

         //图片base64
        String b64Json = imageResponse.getResult().getOutput().getB64Json();
        System.out.println(b64Json);



        //按文件流响应

        // 先转换成URL类型
        /*URL url = new URL(imageUrl);
        try (InputStream in = url.openStream()) {
             response.setHeader("Content-Type", MediaType.IMAGE_PNG_VALUE);
             byte[] buffer = new byte[8192];
             int bytesRead;
             while ((bytesRead = in.read(buffer)) != -1) {
                 response.getOutputStream().write(buffer, 0, bytesRead);
             }
             response.getOutputStream().flush();
         } catch (IOException e) {
             throw new RuntimeException("Failed to read image from URL", e);
         }*/

    }

}
