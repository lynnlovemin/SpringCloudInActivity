package com.lynn.comment;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringCloudApplication
@ComponentScan(basePackages = "com.lynn")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
