package com.eg.libraryappserver.bean.book.library.holding;

import lombok.Data;

/**
 * @time 2020-04-23 00:08
 */
@Data
public class BarcodePosition {
    private String barcode;
    private String position;
    private long timestamp;
    private String sign;
}
