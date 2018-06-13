package com.lynn.user.test;

import com.lynn.common.redis.Redis;
import com.lynn.user.Application;
import com.lynn.user.mapper.ArticleMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class TestDB {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private Redis redis;

    @Test
    public void test(){
        try {
//            redis.set("hello","world");
//            System.out.println(redis.get("hello"));
//            System.out.println(articleMapper.searchArticleList());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
