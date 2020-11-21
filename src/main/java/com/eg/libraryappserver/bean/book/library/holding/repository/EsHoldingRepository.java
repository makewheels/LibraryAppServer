package com.eg.libraryappserver.bean.book.library.holding.repository;

import com.eg.libraryappserver.bean.book.library.holding.EsHolding;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsHoldingRepository extends ElasticsearchRepository<EsHolding, Integer> {
}
