package com;

import com.alibaba.cloud.ai.advisor.RetrievalRerankAdvisor;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
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


    /**
     * 评估模型
     * @param chatModel
     */
    @Test
    void testFactChecking(@Autowired DashScopeChatModel chatModel,
                          @Autowired VectorStore vectorStore) {


        // 创建 FactCheckingEvaluator
        var factCheckingEvaluator = new FactCheckingEvaluator(ChatClient.builder(chatModel));

        // 示例上下文和声明
        Document doc = Document.builder().text("地球是仅次于太阳的第三颗行星，也是已知唯一孕育生命的天文物体。").build();

        List<Document> docList = List.of(doc);
        String claim = "地球是距离太阳第三大行星。";

        // 创建 EvaluationRequest
        EvaluationRequest evaluationRequest = new EvaluationRequest(docList,claim);

        // 执行评估
        EvaluationResponse evaluationResponse = factCheckingEvaluator.evaluate(evaluationRequest);


        Assertions.assertTrue(evaluationResponse.isPass(), "The claim should not be supported by the context");

    }


}