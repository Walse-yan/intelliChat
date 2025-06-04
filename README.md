# 智能聊天室
## 功能描述：
基于SpringBoot+Vue前后端分离的大模型智能聊天室
## 版本：
Springboot3
## 技术栈：
Spring Security、WebSocket、MyBatis、MySQL、Redis、RabbitMQ、线程池、Ollama、FastDFS分布式文件系统
## 功能描述：
1)通过Spring Security、过滤器和数据库进行密码加密和登录登出等逻辑实现
2)利用WebSocket、Stomp协议实现消息的单发、群聊等信息发送、接收和监听功能，并引入心跳检测维持通信的稳定性
3)利用FastDFS文件服务器实现文件的保存，并利用nginx进行代理访问
4)利用RabbitMQ消息队列对邮件验证码进行代理，实现异步处理和解耦，并借助Redis防止重复消费和做消息确认处理，保证消息的可靠性
5)引入线程池异步执行聊天任务，通过设置线程池参数，优化性能和资源管理
6)借助Ollama部署qwen2等大模型，搭建机器人聊天功能
7)借助Mysql实现消息的持久化，通过分页查询的方式对群聊信息进行查询


