package com.eg.libraryappserver.bean.book.library.holding;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class Holding {
    @Id
    private String _id;

    private String bookId;
    private Position position;
    private Date createTime;
    private Date updateTime;

    private long recno;
    private long bookrecno;
    private int state;
    private String barcode;
    private String callno;
    private String orglib;
    private String orglocal;
    private String curlib;
    private String curlocal;
    private String cirtype;
    private Date regdate;
    private Date indate;
    private double singlePrice;
    private double totalPrice;
    private int totalLoanNum;
    private int totalResNum;
    private int totalRenewNum;
    private int totalLibNum;
    private int volnum;
    private String volInfo;
    private String memoinfo;
    private String shelfno;
    private String regno;
    private String biblios;
    private String loan;
    private String packageno;
    private String stateStr;
}