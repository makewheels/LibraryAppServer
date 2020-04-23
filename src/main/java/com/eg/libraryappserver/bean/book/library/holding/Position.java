package com.eg.libraryappserver.bean.book.library.holding;

import lombok.Data;

import java.util.Date;

/**
 * barCode位置
 *
 * @time 2020-04-22 22:30
 */
@Data
public class Position {
    private String position;
    private String provider;
    private String roomName;//文献借阅一室
    private int shelf;      //架
    private String side;    //面
    private int level;      //层
    private int pai;        //排
    private Date createTime;
    private Date updateTime;
}
