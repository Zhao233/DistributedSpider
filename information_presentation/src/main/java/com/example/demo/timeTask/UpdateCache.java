package com.example.demo.timeTask;

import com.example.demo.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UpdateCache {
    @Autowired
    SearchService searchService;

    @Scheduled(cron = "0 0/2 * * * ?") //每10秒执行一次
    public void update(){
        searchService.getFirstGraphData(true);
        searchService.getSecondGraphData(true);
        searchService.getThirdGraphData(true);
        searchService.getScoreData(true);
    }
}
