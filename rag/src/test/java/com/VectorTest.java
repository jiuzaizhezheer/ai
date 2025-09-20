package com;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class VectorTest {

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public VectorStore vectorStore(DashScopeEmbeddingModel dashScopeEmbeddingModel) {
            return SimpleVectorStore.builder(dashScopeEmbeddingModel).build();
        }
    }



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
    public void  testSearch(@Autowired VectorStore vectorStore){
        // todo 根据文本查询所有文档的匹配情况
        List<Document> result = vectorStore.similaritySearch("退票");

        for (Document document : result) {
            System.out.println(document.getScore());
            System.out.println(document.getText());
        }

        // todo 构造 SearchRequest 对象 来筛选文档

        SearchRequest searchRequest = SearchRequest.builder()
                .query("退票")
                .topK(1)//前1个
                .similarityThreshold(0.6)//分值 0.6 以上
                .build();

        List<Document> documents = vectorStore.similaritySearch(searchRequest);
        documents.forEach(doc->{
            System.out.println(doc.getScore());
            System.out.println(doc.getText());
        });

    }

}
