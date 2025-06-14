package org.example.chatserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.example.chatserver.api.entity.RespBean;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@Configuration
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {
  @Override
  public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp, AuthenticationException exception) throws IOException, ServletException {
    System.out.println("AuthenticationFailureHandler");
    resp.setContentType("application/json;charset=utf-8");
    PrintWriter out=resp.getWriter();
    RespBean error = RespBean.error("登录失败!");
    if (exception instanceof LockedException){
      error.setMsg("账户已锁定，请联系管理员！");
    }else if (exception instanceof CredentialsExpiredException){
      error.setMsg("密码已过期，请联系管理员！");
    }else if (exception instanceof AccountExpiredException){
      error.setMsg("账户已过期，请联系管理员！");
    }else if (exception instanceof DisabledException){
      error.setMsg("账户被禁用，请联系管理员!");
    }else if (exception instanceof BadCredentialsException){
      error.setMsg("用户名或密码错误，请重新输入！");
    }
    String s = new ObjectMapper().writeValueAsString(error);
    out.write(s);
    out.flush();
    out.close();
  }
}
