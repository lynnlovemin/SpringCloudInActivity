package com.lynn.user.api.error;

import com.lynn.user.api.IndexService;
import org.springframework.stereotype.Component;

@Component
public class IndexErrorService implements IndexService{

    @Override
    public String index() {
        return "网络异常";
    }
}
