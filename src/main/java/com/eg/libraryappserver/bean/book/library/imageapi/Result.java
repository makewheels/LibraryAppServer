package com.eg.libraryappserver.bean.book.library.imageapi;

import lombok.Data;

import java.util.Date;

/**
 * @time 2020-04-22 16:28
 */
@Data
public class Result {
    private String metaResID;
    private String isbn;
    private String coverlink;
    private String resourceLink;
    private Date handleTime;
    private String fromRes;
    private int status;
}