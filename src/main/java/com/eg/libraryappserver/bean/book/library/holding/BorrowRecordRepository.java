package com.eg.libraryappserver.bean.book.library.holding;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @time 2020-04-22 19:34
 */
public interface BorrowRecordRepository extends MongoRepository<BorrowRecord, String> {
}
