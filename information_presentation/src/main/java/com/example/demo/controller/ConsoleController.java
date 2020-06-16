package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ConsoleController {
    @RequestMapping("/getResult")
    public ModelAndView show(){
        return new ModelAndView("analyse");
    }

    @RequestMapping("/test")
    @ResponseBody
    public String test(){
        return "upload";
    }

}
