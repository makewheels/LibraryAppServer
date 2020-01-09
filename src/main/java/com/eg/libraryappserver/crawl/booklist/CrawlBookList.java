package com.eg.libraryappserver.crawl.booklist;

import com.eg.libraryappserver.book.Book;
import com.eg.libraryappserver.book.BookRepository;
import com.eg.libraryappserver.util.Constants;
import com.eg.libraryappserver.util.CrawlUtil;
import com.eg.libraryappserver.util.HttpUtil;
import com.eg.libraryappserver.util.XmlParser;
import org.apache.commons.collections4.CollectionUtils;
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
    public BookRepository bookRepository;

    //每页几个
    private int rows = 100;
    private String baseUrl = "http://60.218.184.234:8091/opac/search?q=*%3A*" +
            "&searchType=standard&isFacet=true&view=simple&sortWay=score&sortOrder=desc" +
            "&searchWay0=marc&logical0=AND&rows=" + rows;

    /**
     * 从html解析出书的列表
     * 解析出 isbn，callno，recno
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
        //解决isbn和索书号recno
        for (Element tr : trs) {
            String isbn = tr.child(0).child(0).child(0).attr("isbn");
            String recno = tr.child(1).child(0).attr("bookrecno");
            Book book = new Book();
            book.setIsbn(isbn);
            book.setRecno(recno);
            book.setCreateTime(new Date());
            bookList.add(book);
        }
        // 再发请求，获取索书号
        StringBuilder bookrecnos = new StringBuilder();
        for (Book book : bookList) {
            bookrecnos.append(book.getRecno() + ",");
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
            String bookrecno = book.getRecno();
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

    public void doubanSave(Book book) {
        String isbn = book.getIsbn();
        String s = HttpUtil.get(doubanUrl_1 + isbn + doubanUrl_2);
        System.out.println(s);
    }

    /**
     * 爬书的列表并保存
     */
    public void crawlAndSave() {
        //页码
        int page = 1;
        String url = baseUrl + "&page=" + page;
        //发各种请求解析出bookList
        List<Book> bookList = parseHtmlToBookList(url);
        //只要不为空
        while (bookList != null) {
            //遍历书的列表
            for (Book book : bookList) {
                //如果数据库中还没存这本书，这里以recno作为id区分
                List<Book> booksByRecno = bookRepository.findBooksByRecno(book.getRecno());
                if (CollectionUtils.isEmpty(booksByRecno)) {
                    //先处理豆瓣api：
                    doubanSave(book);
                    //再保存到数据库
                    bookRepository.save(book);
                    System.out.println("write to database: " + book);
                }
            }
            //继续下一页
            page++;
            url = baseUrl + "&page=" + page;
            bookList = parseHtmlToBookList(url);
            System.out.println("page = " + page);
        }
    }

    @Test
    public void runCrawlBookList() {
        crawlAndSave();
    }
}
