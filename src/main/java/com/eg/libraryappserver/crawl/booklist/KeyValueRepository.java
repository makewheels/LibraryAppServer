package com.eg.libraryappserver.crawl.booklist;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @time 2020-04-23 20:50
 */
@Repository
public interface KeyValueRepository extends MongoRepository<KeyValue, String> {
    KeyValue findByKey(String key);
}
