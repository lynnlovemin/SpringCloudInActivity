package com.lynn.index.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class IndexController {

    @RequestMapping(value = "index",method = RequestMethod.GET)
    public String index(){
        try {
            Thread.sleep(20);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "hello world!";
    }
}
