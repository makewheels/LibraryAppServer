package com.eg.libraryappserver.crawl.booklist;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @time 2020-04-23 10:12
 */
@Repository
public interface ProgressRepository extends MongoRepository<Progress, String> {
    Progress findProgressByKey(String key);
}
