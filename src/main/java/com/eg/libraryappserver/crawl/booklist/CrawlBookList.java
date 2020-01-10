package com.eg.libraryappserver.crawl.booklist;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eg.libraryappserver.book.bean.Book;
import com.eg.libraryappserver.book.BookRepository;
import com.eg.libraryappserver.book.bean.Holding;
import com.eg.libraryappserver.book.bean.douban.DoubanBookInfo;
import com.eg.libraryappserver.util.Constants;
import com.eg.libraryappserver.util.CrawlUtil;
import com.eg.libraryappserver.util.HttpUtil;
import com.eg.libraryappserver.util.XmlParser;
import org.apache.commons.collections4.CollectionUtils;
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
 *
 * @time 2020-01-08 20:02
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
public class CrawlBookList {
    @Autowired
    BookRepository bookRepository;

    //每页几个
    private int rows = 100;
    private String baseUrl = "http://60.218.184.234:8091/opac/search?q=*%3A*" +
            "&searchType=standard&isFacet=true&view=simple&sortWay=score&sortOrder=desc" +
            "&searchWay0=marc&logical0=AND&rows=" + rows;

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
            String isbn = tr.child(0).child(0).child(0).attr("isbn");
            isbn = isbn.replace(" (pbk) :", "");
            isbn = isbn.replace(" (hbk.) :", "");
            isbn = isbn.replace(" (pbk.)  :", "");
            isbn = isbn.replace(" (pbk.) :", "");
            isbn = isbn.replace(" (cased) :", "");
            isbn = isbn.replace(" (cased.) :", "");
            isbn = isbn.replace(" (hbk) :", "");
            isbn = isbn.replace(" (pbk.):", "");
            System.out.println("final isbn: " + isbn);
            String bookrecno = tr.child(1).child(0).attr("bookrecno");
            Book book = new Book();
            book.setIsbn(isbn);
            book.setBookrecno(bookrecno);
            book.setCreateTime(new Date());
            bookList.add(book);
        }
        // 再发请求，获取索书号
        StringBuilder bookrecnos = new StringBuilder();
        for (Book book : bookList) {
            bookrecnos.append(book.getBookrecno() + ",");
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
        for (int i = 0; i < bookList.size(); i++) {
            Book book = bookList.get(i);
            String bookrecno = book.getBookrecno();
            for (org.dom4j.Element record : records) {
                if (bookrecno.equals(record.element("bookrecno").getText())) {
                    String callno = record.element("callno").getText();
                    book.setCallno(callno);
                    break;
                }
            }
        }
        return bookList;
    }

    private String doubanUrl_1 = "https://api.douban.com/v2/book/isbn/";
    private String doubanUrl_2 = "?apikey=0df993c66c0c636e29ecbb5344252a4a";

    /**
     * 处理豆瓣请求，获取图书信息
     *
     * @param book
     */
    private void doubanSave(Book book) {
        String isbn = book.getIsbn();
        //如果isbn为空
        if (StringUtils.isEmpty(isbn)) {
            return;
        }

        String doubanJson = HttpUtil.get(doubanUrl_1 + isbn + doubanUrl_2);
        //如果豆瓣返回错误信息
        if (doubanJson.contains(
                "{\"msg\":\"book_not_found\",\"code\":6000,\"request\":\"GET \\/v2\\/book\\/isbn\\/")) {
            System.err.println("豆瓣查找书不存在，isbn = " + isbn);
            return;
        }
        DoubanBookInfo doubanBookInfo = JSON.parseObject(doubanJson, DoubanBookInfo.class);

        book.setIsbn13(doubanBookInfo.getIsbn13());
        book.setCoverImageUrl(doubanBookInfo.getImages().getLarge());
        book.setTitle(doubanBookInfo.getTitle());
        book.setSubtitle(doubanBookInfo.getSubtitle());
        book.setOriginTitle(doubanBookInfo.getOrigin_title());
        book.setAuthorList(doubanBookInfo.getAuthor());
        book.setPublisher(doubanBookInfo.getPublisher());
        book.setPublishDate(doubanBookInfo.getPubdate());
        book.setPrice(doubanBookInfo.getPrice());
        book.setPages(doubanBookInfo.getPages());
        book.setCatalog(doubanBookInfo.getCatalog());
        book.setSummary(doubanBookInfo.getSummary());
        book.setDoubanBookInfoJson(doubanJson);
    }

    private String holdingListUrl_1 = "http://60.218.184.234:8091/opac/api/holding/";
    private String holdingListUrl_2 = "?limitLibcodes=";

    /**
     * 处理holdingList
     *
     * @param book
     */
    private void handleHoldingList(Book book) {
        String bookrecno = book.getBookrecno();
        if (StringUtils.isEmpty(bookrecno)) {
            System.err.println("处理holdingList，bookrecno为空！");
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(HttpUtil.get(holdingListUrl_1 + bookrecno + holdingListUrl_2));
        List<Holding> holdingList = JSON.parseArray(JSON.toJSONString(jsonObject.get("holdingList")), Holding.class);
        book.setHoldingList(holdingList);
    }

    /**
     * 爬书的列表并保存
     */
    public void crawlAndSave() {
        //页码
        int page = 761;
        String url = baseUrl + "&page=" + page;
        //向图书馆服务器发各种请求解析出bookList
        List<Book> bookList = parseHtmlToBookList(url);
        //只要不为空
        while (bookList != null) {
            //遍历书的列表
            for (Book book : bookList) {
                //以recno作为id区分，如果数据库已经有了就跳过
                if (CollectionUtils.isNotEmpty((
                        bookRepository.findBooksByBookrecno(book.getBookrecno())))) {
                    System.out.println("数据库已有这本书，isbn = " + book.getIsbn());
                    continue;
                }
                //先处理豆瓣api：
                doubanSave(book);
                //再处理holdingList
                handleHoldingList(book);
                //再保存到数据库
                bookRepository.save(book);
                System.out.println(book.getTitle());
            }
            //继续下一页
            page++;
            //刷新url
            url = baseUrl + "&page=" + page;
            bookList = parseHtmlToBookList(url);
            System.out.println("page = " + page);
        }
    }

    @Test
    public void runTest() {
        crawlAndSave();
    }
}
