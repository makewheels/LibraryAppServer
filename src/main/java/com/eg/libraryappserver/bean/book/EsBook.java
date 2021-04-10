package com.eg.libraryappserver.bean.book;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "book")
public class EsBook {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String mongoId;

    @Field(type = FieldType.Keyword)
    private String bookId;

    @Field(type = FieldType.Keyword)
    private String isbn;            //从图书馆网站爬下来，带横杠

    @Field(type = FieldType.Text)
    private String callno;          //索书号，例如：K837.127/57

    @Field(type = FieldType.Keyword)
    private String bookrecno;       //书的id，例如：127796

    @Field(type = FieldType.Text, analyzer = "ik_max_wordfwe")
    private String title;           //书名

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String author;          //作者列表

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String publisher;       //出版社

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String publishDate;     //出版日期

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String catalog;         //目录

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String summary;         //简述

}
