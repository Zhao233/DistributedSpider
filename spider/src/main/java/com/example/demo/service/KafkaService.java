package com.example.demo.service;

import com.example.demo.domain.WeiBo;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Service("kafkaService")
public class KafkaService {
    Logger log = LoggerFactory.getLogger(KafkaService.class);

    String postUrl = "http://localhost:8882/kafka/kafka_receiver_content";

    public void sendWeiBoListToKafkaSenderServer(LinkedList<WeiBo> list_weibo){
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String,Object> map = new LinkedMultiValueMap<>();
        map.add("data", list_weibo);

        HashMap<Object, Object> res = new HashMap<>();

        res = restTemplate.postForObject(postUrl, map, HashMap.class);

        if(!res.get("res").equals("success")){
            log.error("send error,  data : "+list_weibo.toString());
        } else {
            log.info("send success");
        }
    }
}
