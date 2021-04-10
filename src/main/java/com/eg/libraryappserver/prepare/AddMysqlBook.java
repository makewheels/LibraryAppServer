package com.eg.libraryappserver.prepare;

import com.eg.libraryappserver.LibraryAppServerApplication;
import com.eg.libraryappserver.bean.book.Book;
import com.eg.libraryappserver.bean.book.MysqlBook;
import com.eg.libraryappserver.bean.book.repository.MysqlBookRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LibraryAppServerApplication.class)
public class AddMysqlBook {
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private MysqlBookRepository mysqlBookRepository;

    private void addBookToMysql(Book book) {
        //先查询MySQL，如已存在则跳过
        MysqlBook byBookId = mysqlBookRepository.findByBookId(book.getBookId());
        if (byBookId != null) {
            System.out.println("skip: " + book.getBookId() + ", " + book.getTitle());
            return;
        }
        MysqlBook mysqlBook = new MysqlBook();
        BeanUtils.copyProperties(book, mysqlBook);
        mysqlBook.setMongoId(book.get_id());
        mysqlBookRepository.save(mysqlBook);
        System.out.println(mysqlBook.getId() + " " + mysqlBook.getTitle());
    }

    @Test
    public void addBookToMysql() {
        for (int i = 0; i < 2000; i++) {
            Query query = new Query();
            query.skip(200 * i);
            query.limit(200);
            List<Book> bookList = mongoTemplate.find(query, Book.class);
            for (Book book : bookList) {
                addBookToMysql(book);
            }
        }
    }
}
