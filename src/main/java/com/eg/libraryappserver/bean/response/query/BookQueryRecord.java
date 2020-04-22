package com.eg.libraryappserver.bean.response.query;

import lombok.Data;

import java.util.List;

/**
 * @time 2020-04-17 20:57
 */
@Data
public class BookQueryRecord {
    private String mangoId;
    private String isbn;            //isbn从图书馆网站爬下来，带横杠
    private String isbn13;          //没有横杠，纯数字
    private String callno;          //索书号
    private String bookrecno;       //书的id，例如：127796
    private String coverImageUrl;   //封面图片url
    private String title;           //书名
    private String subtitle;        //子书名
    private List<String> authorList;//作者列表
    private String publisher;       //出版社
    private String publishDate;     //出版日期
}
