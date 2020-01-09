package com.eg.libraryappserver.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
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


    public static String tryGet(String url) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet();
        httpGet.addHeader("User-Agent", userAgent);
        httpGet.setHeader("Content-type", contentType);
        httpGet.setHeader("Connection", "keep-alive");
        System.out.println("HttpClient GET: " + url);
        httpGet.setURI(URI.create(url));
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = response.getEntity();
        try {
            return EntityUtils.toString(entity, Constants.CHARSET);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String get(String url) {
        try {
            return tryGet(url);
        } catch (Exception e) {
            System.err.println("http get error: " + e.getMessage() + ", retry " + url);
            return tryGet(url);
        }
    }

    /**
     * 发送post请求
     *
     * @param url
     * @return
     */
    public static String post(String url, Map<String, String> param) {
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
