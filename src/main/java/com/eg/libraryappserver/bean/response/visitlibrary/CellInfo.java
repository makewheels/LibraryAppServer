package com.eg.libraryappserver.bean.response.visitlibrary;

import lombok.Data;

import java.util.List;

/**
 * 一个cell的info
 * 包括当前cell有哪些书的id
 * 还包括能不能往上下左右移动，如果能，给出他们的position
 */
@Data
public class CellInfo {
    private List<String> bookIdList;
    private PositionResponse current;
    private PositionResponse up;
    private PositionResponse down;
    private PositionResponse left;
    private PositionResponse right;
}
