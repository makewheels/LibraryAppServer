package com.eg.libraryappserver.bean.response.visitlibrary;

import lombok.Data;

@Data
public class PositionResponse {
    private String room;         //文献借阅一室
    private String detailPosition;//7排A面4架2层

    private int row;        //排
    private String side;    //面
    private int shelf;      //架
    private int level;      //层
}
