package com.eg.libraryappserver.book;

import com.alibaba.fastjson.JSON;
import com.eg.libraryappserver.book.bean.Book;
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
        System.out.println(booksByTitle);
        return JSON.toJSONString(booksByTitle);
    }
}
