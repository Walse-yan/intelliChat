package org.example.chatserver.service;


public interface VerifyCodeService {

    String getVerifyCode();

    void sendVerifyCodeMail(String code);

}
