package com.eg.libraryappserver.bean.book.douban;

import lombok.Data;

import java.util.List;

/**
 * 从豆瓣获得的数据
 */
@Data
public class FromDouban {
    private Rating rating;
    private String subtitle;
    private List<String> author;
    private String pubdate;
    private List<Tags> tags;
    private String origin_title;
    private String image;
    private String binding;
    private List<String> translator;
    private String catalog;
    private String pages;
    private Images images;
    private String alt;
    private String id;
    private String publisher;
    private String isbn10;
    private String isbn13;
    private String title;
    private String url;
    private String alt_title;
    private String author_intro;
    private String summary;
    private String price;
}