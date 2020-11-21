package com.eg.libraryappserver.test;

import com.eg.libraryappserver.LibraryAppServerApplication;
import com.eg.libraryappserver.bean.book.library.holding.EsHolding;
import com.eg.libraryappserver.bean.book.library.holding.repository.EsHoldingRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LibraryAppServerApplication.class)
public class TestEs {
    @Resource
    private EsHoldingRepository esHoldingRepository;

    @Test
    public void testAdd() {
        EsHolding esHolding = new EsHolding();
        esHolding.setBookId("sss");
        esHoldingRepository.save(esHolding);
    }

}
