package com.eg.libraryappserver.bean.book.library.holding.repository;

import com.eg.libraryappserver.bean.book.library.holding.Holding;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @time 2020-04-23 09:44
 */
@Repository
public interface HoldingRepository extends MongoRepository<Holding, String> {
    Holding findBy_id(String _id);

    Holding findHoldingByBarcode(String barcode);
}
