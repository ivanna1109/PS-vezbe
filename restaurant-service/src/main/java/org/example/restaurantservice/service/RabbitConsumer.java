package org.example.restaurantservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.restaurantservice.configs.RabbitMQConfig;
import org.example.dtos.OrderEventDTO;
import org.example.restaurantservice.repository.RestaurantRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class RabbitConsumer {

    //@RabbitListener(queues = RabbitMQConfig.QUEUE)
    @RabbitListener(queues = "student_queue")
    public void consume(String message) {
        System.out.println("LOG IZ SERVISA B: Primljena poruka -> " + message);
    }

    //vezbe 10

    @Autowired
    private RestaurantRepository repository;
    @Autowired
    private RabbitTemplate rabbitTemplate;


    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleOrder(Message message) { //OrderEventDTO
        OrderEventDTO event = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            event = mapper.readValue(message.getBody(), OrderEventDTO.class);

            System.out.println("USPEŠNO PRIMLJEN EVENT ZA ORDER: " + event);
            if (event.getTotalPrice() < 0) throw new RuntimeException("Nevalidna cena!");
            System.out.println("Restoran priprema hranu za: " + event.getCustomerName());
            event.setStatus("CONFIRMED");
            rabbitTemplate.convertAndSend("order_feedback_exchange", "feedback_key", event);

            System.out.println("Feedback poslat za porudžbinu: " + event.getOrderId());
        } catch (Exception e) {
            if (event!= null) {
                event.setStatus("REJECTED");
                rabbitTemplate.convertAndSend("order_feedback_exchange", "feedback_key", event);
            }
            else {
                System.out.println("Obrada porudzbine..: ");
            }
        }
    }
}
