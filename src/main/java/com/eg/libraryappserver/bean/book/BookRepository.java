package com.eg.libraryappserver.bean.book;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @time 2020-01-08 23:14
 */
@Repository
public interface BookRepository extends MongoRepository<Book, String> {
    Book findBookByBookrecno(String recno);

    List<Book> findBookByTitleContains(String q, PageRequest pageRequest);

    Book findBookByBookId(String bookId);

}
