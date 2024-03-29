package com.eg.libraryappserver.book;

import com.alibaba.fastjson.JSON;
import com.eg.libraryappserver.bean.book.Book;
import com.eg.libraryappserver.bean.book.library.holding.Position;
import com.eg.libraryappserver.bean.book.repository.BookRepository;
import com.eg.libraryappserver.bean.response.basicinfo.BookBasicInfo;
import com.eg.libraryappserver.bean.response.detail.BookDetailResponse;
import com.eg.libraryappserver.bean.response.query.BookQueryRecord;
import com.eg.libraryappserver.bean.response.query.BookQueryResponse;
import com.eg.libraryappserver.bean.response.visitlibrary.CellInfo;
import com.eg.libraryappserver.bean.response.visitlibrary.PositionResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @time 2020-01-09 21:52
 */
@RestController
@RequestMapping("/book")
public class BookController {
    private BookService bookService;
    private BookRepository bookRepository;

    @Autowired
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @Autowired
    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * 根据关键词查询书的列表
     *
     * @param q
     * @param page
     * @param size
     * @return
     */
    @RequestMapping("/search")
    public String queryBook(
            @RequestParam String q, @RequestParam int page, @RequestParam int size) {
        if (size != 20) {
            size = 20;
        }
        //分页查询
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Book> booksByTitle = bookRepository.findBookByTitleContains(q, pageRequest);
        if (booksByTitle == null) {
            return null;
        }
        BookQueryResponse bookQueryResponse = new BookQueryResponse();
        bookQueryResponse.setQ(q);
        bookQueryResponse.setAmount(booksByTitle.size());
        List<BookQueryRecord> bookQueryRecordList = new ArrayList<>();
        bookQueryResponse.setBookRecordList(bookQueryRecordList);
        //进行一波处理
        for (Book book : booksByTitle) {
            BookQueryRecord bookQueryRecord = new BookQueryRecord();
            //拷贝属性
            BeanUtils.copyProperties(book, bookQueryRecord);
            //设置mango id
            bookQueryRecord.setMangoId(book.get_id());
            bookQueryRecordList.add(bookQueryRecord);
        }
        return JSON.toJSONString(bookQueryResponse);
    }

    /**
     * 通过id查询书的详情信息
     *
     * @param mangoId
     * @return
     */
    @RequestMapping("/getBookDetail")
    public String getBookDetail(@RequestParam String mangoId) {
        //查询数据库
        Book book = bookRepository.findById(mangoId).get();
        BookDetailResponse bookDetailResponse = new BookDetailResponse();
        //拷贝属性
        BeanUtils.copyProperties(book, bookDetailResponse);
        bookDetailResponse.setMangoId(book.get_id());
        //搞定位置
        Position position = bookService.getSingleBookPosition(book.getBookId());
        PositionResponse positionResponse = new PositionResponse();
        BeanUtils.copyProperties(position, positionResponse);
        bookDetailResponse.setPositionResponse(positionResponse);
        return JSON.toJSONString(bookDetailResponse);
    }

    /**
     * 获取指定cell的书的id list
     *
     * @param room
     * @param row
     * @param side
     * @param shelf
     * @param level
     * @return
     */
    @RequestMapping("/getTargetCellInfo")
    public String getTargetCellInfo(
            String room, int row, String side, int shelf, int level)
            throws InvocationTargetException, IllegalAccessException {
        CellInfo cellInfo = bookService.getTargetCellInfo(room, row, side, shelf, level);
        return JSON.toJSONString(cellInfo);
    }

    /**
     * 获取书的基本信息
     *
     * @param bookIds
     * @return
     */
    @RequestMapping("/getBookBasicInfoByBookIds")
    public String getBookBasicInfoByBookIds(String bookIds) {
        List<String> bookIdList = JSON.parseArray(bookIds, String.class);
        List<BookBasicInfo> bookBasicInfoListList = new ArrayList<>();
        for (String bookId : bookIdList) {
            Book book = bookRepository.findBookByBookId(bookId);
            BookBasicInfo bookBasicInfo = new BookBasicInfo();
            BeanUtils.copyProperties(book, bookBasicInfo);
            bookBasicInfo.setMongoId(book.get_id());
            bookBasicInfoListList.add(bookBasicInfo);
        }
        return JSON.toJSONString(bookBasicInfoListList);
    }

}
