package org.example.restaurantservice.service;

import org.example.restaurantservice.configs.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitConsumer {

    //@RabbitListener(queues = RabbitMQConfig.QUEUE)
    @RabbitListener(queues = "student_queue")
    public void consume(String message) {
        System.out.println("LOG IZ SERVISA B: Primljena poruka -> " + message);
    }
}
