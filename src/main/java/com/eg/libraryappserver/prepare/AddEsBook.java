package com.eg.libraryappserver.prepare;

import com.eg.libraryappserver.LibraryAppServerApplication;
import com.eg.libraryappserver.bean.book.Book;
import com.eg.libraryappserver.bean.book.EsBook;
import com.eg.libraryappserver.bean.book.repository.EsBookRepository;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LibraryAppServerApplication.class)
public class AddEsBook {
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private EsBookRepository esBookRepository;

    @Test
    public void addBookToElasticSearch() {
        for (int i = 0; i < 2000; i++) {
            Query query = new Query();
            query.skip(200 * i);
            query.limit(1);
            List<Book> bookList = mongoTemplate.find(query, Book.class);
            for (Book book : bookList) {
                //先查询elastic search，如已存在则跳过
                EsBook esBook = esBookRepository.findByBookId(book.getBookId());
                if (esBook == null) {
                    esBook = new EsBook();
                } else {
                    System.out.println("skip: " + book.getBookId() + ", " + book.getTitle());
                    continue;
                }
                try {
                    BeanUtils.copyProperties(esBook, book);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                esBook.setMongoId(book.get_id());
                esBook.setId(null);
                esBookRepository.save(esBook);
                System.out.println(esBook.getId());
            }
        }
    }
}
