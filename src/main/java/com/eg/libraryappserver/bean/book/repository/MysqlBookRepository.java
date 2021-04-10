package com.eg.libraryappserver.bean.book.repository;

import com.eg.libraryappserver.bean.book.MysqlBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MysqlBookRepository extends JpaRepository<MysqlBook, String> {
}
