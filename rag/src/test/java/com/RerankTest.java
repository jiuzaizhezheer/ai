package com;

import com.alibaba.cloud.ai.advisor.RetrievalRerankAdvisor;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import java.util.List;

@SpringBootTest
public class RerankTest {

    @BeforeEach
    public void init(
            @Autowired VectorStore vectorStore,
            @Value("classpath:rag/test.txt") Resource resource) {
        // 读取
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("filename", resource.getFilename());
        List<Document> documents = textReader.read();


        // 分隔
        ChineseTokenTextSplitter splitter = new ChineseTokenTextSplitter(80,10,5,10000,true);
        List<Document> apply = splitter.apply(documents);


        // 存储向量（内部会自动向量化)
        vectorStore.add(apply);
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public VectorStore vectorStore(DashScopeEmbeddingModel embeddingModel) {
            return SimpleVectorStore.builder(embeddingModel).build();
        }
    }


    @Test
    public void testRerank(
            @Autowired DashScopeChatModel dashScopeChatModel,
            @Autowired VectorStore vectorStore,
            @Autowired DashScopeRerankModel rerankModel) {

        ChatClient chatClient = ChatClient.builder(dashScopeChatModel)
                .build();

        List<Document> a1 = vectorStore.similaritySearch("退票");
        System.out.println(a1);
        RetrievalRerankAdvisor retrievalRerankAdvisor =
                new RetrievalRerankAdvisor(vectorStore, rerankModel
                        , SearchRequest.builder().topK(200).build());

        String content = chatClient.prompt().user("退票")
                .advisors(retrievalRerankAdvisor)
                .call()
                .content();


        System.out.println(content);

    }
}