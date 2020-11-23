package com.eg.libraryappserver.bean.book;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "book", type = "doc", useServerConfiguration = true)
public class EsBook {
    @Id
    private String id;

    private String mongoId;
    private String bookId;

    private String isbn;            //从图书馆网站爬下来，带横杠
    private String callno;          //索书号，例如：K837.127/57
    private String bookrecno;       //书的id，例如：127796

    private String title;           //书名
    private String author;          //作者列表
    private String publisher;       //出版社
    private String publishDate;     //出版日期
    private String catalog;         //目录
    private String summary;         //简述
}
