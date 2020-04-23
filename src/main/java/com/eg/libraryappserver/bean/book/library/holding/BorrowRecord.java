package com.eg.libraryappserver.bean.book.library.holding;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 一本barCode的借阅记录
 *
 * @time 2020-04-22 11:02
 */
@Data
@Document
public class BorrowRecord {
    @Id
    private String _id;

    private String bookId;
    private Date createTime;

    private String logType;
    private String rdid;
    private long loanDate;
    private long returnDate;
    private String regTime;
    private String year;
    private String serNo;
    private String holding;
    private String biblios;
    private String barcode;
    private int ruleState;
    private int loanCount;
    private long dueTime;
    private int attachMent;
    private String barcodeList;
    private String returnDateInStr;
    private String loanDateInStr;
    private String regTimeInStr;
    private String rowid;
    private String ruleno;
    private boolean underlease;
    private String loannumsign;
    private String state;
    private String rowidList;
}
