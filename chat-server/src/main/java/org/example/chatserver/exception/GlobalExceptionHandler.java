package org.example.chatserver.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.example.chatserver.api.entity.RespBean;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;


@RestControllerAdvice
public class GlobalExceptionHandler {

  /*
  处理SQLException异常
   */
  @ExceptionHandler(SQLException.class)
  public RespBean sqlExceptionHandler(SQLException e){
    if (e instanceof SQLIntegrityConstraintViolationException){
      return RespBean.error("该数据与其他数据存在关联，无法删除！");
    }
    else {
      e.printStackTrace();
      return RespBean.error("数据库异常，操作失败！");
    }
  }
}
