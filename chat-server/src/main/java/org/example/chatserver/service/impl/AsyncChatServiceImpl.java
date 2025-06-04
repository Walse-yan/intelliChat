package org.example.chatserver.service.impl;

import com.github.binarywang.java.emoji.EmojiConverter;
import org.example.chatserver.api.entity.GroupMsgContent;
import org.example.chatserver.api.entity.Message;
import org.example.chatserver.api.entity.User;
import org.example.chatserver.api.utils.TuLingUtil;
import org.example.chatserver.service.AsyncChatService;
import org.example.chatserver.service.GroupMsgContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.example.chatserver.service.ChatBotService;
import java.io.IOException;
import java.util.Date;

@Service("asyncChatService")
public class AsyncChatServiceImpl implements AsyncChatService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private GroupMsgContentService groupMsgContentService;

    @Autowired
    private ChatBotService chatBotService;

    EmojiConverter emojiConverter = EmojiConverter.getInstance();

    @Async("chatExecutor")
    public void sendPrivateMessage(User fromUser, Message message) {
        message.setFromNickname(fromUser.getNickname());
        message.setFrom(fromUser.getUsername());
        message.setCreateTime(new Date());
        simpMessagingTemplate.convertAndSendToUser(message.getTo(), "/queue/chat", message);
    }

    @Async("chatExecutor")
    public void sendGroupMessage(User currentUser, GroupMsgContent groupMsgContent) {
        groupMsgContent.setContent(emojiConverter.toHtml(groupMsgContent.getContent()));
        groupMsgContent.setFromId(currentUser.getId());
        groupMsgContent.setFromName(currentUser.getNickname());
        groupMsgContent.setFromProfile(currentUser.getUserProfile());
        groupMsgContent.setCreateTime(new Date());
        groupMsgContentService.insert(groupMsgContent);
        simpMessagingTemplate.convertAndSend("/topic/greetings", groupMsgContent);
    }

    @Async("chatExecutor")
    public void handleRobotMessage(User user, Message message) throws IOException {
        message.setFrom(user.getUsername());
        message.setCreateTime(new Date());
        message.setFromNickname(user.getNickname());
        message.setFromUserProfile(user.getUserProfile());

//        String result = TuLingUtil.replyMessage(message.getContent());
//
//        Message resultMessage = new Message();
//        resultMessage.setFrom("瓦力");
//        resultMessage.setCreateTime(new Date());
//        resultMessage.setFromNickname("瓦力");
//        resultMessage.setContent(result);

        // 调用 Ollama 模型
        String result = chatBotService.getBotResponse(message.getContent());

        Message resultMessage = new Message();
        resultMessage.setFrom("OllamaBot");
        resultMessage.setCreateTime(new Date());
        resultMessage.setFromNickname("OllamaBot");
        resultMessage.setContent(result);

        simpMessagingTemplate.convertAndSendToUser(message.getFrom(), "/queue/robot", resultMessage);
    }
}
