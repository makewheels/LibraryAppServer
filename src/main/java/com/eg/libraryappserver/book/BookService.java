package com.eg.libraryappserver.book;

import com.eg.libraryappserver.bean.book.library.holding.Holding;
import com.eg.libraryappserver.bean.book.library.holding.Position;
import com.eg.libraryappserver.bean.book.library.holding.repository.HoldingRepository;
import com.eg.libraryappserver.crawl.booklist.KeyValue;
import com.eg.libraryappserver.crawl.booklist.KeyValueRepository;
import com.eg.libraryappserver.util.KeyValueConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
    private HoldingRepository holdingRepository;

    @Autowired
    public void setKeyValueRepository(KeyValueRepository keyValueRepository) {
        this.keyValueRepository = keyValueRepository;
    }

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Autowired
    public void setHoldingRepository(HoldingRepository holdingRepository) {
        this.holdingRepository = holdingRepository;
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
        if (CollectionUtils.isEmpty(holdingList))
            progress.setValue(0L);
        else
            progress.setValue(progressIndex + amount);
        keyValueRepository.save(progress);
        System.out.println(progressIndex);
        return holdingList;
    }

    /**
     * 返回数的位置，只返回一个结果的string
     *
     * @param bookId
     * @return
     */
    public String getSingleBookPosition(String bookId) {
        List<Holding> holdingListByBookId = holdingRepository.findHoldingListByBookId(bookId);
        String positionString = null;
        for (Holding holding : holdingListByBookId) {
            Position position = holding.getPosition();
            //如果没有position
            if (position == null) {
                continue;
            }
            //如果是有message，也就是：非自助借还(RFID)图书，无法定位！
            if (StringUtils.isNotEmpty(position.getMessage())) {
                continue;
            }
            //只要找到一个正常的数据了，就可以返回了
            positionString = position.getRoom() + "\n" + position.getDetailPosition();
            break;
        }
        return positionString;
    }
}
