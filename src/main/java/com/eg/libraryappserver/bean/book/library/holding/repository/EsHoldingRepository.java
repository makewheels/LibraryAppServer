package com.eg.libraryappserver.bean.book.library.holding.repository;

import com.eg.libraryappserver.bean.book.library.holding.EsHolding;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface EsHoldingRepository extends ElasticsearchRepository<EsHolding, String> {
    List<EsHolding> findByHasPositionAndRoomAndShelfAndSideAndRowAndLevel(
            boolean hasPosition, String room, int shelf, String side, int row, int level);

    EsHolding findByMongoId(String mongoId);
}
