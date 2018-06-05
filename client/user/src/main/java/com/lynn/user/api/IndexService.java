package com.lynn.user.api;

import com.lynn.user.api.error.IndexErrorService;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "index",fallback = IndexErrorService.class)
public interface IndexService {

    @RequestMapping(value = "/index",method = RequestMethod.GET)
    String index();
}
