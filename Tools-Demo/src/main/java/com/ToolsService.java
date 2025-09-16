package com;

import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

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
}
