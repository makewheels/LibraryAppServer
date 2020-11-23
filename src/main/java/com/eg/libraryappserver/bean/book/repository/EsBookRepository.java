package com.eg.libraryappserver.bean.book.repository;

import com.eg.libraryappserver.bean.book.EsBook;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsBookRepository extends ElasticsearchRepository<EsBook, String> {
    EsBook findByBookId(String bookId);
}
