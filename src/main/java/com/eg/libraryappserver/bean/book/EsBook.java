package com.eg.libraryappserver.bean.book;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EsBook esBook = (EsBook) o;
        return Objects.equals(mongoId, esBook.mongoId) &&
                Objects.equals(bookId, esBook.bookId) &&
                Objects.equals(isbn, esBook.isbn) &&
                Objects.equals(callno, esBook.callno) &&
                Objects.equals(bookrecno, esBook.bookrecno) &&
                Objects.equals(title, esBook.title) &&
                Objects.equals(author, esBook.author) &&
                Objects.equals(publisher, esBook.publisher) &&
                Objects.equals(publishDate, esBook.publishDate) &&
                Objects.equals(catalog, esBook.catalog) &&
                Objects.equals(summary, esBook.summary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mongoId, bookId, isbn, callno, bookrecno, title, author, publisher, publishDate, catalog, summary);
    }
}
