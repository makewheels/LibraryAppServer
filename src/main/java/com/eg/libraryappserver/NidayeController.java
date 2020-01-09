package com.eg.libraryappserver;

import com.eg.libraryappserver.book.BookRepository;
import com.eg.libraryappserver.crawl.booklist.CrawlBookList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @time 2020-01-09 00:37
 */
@RequestMapping("/1")
@Controller
public class NidayeController {
    @Autowired
    BookRepository bookRepository;

    @RequestMapping("/2")
    @ResponseBody
    public String s() {
        System.out.println(bookRepository);
        return "fwa";
    }
}
