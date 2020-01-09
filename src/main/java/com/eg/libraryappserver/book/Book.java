package com.eg.libraryappserver.book;

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

    private String isbn;// isbn
    private String callno;// 索书号
	private String bookrecno;// 书的id，例如：127796

	private String coverImageUrl;// 封面图片url
	private String name;// 书名
    private String author;// 作者
    private String publisher;// 出版社
    private String publishDate;// 出版日期
    private Date createTime;

}