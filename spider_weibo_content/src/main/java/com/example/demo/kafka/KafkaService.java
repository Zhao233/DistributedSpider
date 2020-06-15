package com.example.demo.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {
    @Autowired
    KafkaTemplate kafkaTemplate;

    public void sendMessageToKafka(String message){
        kafkaTemplate.send("content_id", message);
    }
}
