package com.example.demo.nlp;

import lombok.Synchronized;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.locks.ReentrantLock;

import static com.example.demo.nlp.TokenHelper.accessToken;

@Service
public class Analyser {
    public static ReentrantLock lock = new ReentrantLock();

    Logger log = LoggerFactory.getLogger(Analyser.class);

    @Value("${baidu.analyse_url}")
    private String url;

    private final static int POSITIVE = 2;
    private final static int NEGATIVE = 0;
    private final static int MIDDLE = 1;

    @Synchronized
    public int analyse(String content) throws InterruptedException {
        if (accessToken == null) {
            log.error("未获取accessToken");

            return Integer.MIN_VALUE;
        }
        RestTemplate restTemplate = new RestTemplate();

        String request_url = url + "?charset=UTF-8&access_token=" + accessToken;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        JSONObject object = new JSONObject();
        object.put("text", content);

        HttpEntity<String> requestEntity = new HttpEntity<String>(object.toString(), headers);

        lock.lock();

        //System.out.println(Thread.currentThread().getName() + "获得锁");

        Thread.sleep(600);

        String s = restTemplate.postForEntity(request_url, requestEntity, String.class).getBody();

        //System.out.println(Thread.currentThread().getName() + "释放锁");
        lock.unlock();

        JSONObject res1 = JSONObject.fromObject(s);

        if (res1.get("error_code") != null) {
            log.error("处理错误！ 原因: " + s);

            return Integer.MIN_VALUE;
        }

        JSONObject res2 = null;
        try{
            res2 = res1.getJSONArray("items").getJSONObject(0);
        }catch (Exception e){
            e.printStackTrace();
        }

        int sentiment = res2.getInt("sentiment");
        int score = (int) (res2.getDouble("positive_prob")*100);

//        switch (sentiment){
//            case POSITIVE :
//                score = (int) (res2.getDouble("positive_prob")*100);
//                break;
//            case NEGATIVE :
//                score = (int) res2.getDouble("negative_prob")*100;
//                break;
//            case MIDDLE :
//                score = 50;
//        }

        return score;
        //printResult(result,"社会信用代码");
    }
}
