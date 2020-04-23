package com.eg.libraryappserver;

import com.eg.libraryappserver.bean.book.library.holding.Holding;
import com.eg.libraryappserver.bean.book.library.holding.HoldingRepository;
import com.eg.libraryappserver.crawl.booklist.KeyValue;
import com.eg.libraryappserver.crawl.booklist.KeyValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @time 2020-04-23 09:46
 */
@Service
public class BookService {
    private static final String KEY_POSITION_MISSION_PROGRESS_ID = "KEY_POSITION_MISSION_PROGRESS_ID";
    private HoldingRepository holdingRepository;
    private KeyValueRepository keyValueRepository;

    @Autowired
    public void setHoldingRepository(HoldingRepository holdingRepository) {
        this.holdingRepository = holdingRepository;
    }

    @Autowired
    public void setKeyValueRepository(KeyValueRepository keyValueRepository) {
        this.keyValueRepository = keyValueRepository;
    }

    /**
     * 内网爬虫，请求获取书的位置
     *
     * @param amount
     * @return
     */
    public List<Holding> getPositionMissionHoldings(int amount) {
        KeyValue progress = keyValueRepository.findByKey(KEY_POSITION_MISSION_PROGRESS_ID);
        return null;
    }
}
