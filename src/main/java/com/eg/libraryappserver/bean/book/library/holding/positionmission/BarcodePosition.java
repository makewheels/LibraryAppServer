package com.eg.libraryappserver.bean.book.library.holding.positionmission;

import lombok.Data;

import java.io.Serializable;

/**
 * @time 2020-04-23 00:08
 */
@Data
public class BarcodePosition implements Serializable {
    private String barcode;
    private String position;
    private long timestamp;
    private String sign;
}
