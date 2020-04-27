package com.eg.libraryappserver.book;

import com.alibaba.fastjson.JSON;
import com.eg.libraryappserver.bean.book.library.holding.BarcodePosition;
import com.eg.libraryappserver.bean.book.library.holding.Holding;
import com.eg.libraryappserver.bean.book.library.holding.Position;
import com.eg.libraryappserver.bean.book.library.holding.repository.HoldingRepository;
import com.eg.libraryappserver.crawl.booklist.KeyValue;
import com.eg.libraryappserver.util.KeyValueConstants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @time 2020-04-27 16:16
 */
@Controller
@RequestMapping("/bookPosition")
public class BookPositionController {
    private BookService bookService;
    private HoldingRepository holdingRepository;

    @Autowired
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @Autowired
    public void setHoldingRepository(HoldingRepository holdingRepository) {
        this.holdingRepository = holdingRepository;
    }


    @RequestMapping("/goToFlashDemo")
    public String goToFlashDemoHtml() {
        return "/GotoFlash.aspx.html";
    }

    /**
     * 请求书的位置任务
     *
     * @param password
     * @return
     */
    @RequestMapping("/requestPositionMission")
    @ResponseBody
    public String requestPositionMission(@RequestParam String password) {
        if (StringUtils.isEmpty(password))
            return null;
        if (!password.equals("ETwrayANWeniq6HY"))
            return null;
        //从数据库中查
        List<Holding> holdingList = bookService.getPositionMissionHoldings(100);
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
     * @param barcodePositionJson
     * @return
     */
    @PostMapping("/submitPositionMission")
    @ResponseBody
    public String submitPositionMission(@RequestParam String provider,
                                        @RequestParam String barcodePositionJson) {
        System.out.println("BookPositionController.submitPositionMission");
        List<BarcodePosition> barcodePositionList
                = JSON.parseArray(barcodePositionJson, BarcodePosition.class);
        //签名校验
        for (BarcodePosition barcodePosition : barcodePositionList) {
            String barcode = barcodePosition.getBarcode();
            String position = barcodePosition.getPosition();
            //时间大于十分钟则放弃
            long timestamp = barcodePosition.getTimestamp();
            long tenMinutes = 1000 * 60 * 10;
            long diffTime = System.currentTimeMillis() - timestamp;
            if (diffTime > tenMinutes) {
                return null;
            }
            String clientSign = barcodePosition.getSign();
            String signKey = "vPUYt6q1AzmmjzXG";
            String serverSign = DigestUtils.md5Hex(barcode + position + timestamp + signKey);
            if (!clientSign.equals(serverSign))
                return null;
    }
        //集合排序
//        barcodePositionList.sort((o1, o2) -> 0);
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
            position.setProvider(provider);
            position.setMessage(barcodePosition.getMessage());
            //position字符串
            String positionString = barcodePosition.getPosition();
            //可能为空
            if (StringUtils.isNotEmpty(positionString)) {
                position.setPosition(positionString);
                //解析position
                String coordinate = positionString.split("\\|")[0];
                String right = positionString.split("\\|")[1];
                String room = right.split(" ")[0];
                String detailPosition = right.split(" ")[1];

                int row = Integer.parseInt(StringUtils.substringBefore(detailPosition, "排"));
                String side = StringUtils.substringBetween(detailPosition, "排", "面");
                int shelf = Integer.parseInt(StringUtils.substringBetween(detailPosition, "面", "架"));
                int level = Integer.parseInt(StringUtils.substringBetween(detailPosition, "架", "层"));

                position.setRow(row);
                position.setSide(side);
                position.setShelf(shelf);
                position.setLevel(level);

                position.setCoordinate(coordinate);
                position.setRoom(room);
                position.setDetailPosition(detailPosition);
            }

            System.out.println(JSON.toJSONString(position));
            //保存holding
//            holdingRepository.save(holding);

            //更新进度
            KeyValue progress = bookService.getPositionMissionProgress();
            //如果没有progress则新建
            if (progress == null) {
                progress = new KeyValue();
                progress.setKey(KeyValueConstants.KEY_POSITION_MISSION_PROGRESS);
            }
            long holdingIndex = holding.getIndex();
            progress.setValue(holdingIndex);
        }
        //保存进度
//            keyValueRepository.save(progress);
        return "ok";
    }
}
