package com;

import jakarta.annotation.Resource;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Tools工具类
 */
@Service
public class ToolsService {

    @Resource
    private TicketService ticketService;//实际调用的业务接口

    @Tool(description = "退票功能")//tool方法的功能
    public String cancel(
            @ToolParam(description = "用户名") String name,//需要什么样的参数
            @ToolParam(description = "退票单号") String numbers) {
        //调用其接口方法
        return ticketService.cancel(name,numbers);
    }

    public List<ToolCallback> getToolCallList(@Autowired ToolsService toolService) {
        //todo 读取数据库得到映射信息

        //获取tools方法
        Method method = ReflectionUtils.findMethod(ToolsService.class, "cancel",String.class,String.class);
        //手动动态的构建tool定义信息  @Tool  @ToolParam注解就不需要了
        ToolDefinition toolDefinition = ToolDefinition.builder()
                .name("cancel")
                .description("退票功能")
                .inputSchema("""
                        {
                          "type": "object",
                          "properties": {
                            "numbers": {
                              "type": "string",
                              "description": "订单号，可以是纯数字"
                            },
                            "name": {
                              "type": "string",
                              "description": "真实人名"
                            }
                          },
                          "required": ["numbers", "name"]
                        }
                        """)
                .build();
        //一个toolCallback对应一个tool
        assert method != null;
        ToolCallback toolCallback = MethodToolCallback.builder()
                .toolMethod(method)
                .toolDefinition(toolDefinition)
                .toolObject(toolService)
                .build();

        return List.of(toolCallback);
    }
}
