package org.example.chatserver.service;

import org.example.chatserver.api.entity.GroupMsgContent;
import org.example.chatserver.api.entity.Message;
import org.example.chatserver.api.entity.User;

import java.io.IOException;

public interface AsyncChatService {
    /**
     * 私聊
     *
     * @param fromUser 发送者
     * @param message 消息内容
     */
    void sendPrivateMessage(User fromUser, Message message);

    /**
     * 群聊
     *
     * @param currentUser 当前用户
     * @param groupMsgContent 群聊消息内容
     */
    void sendGroupMessage(User currentUser, GroupMsgContent groupMsgContent);

    /**
     * 处理机器人消息
     *
     * @param user 发送者
     * @param message 消息内容
     */
    void handleRobotMessage(User user, Message message) throws IOException;
}
