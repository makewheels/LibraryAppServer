package com.eg.libraryappserver.prepare;

import com.eg.libraryappserver.LibraryAppServerApplication;
import com.eg.libraryappserver.bean.book.library.holding.EsHolding;
import com.eg.libraryappserver.bean.book.library.holding.Holding;
import com.eg.libraryappserver.bean.book.library.holding.Position;
import com.eg.libraryappserver.bean.book.library.holding.repository.EsHoldingRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LibraryAppServerApplication.class)
public class AddEsHolding {
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private EsHoldingRepository esHoldingRepository;

    @Test
    public void addHoldingsToElasticSearch() {
        for (int i = 0; i < 3500; i++) {
            Query query = new Query();
            query.skip(200 * i);
            query.limit(200);
            List<Holding> holdingList = mongoTemplate.find(query, Holding.class);
            for (Holding holding : holdingList) {
                //先查询elastic search，如已存在则跳过
                EsHolding esHolding = esHoldingRepository.findByMongoId(holding.get_id());
                if (esHolding != null) {
                    System.out.println("skip: " + holding.getBookId());
                    continue;
                }
                esHolding = new EsHolding();
                esHolding.setMongoId(holding.get_id());
                esHolding.setBookId(holding.getBookId());
                Position position = holding.getPosition();
                //如果没有位置
                if (position == null || StringUtils.isNotEmpty(position.getMessage())) {
                    esHolding.setHasPosition(false);
                } else {
                    //如果有位置
                    esHolding.setHasPosition(true);
                    esHolding.setDetailPosition(position.getDetailPosition());
                    esHolding.setRoom(position.getRoom());
                    esHolding.setRow(position.getRow());
                    esHolding.setSide(position.getSide());
                    esHolding.setShelf(position.getShelf());
                    esHolding.setLevel(position.getLevel());
                }
                //存入elastic search
                EsHolding save = esHoldingRepository.save(esHolding);
                System.out.println(save);
            }
        }
    }
}
