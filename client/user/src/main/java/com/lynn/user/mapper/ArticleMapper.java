package com.lynn.user.mapper;

import com.lynn.user.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleMapper {

    @Select("select id,title,summary from news_article")
    List<Article> searchArticleList();
}
