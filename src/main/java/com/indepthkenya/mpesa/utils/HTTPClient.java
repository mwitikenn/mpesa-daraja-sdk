package com.indepthkenya.mpesa.utils;
import okhttp3.*;
import org.apache.log4j.Logger;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HTTPClient {

    OkHttpClient.Builder builder;

    public HTTPClient(long connectTimeout, long readTimeout) {

        builder = new OkHttpClient.Builder();
        builder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        builder.readTimeout(readTimeout, TimeUnit.MILLISECONDS);
    }

    public HTTPResponse send(String url, String requestBody, String method, Map<String, String> headers, int connectTimeout, int readTimeout, Logger logger) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        return send(url, requestBody, method, "application/json", headers, connectTimeout, readTimeout, logger);
    }

    public HTTPResponse send(String url, String requestBody, String method, Logger logger) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        return send(url, requestBody, method, new HashMap<>(), 2000, 5000, logger);
    }

    public HTTPResponse send(String url, String requestBody, String method, String contentType, Map<String, String> headers, int connectTimeout, int readTimeout, Logger logger) throws IOException, KeyManagementException, NoSuchAlgorithmException {

        OkHttpClient client = builder.build();

        if (url.contains("https")) {
            client = getUnsafeOkHttpClient();
        }

        MediaType mediaType = MediaType.parse(contentType);
        RequestBody body = RequestBody.create(mediaType, requestBody);

        Request request;

        //builder
        Request.Builder builder = new Request.Builder().url(url).addHeader("Content-Type", contentType);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }

        switch (method) {
            case "POST":
                builder.post(body);
                break;
            case "PUT":
                builder.put(body);
                break;
            case "DELETE":
                builder.delete(body);
                break;
            case "GET":
                builder.get();
                break;
            case "PATCH":
                builder.patch(body);
                break;
            default:
                throw new IOException("Unsupported Method '" + method + "'");
        }

        request = builder.build();

        logger.debug("transaction| url: " + url + "|connect_timeout: " + connectTimeout + "|read_timeout: " + readTimeout + "|request: " + request.toString());

        logger.debug("transaction| url: " + url + "|connect_timeout: " + connectTimeout + "|read_timeout: " + readTimeout + "|request_body: " + requestBody);

        Response response = null;

        try {
            response = client.newCall(request).execute();
        } catch (SocketTimeoutException ex) {
            return new HTTPResponse(500, "No response ");
        }

        int responseCode = 500;
        String responseStr = "No response ";

        logger.debug("transaction| url: " + url + "|connect_timeout: " + connectTimeout + "|read_timeout: " + readTimeout + "|response: " + response);

        if (response != null) {

            responseCode = response.code();

            if (response.body() != null) {
                responseStr = response.body().string();

                logger.debug("transaction| url: " + url + "|connect_timeout: " + connectTimeout + "|read_timeout: " + readTimeout + "|response_body: " + responseStr);
            }

        }

        return new HTTPResponse(responseCode, responseStr);
    }


    public OkHttpClient CustomTrust(OkHttpClient client) throws NoSuchAlgorithmException, KeyManagementException {

        SSLSocketFactory sslSocketFactory;

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustManagers = new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };
        // Install the all-trusting trust manager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, new java.security.SecureRandom());
        sslSocketFactory = sslContext.getSocketFactory();


        client = new OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustManagers[0])
            .build();
        return client;
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
