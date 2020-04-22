package com.eg.libraryappserver;

import com.alibaba.fastjson.JSON;
import com.eg.libraryappserver.bean.book.Book;
import com.eg.libraryappserver.bean.book.BookRepository;
import com.eg.libraryappserver.bean.response.detail.BookDetailResponse;
import com.eg.libraryappserver.bean.response.query.BookQueryRecord;
import com.eg.libraryappserver.bean.response.query.BookQueryResponse;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @time 2020-01-09 21:52
 */
@Controller
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookRepository bookRepository;

    /**
     * 根据关键词查询书的列表
     *
     * @param q
     * @param page
     * @param size
     * @return
     */
    @RequestMapping("/search")
    @ResponseBody
    public String queryBook(@RequestParam String q,
                            @RequestParam int page,
                            @RequestParam int size) {
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
            try {
                BeanUtils.copyProperties(bookQueryRecord, book);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
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
    @ResponseBody
    public String getBookDetail(@RequestParam String mangoId) {
        //查询数据库
        Book book = bookRepository.findById(mangoId).get();
        BookDetailResponse bookDetailResponse = new BookDetailResponse();
        //拷贝属性
        try {
            BeanUtils.copyProperties(bookDetailResponse, book);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        bookDetailResponse.setMangoId(book.get_id());
        return JSON.toJSONString(bookDetailResponse);
    }


}
