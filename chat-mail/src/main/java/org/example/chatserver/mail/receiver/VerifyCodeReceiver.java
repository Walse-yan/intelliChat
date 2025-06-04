package org.example.chatserver.mail.receiver;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.messaging.Message;
import org.springframework.amqp.core.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Date;

@Component
public class VerifyCodeReceiver {
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    StringRedisTemplate redisTemplate;

    private static final Logger LOGGER= LoggerFactory.getLogger(VerifyCodeReceiver.class);

    @Value("${spring.mail.username}")
    private String mail_sender;

    @Value("1159167391@qq.com")
    private String mail_receiver;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "${mail.queue.verifyCode:mail-queue-verifyCode}")
    public void getMessage(Message message, Channel channel) throws IOException {
        //获取消息内容
        String code = new String(message.getBody());
        //获取消息ID
        String msgId = message.getMessageProperties().getMessageId();
        // 如果msgID为null，则直接返回
        if (msgId == null) {
            LOGGER.warn("消息ID为空，无法处理消息");
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            return;
        }

        LOGGER.info("【"+msgId+"】-正在处理的消息");
        //查看消息是否已消费
        if (redisTemplate.opsForHash().entries("mail_log").containsKey(msgId)){
            //手动确认消息已消费
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            LOGGER.info("【"+msgId+"】消息出现重复消费");
            return;
        }
        //否则进行消息消费
        try{
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setSubject("智能聊天室管理端-验证码验证");
            //TODO 使用Thymeleaf，改邮件模板,添加内容：请不要泄露自己的邮箱验证码
            msg.setText("本次登录的验证码："+code);
            msg.setFrom(mail_sender);
            msg.setSentDate(new Date());
            msg.setTo(mail_receiver);
            javaMailSender.send(msg);
            //消息发送成功，将id放到redis中,不能这样put
            //redisTemplate.opsForHash().entries("mail_log").put(msgId,code);
            redisTemplate.opsForHash().put("mail_log",msgId,code);
            //确认消息消费成功
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            //不批量处理，将消息重新放回到队列中
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            LOGGER.info("【"+msgId+"】消息重新放回到了队列中");
            e.printStackTrace();
        }
    }
}
