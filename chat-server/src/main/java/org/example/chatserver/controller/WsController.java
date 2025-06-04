package org.example.chatserver.controller;

import com.github.binarywang.java.emoji.EmojiConverter;
import org.example.chatserver.service.AsyncChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.example.chatserver.api.entity.GroupMsgContent;
import org.example.chatserver.api.entity.Message;
import org.example.chatserver.api.entity.User;
import org.example.chatserver.api.utils.TuLingUtil;
import org.example.chatserver.service.GroupMsgContentService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
public class WsController {

  @Autowired
  private AsyncChatService asyncChatService;

  @MessageMapping("/ws/chat")
  public void handleMessage(Authentication authentication, Message message){
    User user = (User) authentication.getPrincipal();
    asyncChatService.sendPrivateMessage(user, message); // 异步处理单聊
  }

  @MessageMapping("/ws/groupChat")
  public void handleGroupMessage(Authentication authentication, GroupMsgContent groupMsgContent){
    User currentUser = (User) authentication.getPrincipal();
    asyncChatService.sendGroupMessage(currentUser, groupMsgContent); // 异步处理群聊
  }

  @MessageMapping("/ws/robotChat")
  public void handleRobotChatMessage(Authentication authentication, Message message) throws IOException {
    User user = (User) authentication.getPrincipal();
    asyncChatService.handleRobotMessage(user, message); // 异步处理机器人聊天
  }
}
