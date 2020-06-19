package com.example.demo.nlp;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TokenHelper {
    Logger log = LoggerFactory.getLogger(TokenHelper.class);

    @Value("${baidu.auth_url}")
    private String url;

    @Value("${baidu.grant_type}")
    private String grant_type;

    @Value("${baidu.client_id}")
    private String client_id;

    @Value("${baidu.client_secret}")
    private String client_secret;

    public static String accessToken = null;

    //获取百度ai token
    public void getAccessToken(){
        RestTemplate restTemplate = new RestTemplate();

        String request_url = url+"?grant_type="+grant_type+"&client_id="+client_id+"&client_secret="+client_secret;

        String res = restTemplate.getForObject(request_url, String.class);

        JSONObject json = JSONObject.fromObject(res);

        if(json.get("error" ) != null ) {
            accessToken = null;
            log.error("获取accessToken失败，失败原因 ： " + json.get("error_description"));
            return;
        }

        accessToken = json.getString("access_token");
    }



}
