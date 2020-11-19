package com.eg.libraryappserver.bean.response.basicinfo;

import lombok.Data;

@Data
public class BookBasicInfo {
    private String mongoId;
    private String bookId;
    private String isbn;            //从图书馆网站爬下来，带横杠
    private String callno;          //索书号，例如：K837.127/57
    private String bookrecno;       //书的id，例如：127796

    private String title;           //书名
    private String author;          //作者列表
    private String publisher;       //出版社
    private String publishDate;     //出版日期
    private String coverUrl;        //封面图片
}
