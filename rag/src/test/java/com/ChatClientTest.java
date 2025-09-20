package com;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class ChatClientTest {

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public VectorStore vectorStore(DashScopeEmbeddingModel dashScopeEmbeddingModel) {
            return SimpleVectorStore.builder(dashScopeEmbeddingModel).build();
        }
    }


    /**
     * 初始化向量数据库信息
     * @param vectorStore 向量数据库
     */
    @BeforeEach
    public void init(@Autowired VectorStore vectorStore){
        // 1. 声明内容文档
        Document doc = Document.builder()
                .text("""
          预订航班:
          - 通过我们的网站或移动应用程序预订。
          - 预订时需要全额付款。
          - 确保个人信息（姓名、ID 等）的准确性，因为更正可能会产生 25 的费用。
          """)
                .build();
        Document doc2 = Document.builder()
                .text("""
          取消预订:
          - 最晚在航班起飞前 48 小时取消。
          - 取消费用：经济舱 75 美元，豪华经济舱 50 美元，商务舱 25 美元。
          - 退款将在 7 个工作日内处理。
          """)
                .build();

        // 2. 将文本进行向量化，并且存入向量数据库（内部自动向量化）
        vectorStore.add(Arrays.asList(doc,doc2));

    }

    @Test
    public void testRag(@Autowired DashScopeChatModel dashScopeChatModel,
                        @Autowired VectorStore vectorStore){

        ChatClient client = ChatClient.builder(dashScopeChatModel).build();

        Flux<String> flux = client.prompt().user("退票多少钱？？？")
                .advisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder()
                                .topK(1)
                                .similarityThreshold(0.5)
                                .query("退票多少钱？？？").build()).build()
                )
                .advisors(new SimpleLoggerAdvisor())
                .stream()
                .content();

        for (String s : flux.toIterable()) {
            System.out.println(s);
        }


    }


}
