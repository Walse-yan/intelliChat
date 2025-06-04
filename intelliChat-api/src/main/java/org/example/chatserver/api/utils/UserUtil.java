package org.example.chatserver.api.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.example.chatserver.api.entity.User;


public class UserUtil {
  /**
   * 获取当前登录用户实体
   * @return
   */
  public static User getCurrentUser(){
    System.out.println("UserUtil.getCurrentUser()");
    System.out.println(SecurityContextHolder.getContext().getAuthentication());
    return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
  }
}
