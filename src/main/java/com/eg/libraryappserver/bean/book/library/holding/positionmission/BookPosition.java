package com.eg.libraryappserver.bean.book.library.holding.positionmission;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @time 2020-04-23 00:08
 */
@Data
public class BookPosition implements Serializable {
    private String bookId;
    private List<BarcodePosition> barcodePositionList;
}
