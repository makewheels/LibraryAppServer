package com.eg.libraryappserver.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Http工具类
 *
 * @author Administrator
 */
public class HttpUtil {
    private static String userAgent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36";
    private static String contentType = "application/x-www-form-urlencoded";

//    public static String infiniteGet(String url) {
//        try {
//            //执行第一次
//            return tryGetOnce(url);
//        } catch (Exception e) {
//            //第一次错误
//            System.err.println("http get error: " + e.getMessage());
//            System.out.println("retry 1: " + url);
//            try {
//                //执行第二次
//                return tryGetOnce(url);
//            } catch (IOException ex) {
//                //第二次错误
//                System.err.println("http get error: " + e.getMessage());
//                System.out.println("retry 2: " + url);
//                try {
//                    //执行第三次
//                    return tryGetOnce(url);
//                } catch (IOException exc) {
//                    //第三次错误
//                    System.err.println("http get error: " + e.getMessage());
//                    System.out.println("retry 1: " + url);
//                    exc.printStackTrace();
//                }
//            }
//        }
//        return null;
//    }

    /**
     * 无限重试get请求
     *
     * @param url
     * @return
     */
    public static String infiniteGet(String url) {
        System.out.println(Thread.currentThread().getName() + " HttpClient GET: " + url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(20000)
                .setConnectionRequestTimeout(20000)
                .setSocketTimeout(20000)
                .build();
        //无限重试client
        CloseableHttpClient client = HttpClientBuilder.create()
                .setRetryHandler((exception, executionCount, context) -> {
                    System.err.println("重试异常：" + exception.getMessage());
                    return true;
                })
                .setDefaultRequestConfig(requestConfig)
                .build();
        HttpGet httpGet = new HttpGet();
        httpGet.setConfig(requestConfig);
        httpGet.addHeader("User-Agent", userAgent);
        httpGet.setHeader("Content-type", contentType);
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setURI(URI.create(url));
        CloseableHttpResponse response;
        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, Constants.CHARSET);
        } catch (IOException e) {
            System.err.println("} catch (IOException e) {");
            e.printStackTrace();
            return infiniteGet(url);
        }
    }

    /**
     * 发送post请求
     *
     * @param url
     * @return
     */
    public static String tryPostOnce(String url, Map<String, String> param) {
        System.out.println("HttpClient POST: " + url);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        // 装填参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (param != null) {
            for (Entry<String, String> entry : param.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        // 设置参数到请求对象中
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        httpPost.setHeader("User-Agent", userAgent);
        httpPost.setHeader("Content-type", contentType);
        String body = null;
        try {
            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                body = EntityUtils.toString(entity, Constants.CHARSET);
            }
            EntityUtils.consume(entity);
            response.close();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return body;
    }

}
