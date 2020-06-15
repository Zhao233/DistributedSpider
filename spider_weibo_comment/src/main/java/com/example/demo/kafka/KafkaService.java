package com.example.demo.kafka;

import com.example.demo.Spider;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;

public class KafkaService {
    String topic = "content_id";

    @KafkaListener(id = "demo", topics = "content_id")
    public void listen(String content_id) {
        Spider.weibo_id_queue.add(content_id);
    }

}
