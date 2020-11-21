package com.eg.libraryappserver.bean.book.library.holding;

import lombok.Data;

import java.util.Date;

/**
 * barCode位置
 *
 * @time 2020-04-22 22:30
 */
@Data
//加入es搜索
public class Position {
    private String message;      //图书未上架，无法定位！ 非自助借还(RFID)图书，无法定位！
    private String provider;

    private String position;     //01040201300402|文献借阅二室 7排A面4架2层
    private String coordinate;   //坐标：01040201300402
    private String room;         //文献借阅一室
    private String detailPosition;//7排A面4架2层

    private int row;        //排
    private String side;    //面
    private int shelf;      //架
    private int level;      //层

    private Date createTime;
    private Date updateTime;
}
