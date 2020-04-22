package com.eg.libraryappserver.crawl.booklist;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eg.libraryappserver.BookRepository;
import com.eg.libraryappserver.bean.book.Book;
import com.eg.libraryappserver.bean.book.douban.FromDouban;
import com.eg.libraryappserver.bean.book.library.FromLibrary;
import com.eg.libraryappserver.bean.book.library.imageapi.LibraryImageApiResult;
import com.eg.libraryappserver.bean.book.library.imageapi.Result;
import com.eg.libraryappserver.util.*;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @Autowired
    private BookRepository bookRepository;

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
                System.out.println(isbn + "======");
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

    private String doubanUrl_1 = "https://api.douban.com/v2/book/isbn/";
    private String doubanUrl_2 = "?apikey=0df993c66c0c636e29ecbb5344252a4a";

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

    //查询holdingList接口url
    private String holdingListUrl_1 = "http://60.218.184.234:8091/opac/api/holding/";
    private String holdingListUrl_2 = "?limitLibcodes=";

    /**
     * 处理holdingApi
     * <p>
     * http://60.218.184.234:8091/opac/api/holding/246319?limitLibcodes=
     *
     * @param book
     */
    private void handleHoldingApi(Book book) {
        String bookrecno = book.getFromLibrary().getBookrecno();
        if (StringUtils.isEmpty(bookrecno)) {
            System.err.println("处理holdingList，bookrecno为空！ bookId=" + book.getBookId());
            return;
        }
        String json = HttpUtil.get(holdingListUrl_1 + bookrecno + holdingListUrl_2);
        JSONObject jsonObject = JSONObject.parseObject(json);
//        List<Holding> holdingList = JSON.parseArray(JSON.toJSONString(jsonObject.get("holdingList")), Holding.class);
//        book.getFromLibrary().setHoldingList(holdingList);
    }

    /**
     * 爬书的列表并保存
     */
    @Test
    public void run() {
        //页码
        int page = 1;
        String url = baseUrl + "&page=" + page;
        //向图书馆服务器发各种请求解析出bookList
        List<Book> bookList = parseHtmlToBookList(url);
        //只要不为空
        while (CollectionUtils.isNotEmpty(bookList)) {
            //遍历书的列表
            for (Book book : bookList) {
                //以bookrecno作为id区分，如果数据库已经有了就跳过
                List<Book> findBooksByBookrecno = bookRepository.findBooksByBookrecno(book.getBookrecno());
                if (CollectionUtils.isNotEmpty(findBooksByBookrecno)) {
                    System.out.println("数据库已有这本书，isbn = " + book.getFromLibrary().getIsbn());
                    continue;
                }
                //处理豆瓣api：
                handleDouban(book);
                //处理holdingApi
                handleHoldingApi(book);
                //保存到数据库
//                bookRepository.save(book);
                System.out.println(JSON.toJSONString(book));
            }
            //继续下一页
            page++;
            //刷新url
            url = baseUrl + "&page=" + page;
            bookList = parseHtmlToBookList(url);
            System.out.println("page = " + page);
        }
        System.out.println("CollectionUtils.isNotEmpty(bookList) null the end!");
    }

}
