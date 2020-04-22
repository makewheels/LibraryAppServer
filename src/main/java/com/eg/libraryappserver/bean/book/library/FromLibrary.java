package com.eg.libraryappserver.bean.book.library;

import com.eg.libraryappserver.bean.book.library.imageapi.holding.Holding;
import lombok.Data;

import java.util.List;

/**
 * 爬虫获取的，来自图书馆的数据
 *
 * @time 2020-04-21 23:38
 */
@Data
public class FromLibrary {
    private String coverlink;
    private String resourceLink;
    private String isbn;
    private String bookrecno;
    private String title;
    private String author;
    private String publisher;
    private String publishDate;
    private String type;
    private String callno;
    //barcode数据
    private List<Holding> holdingList;
    private int borrowCount;
    private int renewCount;
}
