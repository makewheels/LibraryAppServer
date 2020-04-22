package com.eg.libraryappserver.bean.book.library.imageapi.holding;

import lombok.Data;

import java.util.Map;

/**
 * @time 2020-04-22 19:00
 */
@Data
public class LoanWorkMap {
    private Map<String, BorrowRecord> map;
}
