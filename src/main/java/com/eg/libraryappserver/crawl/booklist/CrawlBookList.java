package com.eg.libraryappserver.crawl.booklist;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eg.libraryappserver.bean.book.BookRepository;
import com.eg.libraryappserver.bean.book.Book;
import com.eg.libraryappserver.bean.book.douban.FromDouban;
import com.eg.libraryappserver.bean.book.library.FromLibrary;
import com.eg.libraryappserver.bean.book.library.holding.repository.BorrowRecordRepository;
import com.eg.libraryappserver.bean.book.library.holding.repository.HoldingRepository;
import com.eg.libraryappserver.bean.book.library.imageapi.LibraryImageApiResult;
import com.eg.libraryappserver.bean.book.library.imageapi.Result;
import com.eg.libraryappserver.bean.book.library.holding.BorrowRecord;
import com.eg.libraryappserver.bean.book.library.holding.Holding;
import com.eg.libraryappserver.util.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.xsoup.Xsoup;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.*;

/**
 * 爬取书的列表
 * <p>
 * 普通视图，有借阅数：
 * http://60.218.184.234:8091/opac/search?q=*%3A*&searchType=standard&isFacet=true&view=standard&rows=10&sortWay=score&sortOrder=desc&searchWay0=marc&logical0=AND&page=1
 * <p>
 * 简单视图：
 * http://60.218.184.234:8091/opac/search?q=*%3A*&searchType=standard&isFacet=true&view=simple&sortWay=score&sortOrder=desc&searchWay0=marc&logical0=AND&rows=10
 *
 * @time 2020-01-08 20:02
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
public class CrawlBookList {
    private String PROGRESS_KEY = "CrawlBookList";
    //是否保存到数据库开关
    private boolean saveSwitch = true;

    private BookRepository bookRepository;
    private BorrowRecordRepository borrowRecordRepository;
    private HoldingRepository holdingRepository;
    private ProgressRepository progressRepository;

    @Autowired
    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Autowired
    public void setBorrowRecordRepository(BorrowRecordRepository borrowRecordRepository) {
        this.borrowRecordRepository = borrowRecordRepository;
    }

    @Autowired
    public void setHoldingRepository(HoldingRepository holdingRepository) {
        this.holdingRepository = holdingRepository;
    }

    @Autowired
    public void setProgressRepository(ProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    //每页几个
    private int rows = 100;
    private String baseUrl = "http://60.218.184.234:8091/opac/search?q=*%3A*" +
            "&searchType=standard&isFacet=true&view=simple&sortWay=score&sortOrder=desc" +
            "&searchWay0=marc&logical0=AND&rows=" + rows;

    /**
     * 图书馆的获取图片url的接口
     * https://book-resource.dataesb.com/websearch/metares?glc=P2HLJ0459003&cmdACT=getImages&type=0&isbns=,9787501191208,9787556251353,9787544280662,9787556251285,9787541155994,9787518337835,9787571405540,9787559638441,9787521713305,9787559636614&callback=showCovers&jsoncallback=jQuery16209185341982588602_1587542739461&_=1587542739506
     *
     * @param isbns
     * @return
     */
    private LibraryImageApiResult getLibraryImageApi(String isbns) {
        String callback = "showCovers";
        String url = "https://book-resource.dataesb.com/websearch/metares" +
                "?glc=P2HLJ0459003&cmdACT=getImages&type=0&isbns="
                + isbns + "&callback=" + callback + "&jsoncallback=jQuery"
                + RandomStringUtils.randomNumeric(20) + "_"
                + System.currentTimeMillis() + "&_=" + System.currentTimeMillis();
        String response = HttpUtil.get(url);
        if (response.startsWith(callback + "(")) {
            response = StringUtils.replaceOnce(response, callback + "(", "");
            response = response.substring(0, response.length() - 1);
        } else {
            return null;
        }
        return JSON.parseObject(response, LibraryImageApiResult.class);
    }

    /**
     * 从图书馆服务器，解析出书的列表
     * 解析出 isbn，callno，bookrecno
     *
     * @param url
     * @return
     */
    private List<Book> parseHtmlToBookList(String url) {
        List<Book> bookList = new ArrayList<>();
        String html = CrawlUtil.get(url);
        Elements trs;
        try {
            trs = Xsoup.select(html, "//*[@id=\"resultTile\"]/div[2]/table/tbody")
                    .getElements().get(0).children();
        } catch (IndexOutOfBoundsException e) {
            //如果没有了，就返回null
            return null;
        }
        //解决isbn和索书号bookrecno
        for (Element tr : trs) {
            Book book = new Book();
            book.setBookId(RandomUtil.getRandomString());
            book.setCreateTime(new Date());
            FromLibrary fromLibrary = new FromLibrary();
            book.setFromLibrary(fromLibrary);
            Element img = tr.child(0).child(0).child(0);
            String isbn = img.attr("isbn").trim();
            isbn = isbn.replace(" (pbk) :", "");
            isbn = isbn.replace(" (pbk.):", "");
            isbn = isbn.replace("(pbk.)", "");
            isbn = isbn.replace(" (pbk.) :", "");
            isbn = isbn.replace(" (pbk.)  :", "");
            isbn = isbn.replace(" (hbk) :", "");
            isbn = isbn.replace(" (hbk.) :", "");
            isbn = isbn.replace(" (cased) :", "");
            isbn = isbn.replace(" (cased.) :", "");
            isbn = isbn.replace(":", "");
            isbn = isbn.replace(" ", "");
            System.out.println("final isbn: " + isbn);
            fromLibrary.setIsbn(isbn);
            String bookrecno = img.attr("bookrecno").trim();
            fromLibrary.setBookrecno(bookrecno);
            Element bookmetaTD = tr.child(1);
            Element bookmeta = bookmetaTD.child(0);
            //图书馆获取图片url的api
            if (StringUtils.isNoneBlank(isbn)) {
                LibraryImageApiResult libraryImageApi = getLibraryImageApi(isbn.replace("-", ""));
                if (libraryImageApi != null) {
                    List<Result> resultList = libraryImageApi.getResult();
                    if (CollectionUtils.isNotEmpty(resultList)) {
                        Result result = resultList.get(0);
                        if (result != null) {
                            String coverlink = result.getCoverlink();
                            if (coverlink != null)
                                coverlink = coverlink.trim();
                            String resourceLink = result.getResourceLink();
                            if (resourceLink != null)
                                resourceLink = resourceLink.trim();
                            fromLibrary.setCoverlink(coverlink);
                            fromLibrary.setResourceLink(resourceLink);
                        }
                    }
                }
            }
            //其实这里应该再检查一下图片，那等到后面连着豆瓣的一起检查也行，其实现在就已经有豆瓣的图片了

            //开始获取右边的了
            String title = bookmeta.child(0).child(0).child(0).text().trim();
            String author = bookmeta.child(1).child(0).text().trim();
            String publisher = bookmeta.child(2).child(0).text().trim();
            String publishDate = StringUtils.substringAfter(bookmeta.child(2).text().trim(), "出版日期: ").trim();
            //文献类型：图书
            String type = StringUtils.substringBetween(bookmeta.child(3).text(),
                    "文献类型: ", ", 索书号:");
            fromLibrary.setTitle(title);
            fromLibrary.setAuthor(author);
            fromLibrary.setPublisher(publisher);
            fromLibrary.setPublishDate(publishDate);
            fromLibrary.setType(type);
            //html页面数据就此结束
            bookList.add(book);
        }
        // 再发请求，获取索书号
        StringBuilder bookrecnos = new StringBuilder();
        for (Book book : bookList) {
            bookrecnos.append(book.getFromLibrary().getBookrecno() + ",");
        }
        bookrecnos.deleteCharAt(bookrecnos.length() - 1);
        String xml = null;
        try {
            xml = CrawlUtil.get(Constants.BASE_URL + "/opac/book/callnos?bookrecnos="
                    + URLEncoder.encode(bookrecnos.toString(), Constants.CHARSET));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        // 解析xml
        List<org.dom4j.Element> records = XmlParser.parseText(xml).elements();
        // 设置到每一个book中
        for (Book book : bookList) {
            String bookrecno = book.getFromLibrary().getBookrecno();
            for (org.dom4j.Element record : records) {
                if (bookrecno.equals(record.element("bookrecno").getText())) {
                    String callno = record.element("callno").getText();
                    book.getFromLibrary().setCallno(callno);
                    break;
                }
            }
        }
        return bookList;
    }

    /**
     * 处理豆瓣请求，获取图书信息，保存到book中
     *
     * @param book
     */
    private void handleDouban(Book book) {
        String isbn = book.getFromLibrary().getIsbn();
        //如果isbn为空，则跳过
        if (StringUtils.isEmpty(isbn)) {
            return;
        }
        String doubanUrl_1 = "https://api.douban.com/v2/book/isbn/";
        String doubanUrl_2 = "?apikey=0df993c66c0c636e29ecbb5344252a4a";
        String doubanJson = HttpUtil.get(doubanUrl_1 + isbn + doubanUrl_2);
        //如果豆瓣返回错误信息
        if (doubanJson.equals(
                "{\"msg\":\"book_not_found\",\"code\":6000,\"request\":\"GET \\/v2\\/book\\/isbn\\/")) {
            System.err.println("豆瓣查找书不存在，isbn = " + isbn);
            return;
        }
        FromDouban fromDouban = JSON.parseObject(doubanJson, FromDouban.class);
        book.setFromDouban(fromDouban);
    }

    /**
     * 处理holdingApi
     * <p>
     * http://60.218.184.234:8091/opac/api/holding/246319?limitLibcodes=
     *
     * @param book
     */
    private void handleHoldingApi(Book book) {
        FromLibrary fromLibrary = book.getFromLibrary();
        String bookId = book.getBookId();
        String bookrecno = fromLibrary.getBookrecno();
        if (StringUtils.isEmpty(bookrecno)) {
            System.err.println("处理holdingList，bookrecno为空！ bookId=" + bookId);
            return;
        }
        //查询holdingList接口url
        String holdingListUrl_1 = "http://60.218.184.234:8091/opac/api/holding/";
        String holdingListUrl_2 = "?limitLibcodes=";
        String json = HttpUtil.get(holdingListUrl_1 + bookrecno + holdingListUrl_2);
        JSONObject jsonObject = JSONObject.parseObject(json);
        //holdingList
        List<Holding> holdingList = JSON.parseArray(JSON.toJSONString(jsonObject.get("holdingList")),
                Holding.class);
        //保存到数据库
        if (saveSwitch)
            for (Holding holding : holdingList) {
                holding.setBookId(bookId);
                String barcode = holding.getBarcode();
                //看这条holding是不是已经存在了
                Holding findHolding = holdingRepository.findHoldingByBarcode(barcode);
                //如果不存在，则保存
                if (findHolding == null) {
                    holding.setCreateTime(new Date());
                    holdingRepository.save(holding);
                } else if (findHolding.getBarcode().equals(holding.getBarcode())
                        && findHolding.getRecno() == holding.getRecno()
                        && findHolding.getBookrecno() == holding.getBookrecno()) {
                    //如果已经存在，则更新
                    holding.set_id(findHolding.get_id());
                    holding.setBookId(findHolding.getBookId());
                    holding.setUpdateTime(new Date());
                    holding.setCreateTime(findHolding.getCreateTime());
                    try {
                        BeanUtils.copyProperties(findHolding, holding);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    holdingRepository.save(findHolding);
                }
            }
        //统计借阅数，续借数
        int borrowCount = 0;
        int renewCount = 0;
        for (
                Holding holding : holdingList) {
            borrowCount += holding.getTotalLoanNum();
            renewCount += holding.getTotalRenewNum();
        }
        fromLibrary.setBorrowCount(borrowCount);
        fromLibrary.setRenewCount(renewCount);
        //loanWorkMap
        Map jsonLoanWorkMap = jsonObject.getJSONObject("loanWorkMap");
        Set set = jsonLoanWorkMap.keySet();
        Map<String, BorrowRecord> loanWorkMap = new HashMap<>();
        for (
                Object key : set) {
            BorrowRecord borrowRecord = JSON.parseObject(JSON.toJSONString(jsonLoanWorkMap.get(key)), BorrowRecord.class);
            loanWorkMap.put((String) key, borrowRecord);
        }
        //loanWorkMap已准备就绪
        Set<String> keySet = loanWorkMap.keySet();
        for (String key : keySet) {
            BorrowRecord borrowRecord = loanWorkMap.get(key);
            borrowRecord.setBookId(bookId);
            borrowRecord.setCreateTime(new Date());
            if (saveSwitch) {
                //要保存借阅记录之前，先看数据库中有没有
                String barcode = borrowRecord.getBarcode();
                List<BorrowRecord> findBorrowRecords = borrowRecordRepository.findBorrowRecordsByBarcode(barcode);
                //如果没有则保存，如果已有则跳过
                if (CollectionUtils.isEmpty(findBorrowRecords)) {
                    borrowRecordRepository.save(borrowRecord);
                } else {
                    for (BorrowRecord findBorrowRecord : findBorrowRecords) {
                        if (borrowRecord.getRdid().equals(findBorrowRecord.getRdid())
                                && borrowRecord.getLoanDate() == findBorrowRecord.getLoanDate()
                                && borrowRecord.getReturnDate() == findBorrowRecord.getReturnDate()
                                && borrowRecord.getDueTime() == findBorrowRecord.getDueTime()
                                && borrowRecord.getBarcode().equals(findBorrowRecord.getBarcode())
                                && borrowRecord.getRuleState() == findBorrowRecord.getRuleState()
                                && borrowRecord.getLoanCount() == findBorrowRecord.getLoanCount()
                                && borrowRecord.getAttachMent() == findBorrowRecord.getAttachMent()
                                && borrowRecord.isUnderlease() == findBorrowRecord.isUnderlease()) {
                            //发现已经有这条数据了，则跳过
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 整合数据资源
     * 整合各种来路的数据资源，包括，图书馆，豆瓣...
     * 放到book下
     *
     * @param book
     */
    private void integrateDataResources(Book book) {
        FromLibrary fromLibrary = book.getFromLibrary();
        FromDouban fromDouban = book.getFromDouban();
        String title = fromLibrary.getTitle();
        if (StringUtils.isNotBlank(title))
            book.setTitle(title);
        else if (fromDouban != null && StringUtils.isNotEmpty(fromDouban.getTitle()))
            book.setTitle(fromDouban.getTitle());
        book.setIsbn(fromLibrary.getIsbn());
        book.setCallno(fromLibrary.getCallno());
        book.setBookrecno(fromLibrary.getBookrecno());
        String author = fromLibrary.getAuthor();
        if (StringUtils.isNotBlank(author)) {
            book.setAuthor(author);
        } else if (fromDouban != null && CollectionUtils.isNotEmpty(fromDouban.getAuthor())) {
            //组装作者
            List<String> authorList = fromDouban.getAuthor();
            StringBuilder doubanAuthor = new StringBuilder();
            doubanAuthor.append(authorList.get(0));
            for (int i = 1; i < authorList.size(); i++) {
                doubanAuthor.append("," + authorList.get(i));
            }
            book.setAuthor(doubanAuthor.toString());
        }
        String publisher = fromLibrary.getPublisher();
        if (StringUtils.isNotBlank(publisher))
            book.setPublisher(publisher);
        else if (fromDouban != null && StringUtils.isNotEmpty(fromDouban.getPublisher()))
            book.setPublisher(fromDouban.getPublisher());
        String publishDate = fromLibrary.getPublishDate();
        if (StringUtils.isNotBlank(publishDate))
            book.setPublishDate(publishDate);
        else if (fromDouban != null && StringUtils.isNotEmpty(fromDouban.getPubdate()))
            book.setPublishDate(fromDouban.getPubdate());
        if (fromDouban != null && StringUtils.isNotEmpty(fromDouban.getCatalog()))
            book.setCatalog(fromDouban.getCatalog());
        if (fromDouban != null && StringUtils.isNotEmpty(fromDouban.getSummary()))
            book.setSummary(fromDouban.getSummary());
        //封面图片url
        String resultImage;
        //豆瓣图片
        String doubanImage = null;
        if (fromDouban != null && fromDouban.getImages() != null
                && StringUtils.isNotEmpty(fromDouban.getImages().getLarge()))
            doubanImage = fromDouban.getImages().getLarge();
        String coverlink = fromLibrary.getCoverlink();
        String resourceLink = fromLibrary.getResourceLink();
        String libraryImage;
        //选出图书馆图片
        if (StringUtils.isEmpty(resourceLink) && StringUtils.isNotEmpty(coverlink))
            libraryImage = coverlink;
        else if (StringUtils.isNotEmpty(coverlink)
                && coverlink.contains("doubanio.com")
                && coverlink.contains("default"))
            libraryImage = resourceLink;
        else
            libraryImage = resourceLink;
        //如果没有豆瓣图片，或者是default，则用图书馆图片
        if (StringUtils.isNotEmpty(doubanImage) && !doubanImage.contains("default"))
            resultImage = doubanImage;
        else
            resultImage = libraryImage;
        book.setCoverUrl(resultImage);
    }

    /**
     * 爬书的列表并保存
     */
    @Test
    public void run() {
        //页码
        int page = 1;
        //读取之前的爬虫进度
        Progress progress = progressRepository.findProgressByKey(PROGRESS_KEY);
        if (progress == null) {
            progress = new Progress();
            progress.setCreateTime(new Date());
            progress.setKey(PROGRESS_KEY);
            progress.setPage(0);
            if (saveSwitch)
                progressRepository.save(progress);
        } else {
            page = progress.getPage() + 1;
        }
        String url = baseUrl + "&page=" + page;
        //向图书馆服务器发各种请求解析出bookList
        List<Book> bookList = parseHtmlToBookList(url);
        //只要不为空
        while (CollectionUtils.isNotEmpty(bookList)) {
            //遍历书的列表
            for (Book book : bookList) {
                //处理豆瓣api：
                handleDouban(book);
                //处理holdingApi
                handleHoldingApi(book);
                //整合数据资源
                integrateDataResources(book);
                //保存book到数据库
                if (saveSwitch) {
                    //以bookrecno作为id区分，看数据库中是否已经有这本书了
                    String bookrecno = book.getFromLibrary().getBookrecno();
                    Book findBook = bookRepository.findBookByBookrecno(bookrecno);
                    //如果没有这本书，则保存
                    if (findBook == null) {
                        bookRepository.save(book);
                    } else {
                        //已经有了则更新
                        System.out.println("数据库已有这本书，则更新，bookrecno = " + bookrecno);
                        book.set_id(findBook.get_id());
                        book.setBookId(findBook.getBookId());
                        book.setUpdateTime(new Date());
                        book.setCreateTime(findBook.getCreateTime());
                        try {
                            BeanUtils.copyProperties(findBook, book);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        bookRepository.save(findBook);
                    }
                    System.out.println("save to database: " + book.getFromLibrary().getTitle()
                            + " " + book.getBookId());
                }
            }
            //一页完成，保存进度
            if (saveSwitch) {
                progress.setPage(page);
                progressRepository.save(progress);
            }
            //继续下一页
            page++;
            //刷新url
            url = baseUrl + "&page=" + page;
            bookList = parseHtmlToBookList(url);
            System.out.println("page = " + page);
        }
        //整个都完事了，page页码进度重置为0
        progress.setPage(0);
        if (saveSwitch)
            progressRepository.save(progress);
        System.out.println("CollectionUtils.isNotEmpty(bookList) null the end!");
    }
}
