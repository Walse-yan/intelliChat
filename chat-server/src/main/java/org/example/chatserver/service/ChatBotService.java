package org.example.chatserver.service;

public interface ChatBotService {
    /**
     * 获取机器人对用户消息的回复
     * @param userMessage 用户输入
     * @return 机器人回复
     */
    String getBotResponse(String userMessage);
}
