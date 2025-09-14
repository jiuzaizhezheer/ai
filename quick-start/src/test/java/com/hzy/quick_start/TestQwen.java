package com.hzy.quick_start;

import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionOptions;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisOptions;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisPrompt;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisResponse;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;


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

    /**
     * 文生语言
     * @param speechSynthesisModel
     * @throws IOException
     */
    // https://bailian.console.aliyun.com/?spm=5176.29619931.J__Z58Z6CX7MY__Ll8p1ZOR.1.74cd59fcXOTaDL&tab=doc#/doc/?type=model&url=https%3A%2F%2Fhelp.aliyun.com%2Fdocument_detail%2F2842586.html&renderType=iframe
    @Test
    public void testText2Audio(@Autowired DashScopeSpeechSynthesisModel speechSynthesisModel) throws IOException {
        DashScopeSpeechSynthesisOptions options = DashScopeSpeechSynthesisOptions.builder()
                //.voice()   // 人声
                //.speed()    // 语速
                //.model()    // 模型
                //.responseFormat(DashScopeSpeechSynthesisApi.ResponseFormat.MP3)
                .build();

        SpeechSynthesisResponse response = speechSynthesisModel.call(
                new SpeechSynthesisPrompt("大家好， 我是爱打篮球的蔡徐坤。",options)
        );

       // File file = new File( "C:\\Users\\17996\\Downloads\\mp3\\test.mp3");
        File file = new File( "C:/Users/17996/Downloads/mp3/test1.mp3");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            ByteBuffer byteBuffer = response.getResult().getOutput().getAudio();
            fos.write(byteBuffer.array());
        }
        catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * 语言翻译
     * @param transcriptionModel
     * @throws MalformedURLException
     */

    private static final String AUDIO_RESOURCES_URL = "https://dashscope.oss-cn-beijing.aliyuncs.com/samples/audio/paraformer/hello_world_female2.wav";

    @Test
    public void testAudio2Text(
            @Autowired
            DashScopeAudioTranscriptionModel transcriptionModel
    ) throws MalformedURLException {
        DashScopeAudioTranscriptionOptions transcriptionOptions = DashScopeAudioTranscriptionOptions.builder()
                //.withModel()   模型
                .build();
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(
                new UrlResource(AUDIO_RESOURCES_URL),
                transcriptionOptions
        );
        AudioTranscriptionResponse response = transcriptionModel.call(
                prompt
        );

        System.out.println(response.getResult().getOutput());

    }
}
