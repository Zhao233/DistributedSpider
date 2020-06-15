package com.example.demo;

import com.example.demo.dao.WeiboDao;
import com.example.demo.nlp.TokenHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
class DemoApplicationTests {
    @Autowired
    Spider spider;

    @Autowired
    TokenHelper tokenHelper;

    @Autowired
    WeiboDao weiboDao;

    @Test
    void contextLoads() {
        //List<String> list = weiboDao.getAllContentIds();

        tokenHelper.getAccessToken();
        spider.init();

        try {
            spider.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getComment() throws IOException, InterruptedException {
        String aa = "</span>hahahahha";

        aa = aa.replaceAll("</[a-zA-Z][^>]*>", "");

        System.out.println(aa);

    }
}
