package com.eg.libraryappserver.bean.book.library.holding;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "holding", type = "doc",
        useServerConfiguration = true, createIndex = false)
public class EsHolding {
    @Id
    private Integer id;

    private String mongoId;
    private String bookId;
    private Position position;

    @Field(type = FieldType.Text)
    private String room;    //文献借阅一室

    @Field(type = FieldType.Integer)
    private int row;        //排

    @Field(type = FieldType.Text)
    private String side;    //面

    @Field(type = FieldType.Integer)
    private int shelf;      //架

    @Field(type = FieldType.Integer)
    private int level;      //层

}
