package com.eg.libraryappserver.bean.book;

import com.eg.libraryappserver.autoincrease.AutoIncrement;
import com.eg.libraryappserver.bean.book.douban.FromDouban;
import com.eg.libraryappserver.bean.book.library.FromLibrary;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

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
    @AutoIncrement
    private long index;

    private String bookId;
    private Date createTime;
    private Date updateTime;

    private FromLibrary fromLibrary;
    private FromDouban fromDouban;

    //整合资源
    private String isbn;            //从图书馆网站爬下来，带横杠
    private String callno;          //索书号，例如：K837.127/57
    private String bookrecno;       //书的id，例如：127796

    private String title;           //书名
    private String author;          //作者列表
    private String publisher;       //出版社
    private String publishDate;     //出版日期
    private String catalog;         //目录
    private String summary;         //简述
    private String coverUrl;        //封面图片

}