package com.eg.libraryappserver.book;

import com.alibaba.fastjson.JSON;
import com.eg.libraryappserver.book.bean.Book;
import com.eg.libraryappserver.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @time 2020-01-09 21:52
 */
@Controller
@RequestMapping("book")
public class BookController {
    @Autowired
    BookRepository bookRepository;

    @RequestMapping("query")
    @ResponseBody
    public String queryBook(@RequestParam("q") String q) {
        List<Book> booksByTitle = bookRepository.findBooksByTitle(q);
        //进行一波处理
        for (Book book : booksByTitle) {
            //去掉豆瓣json
            book.setDoubanBookInfoJson(null);
            //给图片加api key
//            book.setCoverImageUrl(book.getCoverImageUrl() + "?"
//                    + Constants.DOUBAN_API_KEY_PARAM_NAME + "=" + Constants.DOUBAN_API_KEY);
        }
        return JSON.toJSONString(booksByTitle);
    }
}
