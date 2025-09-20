package com;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Arrays;

@SpringBootTest
public class EmbeddingTest {
    @Test
    public void testEmbadding(@Autowired DashScopeEmbeddingModel dashScopeEmbeddingModel) {

        float[] embedded = dashScopeEmbeddingModel.embed("我叫蔡徐坤");
        System.out.println(embedded.length);
        System.out.println(Arrays.toString(embedded));

    }
}
