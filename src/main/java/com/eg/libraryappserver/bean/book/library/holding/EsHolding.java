package com.eg.libraryappserver.bean.book.library.holding;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Objects;

@Data
@Document(indexName = "holding", type = "doc", useServerConfiguration = true)
public class EsHolding {
    @Id
    private String id;

    private String mongoId;
    private String bookId;

    @Field(type = FieldType.Boolean)
    private boolean hasPosition;//是否有位置

    @Field(type = FieldType.Text)
    private String detailPosition;//具体的位置

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EsHolding esHolding = (EsHolding) o;
        return hasPosition == esHolding.hasPosition &&
                row == esHolding.row &&
                shelf == esHolding.shelf &&
                level == esHolding.level &&
                Objects.equals(mongoId, esHolding.mongoId) &&
                Objects.equals(bookId, esHolding.bookId) &&
                Objects.equals(detailPosition, esHolding.detailPosition) &&
                Objects.equals(room, esHolding.room) &&
                Objects.equals(side, esHolding.side);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mongoId, bookId, hasPosition, detailPosition, room, row, side, shelf, level);
    }
}
