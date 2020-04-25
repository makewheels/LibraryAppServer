package com.eg.libraryappserver.crawl.booklist;

import com.eg.libraryappserver.util.HttpUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @time 2020-04-25 10:13
 */
public class Test {
    public static void main(String[] args) {
        new Test().run();
    }

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    private void run() {
        List<String> urlList = new ArrayList<>();
        urlList.add("http://rrd.me/gEHdK");
        urlList.add("http://rrd.me/gEHed");
        urlList.add("http://rrd.me/gEHed");
        urlList.add("http://rrd.me/gEHed");
        urlList.add("http://rrd.me/gEHed");
        urlList.add("http://rrd.me/gEHed");
        urlList.add("http://rrd.me/gEHed");
        urlList.add("http://rrd.me/gEHed");
        urlList.add("http://rrd.me/gEHed");
        urlList.add("http://rrd.me/gEHed");
        urlList.add("http://rrd.me/gEHed");
        urlList.add("http://rrd.me/gEHed");
        urlList.add("http://rrd.me/gEHed");
        urlList.add("http://rrd.me/gEHed");
        urlList.add("http://rrd.me/gEHed");
        urlList.add("http://rrd.me/gEHed");
        urlList.add("http://rrd.me/gEHed");
        urlList.add("http://rrd.me/gEHee");
        urlList.add("http://rrd.me/gEHeg");
        for (String url : urlList) {
            executorService.execute(() -> {
                String s = HttpUtil.get(url);
                System.out.println(Thread.currentThread().getName() + " " + s);
            });
        }
        executorService.shutdown();
    }
}
