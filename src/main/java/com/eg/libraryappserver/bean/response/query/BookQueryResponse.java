package com.eg.libraryappserver.bean.response.query;

import lombok.Data;

import java.util.List;

/**
 * 相应给客户端的，查询书的列表
 *
 * @time 2020-04-17 20:50
 */
@Data
public class BookQueryResponse {
    private String q;
    private List<BookQueryRecord> bookRecordList;
    private int amount;

}

