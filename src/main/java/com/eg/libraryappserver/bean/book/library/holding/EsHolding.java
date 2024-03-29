package com.eg.libraryappserver.bean.book.library.holding;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@EqualsAndHashCode
@Document(indexName = "holding", type = "docs")
public class EsHolding {
    @Id
    private String esId;

    @Field(type = FieldType.Keyword)
    private String mongoId;
    @Field(type = FieldType.Keyword)

    private String bookId;

    @Field(type = FieldType.Boolean)
    private boolean hasPosition;//是否有位置

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String detailPosition;//具体的位置

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String room;    //文献借阅一室

    @Field(type = FieldType.Integer)
    private int row;        //排

    @Field(type = FieldType.Keyword)
    private String side;    //面

    @Field(type = FieldType.Integer)
    private int shelf;      //架

    @Field(type = FieldType.Integer)
    private int level;      //层

}
