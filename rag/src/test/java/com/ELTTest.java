package com;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.util.List;

@SpringBootTest
public class ELTTest {
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

}
