package com.eg.libraryappserver.bean.response.detail;

import com.eg.libraryappserver.bean.book.library.holding.Position;
import lombok.Data;

import java.util.List;

/**
 * 返回给客户端的，书的详情页的数据
 *
 * @time 2020-04-18 19:24
 */
@Data
public class BookDetailResponse {
    private String mangoId;
    //一次图书馆爬虫所拿到的：
    private String isbn;            //isbn从图书馆网站爬下来，带横杠
    private String callno;          //索书号
    private String bookrecno;       //书的id，例如：127796

    //豆瓣api拿到的：
    private String isbn13;          //没有横杠，纯数字
    private String coverUrl;        //封面图片url
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
    private Position position;      //位置
}
