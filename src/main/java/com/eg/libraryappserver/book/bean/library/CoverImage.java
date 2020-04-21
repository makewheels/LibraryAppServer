package com.eg.libraryappserver.book.bean.library;

import lombok.Data;

/**
 * 书的封面图片
 *
 * @time 2020-04-21 23:22
 */
@Data
public class CoverImage {
    private String libraryUrl;  //图书馆url
    private String doubanUrl;   //豆瓣url
}
