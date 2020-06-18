package com.example.demo.controller;

import com.example.demo.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ShowDataController {
    @Autowired
    SearchService searchServices;

    @RequestMapping("/getData1")
    @ResponseBody
    public Map getData1(){
        HashMap<Object,Object> map = new HashMap();

        map.put("data", searchServices.getFirstGraphData(false));
        map.put("res", "success");

        return map;
    }

    @RequestMapping("/getData2")
    @ResponseBody
    public Map getData2(){
        HashMap<Object,Object> map = new HashMap();

        map.put("data", searchServices.getSecondGraphData(false));
        map.put("res", "success");

        return map;
    }

    @RequestMapping("/getData3")
    @ResponseBody
    public Map getData3(){
        HashMap<Object,Object> map = new HashMap();
        map.put("data", searchServices.getThirdGraphData(false));
        map.put("res", "success");

        return map;
    }

    @RequestMapping("/getScore")
    @ResponseBody
    public Map getScoreByTime(){
        HashMap<Object,Object> map = new HashMap();
        map.put("data", searchServices.getScoreData(false));
        map.put("res", "success");

        return map;
    }

}
