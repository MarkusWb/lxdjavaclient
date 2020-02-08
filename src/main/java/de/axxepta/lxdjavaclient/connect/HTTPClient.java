package de.axxepta.lxdjavaclient.connect;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import spark.route.HttpMethod;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

public class HTTPClient {

    private final String keyStorePwd;
    private final String keyStoreFile;
    private final String host;
    private final int port;

    public HTTPClient(String keyStoreFile, String keyStorePwd, String host, int port) {
        this.keyStorePwd = keyStorePwd;
        this.keyStoreFile = keyStoreFile;
        this.host = host;
        this.port = port;
    }

    private String get(CloseableHttpClient httpClient, String url) throws Exception {
        System.out.println("GET " + url);
        HttpGet httpGet = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            return EntityUtils.toString(response.getEntity());
        }
    }

    private String delete(CloseableHttpClient httpClient, String url) throws Exception {
        System.out.println("DELETE " + url);
        HttpDelete httpDelete = new HttpDelete(url);
        try (CloseableHttpResponse response = httpClient.execute(httpDelete)) {
            return EntityUtils.toString(response.getEntity());
        }
    }

    private String post(CloseableHttpClient httpClient, String url, HttpEntity stringEntity) throws Exception {
        System.out.println("POST " + url);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(stringEntity);
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            return EntityUtils.toString(response.getEntity());
        }
    }

    private String put(CloseableHttpClient httpClient, String url, HttpEntity stringEntity) throws Exception {
        System.out.println("PUT " + url);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(stringEntity);
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            return EntityUtils.toString(response.getEntity());
        }
    }

    private String patch(CloseableHttpClient httpClient, String url, HttpEntity stringEntity) throws Exception {
        System.out.println("PATCH " + url);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(stringEntity);
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            return EntityUtils.toString(response.getEntity());
        }
    }

    public String p(HttpMethod method, String path, String... content) throws Exception {
        try (CloseableHttpClient httpClient = getClient()) {
            String url = "https://" + host + ":" + port + path;
            if (content.length < 1) {
                if (method.equals(HttpMethod.get)) {
                    return get(httpClient, url);
                } else {
                    return delete(httpClient, url);
                }
            } else {
                HttpEntity stringEntity;
                stringEntity = new StringEntity(content[0], ContentType.APPLICATION_JSON);
                switch (method) {
                    case patch:
                        return patch(httpClient, url, stringEntity);
                    case put:
                        return put(httpClient, url, stringEntity);
                    default:
                        return post(httpClient, url, stringEntity);
                }
            }
        }
    }

    // Use keystore file as client identification, assume server certificate to be trustworthy
    private CloseableHttpClient getClient() throws Exception {
        HttpClientBuilder builder = HttpClients.custom();
        try {
            KeyStore ks = KeyStore.getInstance("pkcs12");
            ks.load(new FileInputStream(keyStoreFile), keyStorePwd.toCharArray());

            SSLContextBuilder contextBuilder = SSLContexts.custom();
            SSLContext sslContext = contextBuilder
                    .loadKeyMaterial(ks, keyStorePwd.toCharArray())
                    .loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true).build();

            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);

            return builder.setSSLSocketFactory(sslsf).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

}