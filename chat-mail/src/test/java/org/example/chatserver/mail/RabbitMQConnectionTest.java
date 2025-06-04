package org.example.chatserver.mail;

import com.rabbitmq.client.*;

public class RabbitMQConnectionTest {
    private static final String QUEUE_NAME = "TestQueue";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("100.100.53.107");
        factory.setVirtualHost("intelliChat");
        factory.setPort(5672);
        factory.setUsername("yanjq");
        factory.setPassword("yanjq2024");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Declare a queue
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            System.out.println("Queue declared: " + QUEUE_NAME);

            // Publish a message
            String message = "Hello, RabbitMQ!";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("Message sent: " + message);

            // Consume the message
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String receivedMessage = new String(delivery.getBody(), "UTF-8");
                System.out.println("Message received: " + receivedMessage);
            };
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});

        } catch (Exception e) {
            System.err.println("Failed to connect to RabbitMQ or process messages: " + e.getMessage());
        }
    }
}