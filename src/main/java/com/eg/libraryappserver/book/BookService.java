package com.eg.libraryappserver.book;

import com.eg.libraryappserver.bean.book.library.holding.Holding;
import com.eg.libraryappserver.crawl.booklist.KeyValue;
import com.eg.libraryappserver.crawl.booklist.KeyValueRepository;
import com.eg.libraryappserver.util.KeyValueConstants;
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
        return keyValueRepository.findByKey(KeyValueConstants.KEY_POSITION_MISSION_REQUEST_PROGRESS);
    }

    /**
     * 内网爬位置，获取当前进度索引
     *
     * @return
     */
    public KeyValue getRequestPositionMissionProgress() {
        KeyValue progress = getPositionMissionProgress();
        if (progress == null) {
            progress = new KeyValue();
            progress.setKey(KeyValueConstants.KEY_POSITION_MISSION_REQUEST_PROGRESS);
            progress.setValue(0L);
            keyValueRepository.save(progress);
        }
        return progress;
    }

    /**
     * 内网爬虫，请求获取书的位置
     *
     * @param amount
     * @return
     */
    public List<Holding> getPositionMissionHoldings(int amount) {
        KeyValue progress = getRequestPositionMissionProgress();
        long progressIndex = (long) progress.getValue();
        Query query = Query.query(Criteria.where("index").gt(progressIndex));
        query.limit(amount);
        List<Holding> holdingList = mongoTemplate.find(query, Holding.class);
        //更新进度
        progress.setValue(progressIndex + amount);
        keyValueRepository.save(progress);
        System.out.println(progressIndex);
        return holdingList;
    }
}
