package com.eg.libraryappserver.book;

import com.eg.libraryappserver.bean.book.library.holding.Holding;
import com.eg.libraryappserver.crawl.booklist.KeyValue;
import com.eg.libraryappserver.crawl.booklist.KeyValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @time 2020-04-23 09:46
 */
@Service
public class BookService {
    private static final String KEY_POSITION_MISSION_PROGRESS_ID = "KEY_POSITION_MISSION_PROGRESS_ID";
    private KeyValueRepository keyValueRepository;
    private MongoTemplate mongoTemplate;

    @Autowired
    public void setKeyValueRepository(KeyValueRepository keyValueRepository) {
        this.keyValueRepository = keyValueRepository;
    }

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * 获取内网爬位置进度 key value 对象
     *
     * @return
     */
    public KeyValue getPositionMissionProgress() {
        return keyValueRepository.findByKey(KEY_POSITION_MISSION_PROGRESS_ID);
    }

    /**
     * 内网爬位置，获取当前进度索引
     *
     * @return
     */
    public long getPositionMissionHoldingIndex() {
        long lastIndex = 0;
        KeyValue progress = getPositionMissionProgress();
        if (progress != null)
            lastIndex = (long) progress.getValue();
        return lastIndex;
    }

    /**
     * 内网爬虫，请求获取书的位置
     *
     * @param amount
     * @return
     */
    public List<Holding> getPositionMissionHoldings(int amount) {
        long lastIndex = getPositionMissionHoldingIndex();
        lastIndex++;
        Query query = Query.query(Criteria.where("index").gt(lastIndex));
        query.limit(amount);
        return mongoTemplate.find(query, Holding.class);
    }
}
