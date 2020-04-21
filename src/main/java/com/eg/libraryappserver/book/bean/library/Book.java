package com.eg.libraryappserver.book.bean.library;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * 一种书
 *
 * @author Administrator
 */
@Data
@Document
public class Book {
    @Id
    private String _id;

    //一次图书馆爬虫所拿到的：
    private String isbn;            //isbn从图书馆网站爬下来，带横杠
    private String callno;          //索书号，例如：K837.127/57
    private String bookrecno;       //书的id，例如：127796
    private Date createTime;        //创建时间
    private CoverImage coverImage;

    private List<Holding> holdingList;

    //豆瓣api拿到的：
    private String isbn13;          //没有横杠，纯数字
    private String coverImageUrl;   //封面图片url
    private String title;           //书名
    private String subtitle;        //子书名
    private String originTitle;     //原书名
    private List<String> authorList;//作者列表
    private String publisher;       //出版社
    private String publishDate;     //出版日期
    private String price;           //价格
    private String pages;           //页码数
    private String catalog;         //目录
    private String summary;         //简述
    private String doubanBookInfoJson;//豆瓣查到的信息

}