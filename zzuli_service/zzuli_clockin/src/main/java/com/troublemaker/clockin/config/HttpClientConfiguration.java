package com.troublemaker.clockin.config;

import lombok.Data;
import lombok.SneakyThrows;
import org.apache.http.Header;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicHeader;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.config
 * @Author: troublemaker
 * @CreateTime: 2022-08-27  21:30
 * @Version: 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "data.client")
public class HttpClientConfiguration {
    private Integer socketTimeout;
    private Integer connectTimeout;
    private Integer connectionRequestTimeout;
    private Integer retryCount;

    @SneakyThrows
    @Bean
    public HttpClientBuilder getHttpClientBuilder() {

        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] xcs, String str) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] xcs, String str) {
            }
        };

        // ??????????????????
        SSLContext sslContext;
        sslContext = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
        sslContext.init(null, new TrustManager[]{trustManager}, null);

        // https ????????????
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);


        // ??????????????????
        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.STANDARD_STRICT)
                .setExpectContinueEnabled(Boolean.TRUE)
                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                .setProxyPreferredAuthSchemes(Collections.singletonList(AuthSchemes.BASIC))
                // ?????????????????????
                .setCircularRedirectsAllowed(true)
                // ?????????????????????????????????????????????????????????
                .setSocketTimeout(socketTimeout * 1000)
                // ?????????????????????TCP???????????????????????????
                .setConnectTimeout(connectTimeout * 1000)
                // ??????????????????????????????????????????????????????
//                .setConnectionRequestTimeout(connectionRequestTimeout * 1000)
                .build();

        // ???????????????
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.16 Safari/537.36"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
        headers.add(new BasicHeader("Accept-Language", "zh-CN"));
        headers.add(new BasicHeader("Connection", "Keep-Alive"));

        return HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setDefaultRequestConfig(requestConfig)
                .setDefaultHeaders(headers)
                .setRedirectStrategy(new LaxRedirectStrategy())
                // ??????????????????
                .setRetryHandler(new DefaultHttpRequestRetryHandler(retryCount, false));
    }
}

