package org.example.chatserver.controller;

import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.example.chatserver.api.utils.FastDFSUtil;

import java.io.IOException;

@RestController
public class FileController {

  @Value("${fastdfs.nginx.host:http://100.100.53.107:8889/}")
  String nginxHost;

  @PostMapping("/file")
  public String uploadFlie(MultipartFile file) throws IOException, MyException {
    String fileId = FastDFSUtil.upload(file);
    String url = nginxHost + fileId;
    System.out.println("upload_url = " + url);
    return url;
  }
}
