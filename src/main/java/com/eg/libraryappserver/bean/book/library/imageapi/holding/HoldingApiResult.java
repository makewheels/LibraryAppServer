package com.eg.libraryappserver.bean.book.library.imageapi.holding;

import lombok.Data;

/**
 * 通过查询这个网址得到
 * http://60.218.184.234:8091/opac/api/holding/225864?limitLibcodes=
 * 里面有条码号
 *
 * @time 2020-04-22 18:56
 */
@Data
public class HoldingApiResult {
    private LoanWorkMap loanWorkMap;
//    private List<HoldingList> holdingList;
//    private HoldStateMap holdStateMap;
//    private BarcodeLocationUrlMap barcodeLocationUrlMap;
//    private LibcodeDeferDateMap libcodeDeferDateMap;
//    private PBCtypeMap pBCtypeMap;
//    private LocalMap localMap;
//    private LibcodeMap libcodeMap;
}
