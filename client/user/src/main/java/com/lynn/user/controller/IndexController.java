package com.lynn.user.controller;

import com.lynn.user.api.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class IndexController {

    @Autowired
    private IndexService indexService;

    @RequestMapping(value = "index",method = RequestMethod.GET)
    public String index(){
        return indexService.index();
    }
}
