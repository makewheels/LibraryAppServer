package com.eg.libraryappserver;

import com.alibaba.fastjson.JSON;
import com.eg.libraryappserver.bean.book.Book;
import com.eg.libraryappserver.bean.book.BookRepository;
import com.eg.libraryappserver.bean.book.library.holding.Holding;
import com.eg.libraryappserver.bean.book.library.holding.HoldingRepository;
import com.eg.libraryappserver.bean.book.library.holding.Position;
import com.eg.libraryappserver.bean.book.library.holding.positionmission.BarcodePosition;
import com.eg.libraryappserver.bean.book.library.holding.positionmission.BookPosition;
import com.eg.libraryappserver.bean.response.detail.BookDetailResponse;
import com.eg.libraryappserver.bean.response.query.BookQueryRecord;
import com.eg.libraryappserver.bean.response.query.BookQueryResponse;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
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
    private MongoTemplate mongoTemplate;

    @Autowired
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Autowired
    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Autowired
    public void setHoldingRepository(HoldingRepository holdingRepository) {
        this.holdingRepository = holdingRepository;
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
//        bookService.getPositionMissionHoldings(10)
//        holdingRepository.findHoldingByBarcode()
//        List<BookPosition> responseList = new ArrayList<>();
//        for (Book book : books) {
//            BookPosition bookPosition = new BookPosition();
//            bookPosition.setBookId(book.getBookId());
//            //从数据库中查
//            List<BarcodePosition> barcodePositionList = new ArrayList<>();
//            List<Holding> holdingList = null;
////            book.getFromLibrary().getHoldingList();
//            for (Holding holding : holdingList) {
//                BarcodePosition barcodePosition = new BarcodePosition();
//                barcodePosition.setBarcode(holding.getBarcode());
//                barcodePositionList.add(barcodePosition);
//            }
//            bookPosition.setBarcodePositionList(barcodePositionList);
//            responseList.add(bookPosition);
//        }
//        return JSON.toJSONString(responseList);
        return null;
    }

    /**
     * 提交书的位置任务
     *
     * @param provider
     * @return
     */
    @PostMapping("/submitPositionMission")
    public String submitPositionMission(@RequestParam String provider,
                                        @RequestParam String bookPositionJson) {
        BookPosition bookPosition = JSON.parseObject(bookPositionJson, BookPosition.class);
        List<BarcodePosition> barcodePositionList = bookPosition.getBarcodePositionList();
        for (BarcodePosition barcodePosition : barcodePositionList) {
            String barcode = barcodePosition.getBarcode();
            String position = barcodePosition.getPosition();
            long timestamp = barcodePosition.getTimestamp();
            String clientSign = barcodePosition.getSign();
            String serverSign = DigestUtils.md5Hex(barcode + position + timestamp + "vPUYt6q1AzmmjzXG");
            if (!clientSign.equals(serverSign))
                return null;
        }
        String bookId = bookPosition.getBookId();
        Book book = bookRepository.findBookByBookId(bookId);
        List<Holding> holdingList = null;
//                book.getFromLibrary().getHoldingList();
        for (Holding holding : holdingList) {
            String barcode = holding.getBarcode();
            BarcodePosition barcodePosition = null;
            for (BarcodePosition each : barcodePositionList) {
                if (barcode.equals(each.getBarcode()))
                    barcodePosition = each;
            }
            Position position = new Position();
            position.setCreateTime(new Date());
            position.setPosition(barcodePosition.getPosition());
            position.setRoomName("");
            System.out.println();
            System.out.println("position = " + position);
            System.out.println("BookController.submitPositionMission");

            holding.setPosition(position);
        }
        bookRepository.save(book);
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
