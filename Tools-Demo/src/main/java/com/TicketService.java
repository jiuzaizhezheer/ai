package com;

import org.springframework.stereotype.Service;

/**
 * 原本的业务类
 */
@Service
public class TicketService {
    /**
     * 退票方法
     * @param name
     * @param numbers
     * @return
     */
    public String cancel(String name,String numbers){
        return "退票成功";
    }
}
