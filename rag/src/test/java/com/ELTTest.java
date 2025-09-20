package com;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.ai.model.transformer.SummaryMetadataEnricher;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
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
public class ELTTest {


    @TestConfiguration
    public static class TestConfig {
        @Bean
        public VectorStore vectorStore(DashScopeEmbeddingModel dashScopeEmbeddingModel) {
            return SimpleVectorStore.builder(dashScopeEmbeddingModel).build();
        }
    }

    /**
     * 读取txt文本内容
     * @param resource
     */
    @Test
    public void testReaderText(@Value("classpath:rag/test.txt") Resource resource) {
        TextReader textReader = new TextReader(resource);
        List<Document> documents = textReader.read();

        for (Document document : documents) {
            System.out.println(document.getText());
        }
    }

    /**
     * 读取markdown
     * @param resource
     */
    @Test
    public void testReaderMD(@Value("classpath:rag/test.md") Resource resource) {
        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)     // 分割线创建新document
                .withIncludeCodeBlock(false)                // 代码创建新document
                .withIncludeBlockquote(false)               // 引用创建新document
                .withAdditionalMetadata("filename", resource.getFilename())    // 每个document添加的元数据
                .build();

        MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(resource, config);
        List<Document> documents = markdownDocumentReader.read();
        for (Document document : documents) {
            System.out.println(document.getText());
        }
    }



    @Test
    public void testReaderPdf(@Value("classpath:rag/平安银行2023年半年度报告摘要.pdf") Resource resource) {

        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource,
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .withPagesPerDocument(1)
                        .build());

        List<Document> documents = pdfReader.read();
        for (Document document : documents) {
            System.out.println(document.getText());
        }
    }

    /**
     * pdf读取
     * @param resource
     */
    // 必需要带目录，  按pdf的目录分document
    @Test
    public void testReaderParagraphPdf(@Value("classpath:rag/test.pdf") Resource resource) {
        ParagraphPdfDocumentReader pdfReader = new ParagraphPdfDocumentReader(resource,
                PdfDocumentReaderConfig.builder()
                        // 不同的PDF生成工具可能使用不同的坐标系 ， 如果内容识别有问题， 可以设置该属性为true
                        .withReversedParagraphPosition(true)
                        .withPageTopMargin(0)       // 上边距
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                // 从页面文本中删除前 N 行
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .build());

        List<Document> documents = pdfReader.read();
        for (Document document : documents) {
            System.out.println(document.getText());
        }
    }

    /**
     * 文档分割器
     * @param resource
     */

    @Test
    public void testTokenTextSplitter(@Value("classpath:rag/test.txt") Resource resource) {
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("filename", resource.getFilename());
        List<Document> documents = textReader.read();


        TokenTextSplitter splitter = new TokenTextSplitter(10, 8, 10, 5000, true);
        List<Document> apply = splitter.apply(documents);

        apply.forEach(System.out::println);
    }
    /**
     * 自定义文档分割器
     * @param resource
     */

    @Test
    public void testChineseTokenTextSplitter(@Value("classpath:rag/test.txt") Resource resource) {
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("filename", resource.getFilename());
        List<Document> documents = textReader.read();


        ChineseTokenTextSplitter splitter = new ChineseTokenTextSplitter();
        List<Document> apply = splitter.apply(documents);

        apply.forEach(System.out::println);
    }

    /**
     * 提取元数据 关键词
     * @param chatModel
     * @param resource
     */
    @Test
    public void testKeywordMetadataEnricher(
            @Autowired VectorStore vectorStore,
            @Autowired DashScopeChatModel chatModel,
            @Value("classpath:rag/test.txt") Resource resource) {
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("filename", resource.getFilename());
        List<Document> documents = textReader.read();

        ChineseTokenTextSplitter splitter = new ChineseTokenTextSplitter();
        List<Document> apply = splitter.apply(documents);
        //提取关键字  但不能用于过滤，因为是不可控的
        KeywordMetadataEnricher enricher = new KeywordMetadataEnricher(chatModel, 5);
        apply=  enricher.apply(apply);
        //添加到rag
        vectorStore.add(apply);


        //检索
        List<Document> documents1 = vectorStore.similaritySearch(SearchRequest.builder()
                .filterExpression("filename=='test.txt'") //过滤元数据，严格相等
                .build());


        System.out.println(documents1);
    }


    /**
     * 上下文摘要提取
     * @param chatModel
     * @param resource
     */
    @Test
    public void testSummaryMetadataEnricher(
            @Autowired DashScopeChatModel chatModel,
            @Value("classpath:rag/test.txt") Resource resource) {
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("filename", resource.getFilename());
        List<Document> documents = textReader.read();


        ChineseTokenTextSplitter splitter = new ChineseTokenTextSplitter();
        List<Document> apply = splitter.apply(documents);

        SummaryMetadataEnricher enricher = new SummaryMetadataEnricher(chatModel,
                List.of(SummaryMetadataEnricher.SummaryType.PREVIOUS,
                        SummaryMetadataEnricher.SummaryType.CURRENT,
                        SummaryMetadataEnricher.SummaryType.NEXT));

        apply = enricher.apply(apply);

        System.out.println(apply);
    }


}
