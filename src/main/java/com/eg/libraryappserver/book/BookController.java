package com.eg.libraryappserver.book;

import com.alibaba.fastjson.JSON;
import com.eg.libraryappserver.book.bean.library.Book;
import com.eg.libraryappserver.book.bean.response.query.BookQueryRecord;
import com.eg.libraryappserver.book.bean.response.query.BookQueryResponse;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @time 2020-01-09 21:52
 */
@Controller
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookRepository bookRepository;

    /**
     * 跳转到搜索结果页
     *
     * @param q
     * @param map
     * @return
     */
    @RequestMapping("/toSearchPage")
    public String toSearchPage(@RequestParam("q") String q, Map<String, String> map) {
        map.put("q", q);
        return "searchResult";
    }

    /**
     * ajax请求
     *
     * @param q
     * @return
     */
    @RequestMapping("/search")
    @ResponseBody
    public String queryBook(@RequestParam("q") String q) {
        List<Book> booksByTitle = bookRepository.findBookByTitleContains(q);
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
//            bookQueryRecord.setIsbn(book.getIsbn());
//            bookQueryRecord.setIsbn13(book.getIsbn13());
//            bookQueryRecord.setCoverImageUrl(book.getCoverImageUrl());
//            bookQueryRecord.setTitle(book.getTitle());
//            bookQueryRecord.setSubtitle(book.getSubtitle());
//            bookQueryRecord.setAuthorList(book.getAuthorList());
//            bookQueryRecord.setPublisher(book.getPublisher());
//            bookQueryRecord.setPublishDate(book.getPublishDate());
            try {
                BeanUtils.copyProperties(bookQueryRecord,book);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            bookQueryRecordList.add(bookQueryRecord);
            //给图片加api key
//            book.setCoverImageUrl(book.getCoverImageUrl() + "?"
//                    + Constants.DOUBAN_API_KEY_PARAM_NAME + "=" + Constants.DOUBAN_API_KEY);
        }
        return JSON.toJSONString(bookQueryResponse);
    }
}
