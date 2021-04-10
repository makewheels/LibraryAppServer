package com.eg.libraryappserver.prepare;

import com.alibaba.fastjson.JSON;
import com.eg.libraryappserver.LibraryAppServerApplication;
import com.eg.libraryappserver.bean.book.Book;
import com.eg.libraryappserver.bean.book.EsBook;
import com.eg.libraryappserver.bean.book.repository.EsBookRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LibraryAppServerApplication.class)
public class AddEsBook {
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private EsBookRepository esBookRepository;


    private void addToEs(Book book) {
        //先查询elastic search，如已存在则跳过
        EsBook esBook = esBookRepository.findByBookId(book.getBookId());
        if (esBook != null) {
            System.out.println("skip: " + book.getBookId() + ", " + book.getTitle());
            return;
        }
        esBook = new EsBook();
        BeanUtils.copyProperties(book, esBook);
        esBook.setMongoId(book.get_id());
        //从mongo拷贝过来的_id，存入elastic search时置为null
//        esBook.set_id(null);
        esBookRepository.save(esBook);
        System.out.println(Thread.currentThread().getName() + " "
                + esBook.getEsId() + " " + JSON.toJSONString(esBook));
    }

    @Test
    public void addBookToElasticSearch() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 2000; i++) {
            Query query = new Query();
            query.skip(200 * i);
            query.limit(200);
            List<Book> bookList = mongoTemplate.find(query, Book.class);
            for (Book book : bookList) {
                executorService.execute(() -> addToEs(book));
            }
        }
        executorService.shutdown();
    }
}
