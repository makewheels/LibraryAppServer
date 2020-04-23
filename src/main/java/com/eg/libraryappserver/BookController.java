package com.eg.libraryappserver;

import com.alibaba.fastjson.JSON;
import com.eg.libraryappserver.bean.book.Book;
import com.eg.libraryappserver.bean.book.BookRepository;
import com.eg.libraryappserver.bean.book.library.holding.BarcodePosition;
import com.eg.libraryappserver.bean.book.library.holding.Holding;
import com.eg.libraryappserver.bean.book.library.holding.Position;
import com.eg.libraryappserver.bean.book.library.holding.repository.HoldingRepository;
import com.eg.libraryappserver.bean.response.detail.BookDetailResponse;
import com.eg.libraryappserver.bean.response.query.BookQueryRecord;
import com.eg.libraryappserver.bean.response.query.BookQueryResponse;
import com.eg.libraryappserver.crawl.booklist.KeyValue;
import com.eg.libraryappserver.crawl.booklist.KeyValueRepository;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @time 2020-01-09 21:52
 */
@Controller
@RequestMapping("/book")
@ResponseBody
public class BookController {
    private BookService bookService;
    private BookRepository bookRepository;
    private HoldingRepository holdingRepository;
    private KeyValueRepository keyValueRepository;

    @Autowired
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @Autowired
    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Autowired
    public void setHoldingRepository(HoldingRepository holdingRepository) {
        this.holdingRepository = holdingRepository;
    }

    @Autowired
    public void setKeyValueRepository(KeyValueRepository keyValueRepository) {
        this.keyValueRepository = keyValueRepository;
    }

    /**
     * 请求书的位置任务
     *
     * @param password
     * @return
     */
    @RequestMapping("/requestPositionMission")
    public String requestPositionMission(@RequestParam String password) {
        if (password == null || !password.equals("ETwrayANWeniq6HY"))
            return null;
        //从数据库中查
        List<Holding> holdingList = bookService.getPositionMissionHoldings(10);
        List<BarcodePosition> barcodePositionList = new ArrayList<>();
        for (Holding holding : holdingList) {
            BarcodePosition barcodePosition = new BarcodePosition();
            barcodePosition.setBarcode(holding.getBarcode());
            barcodePositionList.add(barcodePosition);
        }
        return JSON.toJSONString(barcodePositionList);
    }

    /**
     * 提交书的位置任务
     *
     * @param provider
     * @return
     */
    @PostMapping("/submitPositionMission")
    public String submitPositionMission(@RequestParam String provider,
                                        @RequestParam String barcodePositionJson) {
        List<BarcodePosition> barcodePositionList = JSON.parseArray(barcodePositionJson, BarcodePosition.class);
        //签名校验
        String signKey = "vPUYt6q1AzmmjzXG";
        for (BarcodePosition barcodePosition : barcodePositionList) {
            String barcode = barcodePosition.getBarcode();
            String position = barcodePosition.getPosition();
            //时间大于十分钟则放弃
            long timestamp = barcodePosition.getTimestamp();
            long tenMinutes = 1000 * 60 * 10;
            long diffTime = System.currentTimeMillis() - timestamp;
            if (diffTime > tenMinutes)
                return null;
            String clientSign = barcodePosition.getSign();
            String serverSign = DigestUtils.md5Hex(barcode + position + timestamp + signKey);
            if (!clientSign.equals(serverSign))
                return null;
        }
        //保存位置数据
        for (BarcodePosition barcodePosition : barcodePositionList) {
            String barcode = barcodePosition.getBarcode();
            Holding holding = holdingRepository.findHoldingByBarcode(barcode);
            Position position = holding.getPosition();
            if (position == null) {
                position = new Position();
                position.setCreateTime(new Date());
                holding.setPosition(position);
            } else {
                position.setUpdateTime(new Date());
            }
            position.setPosition(barcodePosition.getPosition());
            //todo

            //更新进度
            long holdingIndex = holding.getIndex();
            KeyValue progress = bookService.getPositionMissionProgress();
            progress.setValue(holdingIndex);
            keyValueRepository.save(progress);
        }
        return "ok";
    }

    /**
     * 根据关键词查询书的列表
     *
     * @param q
     * @param page
     * @param size
     * @return
     */
    @RequestMapping("/search")
    public String queryBook(@RequestParam String q,
                            @RequestParam int page,
                            @RequestParam int size) {
        if (size != 20) {
            size = 20;
        }
        //分页查询
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Book> booksByTitle = bookRepository.findBookByTitleContains(q, pageRequest);
        if (booksByTitle == null) {
            return null;
        }
        BookQueryResponse bookQueryResponse = new BookQueryResponse();
        bookQueryResponse.setQ(q);
        bookQueryResponse.setAmount(booksByTitle.size());
        List<BookQueryRecord> bookQueryRecordList = new ArrayList<>();
        bookQueryResponse.setBookRecordList(bookQueryRecordList);
        //进行一波处理
        for (Book book : booksByTitle) {
            BookQueryRecord bookQueryRecord = new BookQueryRecord();
            //拷贝属性
            try {
                BeanUtils.copyProperties(bookQueryRecord, book);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            //设置mango id
            bookQueryRecord.setMangoId(book.get_id());
            bookQueryRecordList.add(bookQueryRecord);
        }
        return JSON.toJSONString(bookQueryResponse);
    }

    /**
     * 通过id查询书的详情信息
     *
     * @param mangoId
     * @return
     */
    @RequestMapping("/getBookDetail")
    public String getBookDetail(@RequestParam String mangoId) {
        //查询数据库
        Book book = bookRepository.findById(mangoId).get();
        BookDetailResponse bookDetailResponse = new BookDetailResponse();
        //拷贝属性
        try {
            BeanUtils.copyProperties(bookDetailResponse, book);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        bookDetailResponse.setMangoId(book.get_id());
        return JSON.toJSONString(bookDetailResponse);
    }

}
