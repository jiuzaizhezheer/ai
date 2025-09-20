package ccc;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(DeepSeekChatModel deepSeekChatModel) {
		var chatClient =  ChatClient.create(deepSeekChatModel);
		return args -> {

			List<String> departments = List.of(
					"IT部门：负责系统架构升级，团队技术水平参差不齐，预算紧张",
					"销售部门：需要学习新的CRM系统，担心影响客户关系，抗拒变化",
					"财务部门：要求数据安全性极高，对云端存储有顾虑，流程复杂",
					"人力资源部门：需要数字化招聘流程，缺乏相关技术人员，时间紧迫"
			);

			System.out.println("=== 并行分析 + 聚合处理 ===");
			ParallelizationWorkflowWithAggregator.AggregatedResult result = new ParallelizationWorkflowWithAggregator(chatClient)
					.parallelWithAggregation( departments);

			System.out.println("\n=== 各部门独立分析结果 ===");
			for (int i = 0; i < result.individualResults().size(); i++) {
				System.out.println("部门" + (i + 1) + ":");
				System.out.println(result.individualResults().get(i));
				System.out.println("\n" + "-".repeat(50) + "\n");
			}

			System.out.println("\n=== 聚合器综合报告 ===");
			System.out.println(result.aggregatedOutput());
		};
	}
}