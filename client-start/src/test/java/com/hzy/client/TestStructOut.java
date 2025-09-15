package com.hzy.client;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestStructOut {
    private ChatClient chatClient;
    @BeforeEach
    public  void init(@Autowired
                      DashScopeChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    /**
     * 结构化输出
     */
    @Test
    public void testBoolOut() {
        Boolean isComplain = chatClient
                .prompt()
                .system("""
            请判断用户信息是否表达了投诉意图?
            只能用 true 或 false 回答，不要输出多余内容
            """)
                .user("你们家的快递迟迟不到,我要退货！")
                .call()
                .entity(Boolean.class);

        // 分支逻辑
        if (Boolean.TRUE.equals(isComplain)) {
            System.out.println("用户是投诉，转接人工客服！");
        } else {
            System.out.println("用户不是投诉，自动流转客服机器人。");
            //可继续调用 客服ChatClient进行对话
        }
    }

    //内部类
    public record Address(
            String name,        // 收件人姓名
            String phone,       // 联系电话
            String province,    // 省
            String city,        // 市
            String district,    // 区/县
            String detail       // 详细地址
    ) {}

    /**
     * 转化为Address结构输出
     */
    @Test
    public void testEntityOut() {
        Address address = chatClient.prompt()
                .system("""
                        请从下面这条文本中提取收货信息
                        """)
                .user("收货人：蔡徐坤，电话13588888888，地址：浙江省杭州市西湖区文一西路100号8幢202室")
                .call()
                .entity(Address.class);
        System.out.println(address);
    }
}
