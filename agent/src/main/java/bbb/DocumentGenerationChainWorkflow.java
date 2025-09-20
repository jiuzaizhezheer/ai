package bbb;

import org.springframework.ai.chat.client.ChatClient;

import java.util.ArrayList;
import java.util.List;

public class DocumentGenerationChainWorkflow {
      
    private final ChatClient chatClient;
      
    public DocumentGenerationChainWorkflow(ChatClient chatClient) {  
        this.chatClient = chatClient;  
    }  
      
    public DocumentResult processDocumentGeneration(String requirements) {  
        List<String> steps = new ArrayList<>();
        String currentOutput = requirements;  
          
        System.out.println("=== 开始文档生成链式流程 ===");  
          
        // 门控：需求验证  
        if (!validateRequirements(currentOutput)) {  
            return new DocumentResult("需求验证失败，流程终止", steps, false);  
        }  
        steps.add("需求验证: 通过");  
          
        // 步骤1: 生成大纲 - 基于原始需求  
        currentOutput = generateOutline(currentOutput);  
        steps.add("大纲生成: 完成");  
          
        // 步骤2: 扩展内容 - 基于大纲  
        currentOutput = expandContent(currentOutput);  
        steps.add("内容扩展: 完成");  
          
        // 步骤3: 优化语言 - 基于扩展后的内容  
        currentOutput = optimizeLanguage(currentOutput);  
        steps.add("语言优化: 完成");  
          
        // 步骤4: 格式化 - 基于优化后的内容  
        currentOutput = formatDocument(currentOutput);  
        steps.add("文档格式化: 完成");  
          
        System.out.println("=== 文档生成流程完成 ===");  
          
        return new DocumentResult(currentOutput, steps, true);  
    }  
      
    private boolean validateRequirements(String requirements) {  
        String validationPrompt = """  
            请验证以下文档需求是否可行：  
              
            需求: {requirements}  
              
            如果需求清晰完整，请回复"PASS"，否则回复"FAIL"。  
            """;  
          
        String result = chatClient.prompt()  
            .user(u -> u.text(validationPrompt).param("requirements", requirements))  
            .call()  
            .content();
        System.err.println(result);
          
        return result.trim().toUpperCase().contains("PASS");  
    }  
      
    private String generateOutline(String requirements) {  
        String outlinePrompt = """  
            基于以下需求，生成详细的文档大纲：  
              
            需求: {input}  
              
            请生成包含主要章节和子章节的结构化大纲。  
            """;  
          
        return executeStep(outlinePrompt, requirements);  
    }  
      
    private String expandContent(String outline) {  
        String contentPrompt = """  
            基于以下大纲，为每个章节生成详细内容：  
              
            大纲: {input}  
              
            请为每个章节编写具体内容，保持逻辑连贯。  
            """;  
          
        return executeStep(contentPrompt, outline);  
    }  
      
    private String optimizeLanguage(String content) {  
        String optimizePrompt = """  
            优化以下文档内容的语言表达：  
              
            原始内容: {input}  
              
            请改进语言表达，使其更加专业、清晰、易读。  
            """;  
          
        return executeStep(optimizePrompt, content);  
    }  
      
    private String formatDocument(String content) {  
        String formatPrompt = """  
            将以下内容格式化为专业文档：  
              
            内容: {input}  
              
            请添加适当的标题层级、列表、表格等格式，生成最终的markdown文档。  
            """;  
          
        return executeStep(formatPrompt, content);  
    }  
      
    private String executeStep(String prompt, String input) {  
        return chatClient.prompt()  
            .user(u -> u.text(prompt).param("input", input))  
            .call()  
            .content();  
    }  
      
    public record DocumentResult(String finalDocument, List<String> steps, boolean success) {}  
}