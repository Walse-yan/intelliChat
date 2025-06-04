package org.example.chatserver.mail.receiver;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.amqp.core.Message;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.example.chatserver.api.entity.Feedback;
import org.example.chatserver.api.utils.JsonUtil;

import java.io.IOException;
import java.util.Date;

@Component
public class FeedbackReceiver {

    private static final Logger LOGGER= LoggerFactory.getLogger(FeedbackReceiver.class);

    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    StringRedisTemplate redisTemplate;

    @Value("${spring.mail.username}")
    private String mail_sender;

    @Value("1159167391@qq.com")
    private String mail_receiver;

    @RabbitListener(queues ="${mail.queue.feedback:mail-queue-feedback}")
    public void getFeedbackMessage(Message message, Channel channel) throws IOException {
        //获取消息内容
        String s = message.getPayload().toString();
//        String s = new String(message.getBody());
        //获取消息的唯一标志
        MessageHeaders headers = message.getHeaders();
//        MessageHeaders headers = (MessageHeaders) message.getMessageProperties().getHeaders();
        Long tag = ((Long) headers.get(AmqpHeaders.DELIVERY_TAG));

        // 检查 spring_returned_message_correlation 是否存在
        Object correlationId = headers.get("spring_returned_message_correlation");
//        if (correlationId == null) {
//            LOGGER.error("消息头中缺少 spring_returned_message_correlation");
//            System.out.println("消息头中缺少 spring_returned_message_correlation");
//            channel.basicNack(tag, false, true); // 将消息重新放回队列
//            return;
//        }

        String msgId = headers.get("spring_returned_message_correlation").toString();
        LOGGER.info("【"+msgId+"】-正在处理的消息");
        //是否已消费
        if (redisTemplate.opsForHash().entries("mail_log").containsKey(msgId)){
            channel.basicAck(tag,true);
            LOGGER.info("【"+msgId+"】消息出现重复消费");
            return;
        }
        try{
            Feedback feedback = JsonUtil.parseToObject(s, Feedback.class);
            System.out.println(feedback.getContent());
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setSubject("来自用户的意见反馈");
            //读取信息
            StringBuilder formatMessage = new StringBuilder();
            formatMessage.append("用户编号：").append(feedback.getUserId()).append("\n");
            formatMessage.append("用户名：").append(feedback.getUsername()).append("\n");
            formatMessage.append("用户昵称：").append(feedback.getNickname()).append("\n");
            formatMessage.append("反馈内容：").append(feedback.getContent());
            System.out.println(">>>>>>>>>>>>>>"+formatMessage+"<<<<<<<<<<<<<<<<<<");
            //设置邮件消息
            mailMessage.setText(formatMessage.toString());
            mailMessage.setFrom(mail_sender);
            mailMessage.setTo(mail_receiver);
            mailMessage.setSentDate(new Date());
            javaMailSender.send(mailMessage);
            //消息处理完成
           // redisTemplate.opsForHash().entries("mail_log").put(msgId,msgId+1);
            redisTemplate.opsForHash().put("mail_log",msgId,feedback.getContent());
            //手动确定消息处理完成
            channel.basicAck(tag,true);
        }catch (Exception e){
            //出现异常就重新放回到队列中
            channel.basicNack(tag,false,true);
            LOGGER.info("【"+msgId+"】消息重新放回到了队列中");
            e.printStackTrace();
        }

    }
}
