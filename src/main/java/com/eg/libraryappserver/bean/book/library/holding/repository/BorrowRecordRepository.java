package com.eg.libraryappserver.bean.book.library.holding.repository;

import com.eg.libraryappserver.bean.book.library.holding.BorrowRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @time 2020-04-22 19:34
 */
@Repository
public interface BorrowRecordRepository extends MongoRepository<BorrowRecord, String> {
    List<BorrowRecord> findBorrowRecordsByBarcode(String barcode);
}
