package com.troublemaker.utils.httputils;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Troublemaker
 * @date 2022- 04 28 20:21
 */
@Slf4j
public class HttpClientUtils {
    private static final int SUCCESS_CODE = 200;

    @SneakyThrows
    public static String doGetForEntity(CloseableHttpClient client, String url) {
        CloseableHttpResponse response = null;
        HttpEntity entity;
        String entityStr = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            response = client.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (SUCCESS_CODE == statusCode) {
                entity = response.getEntity();
                entityStr = EntityUtils.toString(entity, "utf-8");
            } else {
                log.info("响应失败：" + statusCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != response) {
                // 若响应不是200，手动关闭响应
                EntityUtils.consume(response.getEntity());
            }
        }
        return entityStr;
    }

    public static String doGetForHeaders(CloseableHttpClient client, String url) {
        CloseableHttpResponse response;
        String headerStr = null;
        Header[] headers;
        try {
            HttpGet httpGet = new HttpGet(url);
            response = client.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (SUCCESS_CODE == statusCode) {
                headers = response.getHeaders("Set-Cookie");
                // %3D换成=
                headerStr = Arrays.toString(headers).replace("%3D", "=");
            } else {
                log.info("响应失败：" + statusCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return headerStr;
    }

    @SneakyThrows
    public static String doApplicationPost(CloseableHttpClient client, String url, Map<String, String> map) {
        CloseableHttpResponse response = null;
        HttpEntity entity;
        String entityStr = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> parameters = new ArrayList<>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(parameters);
            httpPost.setEntity(encodedFormEntity);
            response = client.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (SUCCESS_CODE == statusCode) {
                entity = response.getEntity();
                entityStr = EntityUtils.toString(entity, "utf-8");
            } else {
                log.info("响应失败：" + statusCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != response) {
                // 若响应不是200，手动关闭响应
                EntityUtils.consume(response.getEntity());
            }
        }
        return entityStr;
    }

    @SneakyThrows
    public static String doJsonPostWithHeader(CloseableHttpClient client, String url, String params, Header header) {
        CloseableHttpResponse response = null;
        HttpEntity entity;
        String entityStr = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader(header);
            httpPost.setEntity(new StringEntity(params, StandardCharsets.UTF_8));
            response = client.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (SUCCESS_CODE == statusCode) {
                entity = response.getEntity();
                entityStr = EntityUtils.toString(entity, "utf-8");
            } else {
                log.info("响应失败：" + statusCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != response) {
                // 若响应不是200，手动关闭响应
                EntityUtils.consume(response.getEntity());
            }
        }
        return entityStr;
    }

    public static Header getHeader(String name, String value) {
        return new Header() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getValue() {
                return value;
            }

            @Override
            public HeaderElement[] getElements() throws ParseException {
                return new HeaderElement[0];
            }
        };
    }

    public static long timeDifference(String str) {
        URL url;
        HttpURLConnection urlConnection = null;
        long localDate = 0;
        long severDate = 0;
        try {
            url = new URL(str);
            urlConnection = (HttpURLConnection) url.openConnection();
            localDate = System.currentTimeMillis();
            urlConnection.connect();
            severDate = urlConnection.getDate();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        long difference = localDate - severDate;
        return difference > 0 ? difference : 0;
    }

    public static void clientClose(CloseableHttpClient client) {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
