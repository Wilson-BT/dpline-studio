package com.dpline.common.util;

import com.dpline.common.Constants;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.util.Timeout;
import org.apache.http.client.config.AuthSchemes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * http utils
 */
public class HttpUtils {

    public static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);


    public static final String CHARSET = "UTF-8";

    private HttpUtils() {
        throw new UnsupportedOperationException("Construct HttpUtils");
    }

    public static CloseableHttpClient getInstance() {
        return HttpClientInstance.httpClient;
    }

    public static class HttpClientInstance {
        public static final CloseableHttpClient httpClient =
            HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }


    private static PoolingHttpClientConnectionManager cm;

    private static SSLContext ctx = null;

    private static SSLConnectionSocketFactory socketFactory;

    private static RequestConfig requestConfig;

    private static Registry<ConnectionSocketFactory> socketFactoryRegistry;

    private static X509TrustManager xtm = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };

    static {
        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{xtm}, null);
        } catch (NoSuchAlgorithmException e) {
            logger.error("SSLContext init with NoSuchAlgorithmException", e);
        } catch (KeyManagementException e) {
            logger.error("SSLContext init with KeyManagementException", e);
        }
        socketFactory = new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
        /** set timeout、request time、socket timeout */
        requestConfig = RequestConfig.custom()
                .setExpectContinueEnabled(Boolean.TRUE)
                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                .setProxyPreferredAuthSchemes(Collections.singletonList(AuthSchemes.BASIC))
                .setConnectTimeout(Timeout.ofMilliseconds(Constants.HTTP_CONNECT_TIMEOUT))
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(Constants.HTTP_CONNECTION_REQUEST_TIMEOUT))
                .setRedirectsEnabled(true)
                .build();
        socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", socketFactory).build();
        cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        // max routes each connection
        cm.setDefaultMaxPerRoute(60);
        // max connection nums
        cm.setMaxTotal(100);

    }


    /**
     * get http request content
     *
     * @param url url
     * @return http get request response content
     */
    public static String doGet(URI url) {
        CloseableHttpClient httpclient = HttpUtils.getInstance();
        HttpGet httpget = new HttpGet(url);
        return getExecute(httpget, httpclient);
    }


    /**
     * get http request content
     *
     * @param url url
     * @return http get request response content
     */
    public static String doPatch(URI url) {
        CloseableHttpClient httpclient = HttpUtils.getInstance();
        HttpPatch httpPatch = new HttpPatch(url);
        return getExecute(httpPatch, httpclient);
    }

    /**
     * @param url url
     * @param params form data
     * @param header request header
     * @return
     * @throws IOException
     */
    public static String doPost(String url, Map<String, String> params, Map<String, String> header) throws IOException {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        List<NameValuePair> pairs = null;
        if (params != null && !params.isEmpty()) {
            pairs = new ArrayList<>(params.size());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value = entry.getValue();
                if (value != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
        }
        HttpPost httpPost = new HttpPost(url);
        if (pairs != null && pairs.size() > 0) {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, Charset.defaultCharset()));
        }
        if (header != null && !header.isEmpty()) {
            header.forEach(httpPost::setHeader);
        }
        return postExecute(httpPost);
    }

    /**
     * get http response content
     *
     * @param httpUriRequest    httpUriRequest
     * @param httpClient httpClient
     * @return http get request response content
     */
    public static String getExecute(HttpUriRequestBase httpUriRequest, CloseableHttpClient httpClient) {
        String responseContent = null;
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpUriRequest);
            // check response status is 200
            if (response.getCode() == 200 || response.getCode() == 202) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    responseContent = EntityUtils.toString(entity, Constants.UTF_8);
                } else {
                    logger.warn("http entity is null");
                }
            } else {
                logger.error("http get:{} response status code is not 200!", response.getCode());
            }
        } catch (IOException | ParseException ioe) {
            logger.error(ioe.getMessage(), ioe);
        } finally {
            try {
                if (response != null) {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            if (!httpUriRequest.isAborted()) {
                httpUriRequest.abort();
            }

        }
        return responseContent;
    }

    public static String uploadFile(String url, File file){
        CloseableHttpResponse response = null;
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(StandardCharsets.UTF_8);
            builder.addPart("multipart/form-data",new FileBody(file));
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            response = HttpClientInstance.httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                result = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void doDelete(String uri) throws IOException {
        HttpDelete httpDelete = new HttpDelete(uri);
        HttpClientInstance.httpClient.execute(httpDelete);
    }

    public static String doStringBodyPost(String url,String jsonStr){
        if (StringUtils.isBlank(url)) {
            return null;
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(jsonStr));
        return postExecute(httpPost);
    }

    public static String postExecute(HttpPost httpPost){
        CloseableHttpResponse response = null;
        try {
            response = HttpClientInstance.httpClient.execute(httpPost);
            int statusCode = response.getCode();
            if (statusCode != 200 && statusCode != 202) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
            }
            return result;
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }



}
