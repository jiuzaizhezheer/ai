package com.hzy.client;


import org.springframework.ai.chat.model.ChatModel;

public class PlatformsAndModelsDTO {

    private String msg;  //用户传入的消息
    private String platform;  //平台
    private String model;  //模型

    //额外配置
    private Double temperature;  //温度


    public PlatformsAndModelsDTO() {
    }

    public PlatformsAndModelsDTO(String msg, String platform, String model, Double temperature) {
        this.msg = msg;
        this.platform = platform;
        this.model = model;
        this.temperature = temperature;
    }

    /**
     * 获取
     * @return msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置
     * @param msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取
     * @return platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * 设置
     * @param platform
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * 获取
     * @return model
     */
    public String getModel() {
        return model;
    }

    /**
     * 设置
     * @param model
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * 获取
     * @return temperature
     */
    public Double getTemperature() {
        return temperature;
    }

    /**
     * 设置
     * @param temperature
     */
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String toString() {
        return "PlatformsAndModelsDTO{msg = " + msg + ", platform = " + platform + ", model = " + model + ", temperature = " + temperature + "}";
    }
}
