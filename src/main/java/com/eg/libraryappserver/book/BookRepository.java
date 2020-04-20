package com.eg.libraryappserver.book;

import com.eg.libraryappserver.book.bean.library.Book;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @time 2020-01-08 23:14
 */
public interface BookRepository extends MongoRepository<Book, String> {
    List<Book> findBooksByBookrecno(String recno);

    List<Book> findBookByTitleContains(String q, PageRequest pageRequest);
}
