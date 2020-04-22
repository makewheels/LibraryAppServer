package com.eg.libraryappserver.bean.book.douban;

import lombok.Data;

@Data
public class Rating {
    private int max;
    private int numRaters;
    private String average;
    private int min;
}