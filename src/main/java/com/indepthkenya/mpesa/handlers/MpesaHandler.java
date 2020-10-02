package com.indepthkenya.mpesa.handlers;

import com.indepthkenya.mpesa.domain.AccessToken;
import com.indepthkenya.mpesa.utils.HTTPClient;
import com.indepthkenya.mpesa.utils.HTTPResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class MpesaHandler {
    static ObjectMapper objectMapper = new ObjectMapper();
    static HTTPClient httpClient = new HTTPClient(2000, 3000);

    public static AccessToken authenticate(String appKey, String appSecret, Logger logger) throws IOException, UnsupportedEncodingException {
        AccessToken accessToken = null;
        String appKeySecret = appKey + ":" + appSecret;
        byte[] bytes = appKeySecret.getBytes("ISO-8859-1");
        String encoded = Base64.getEncoder().encodeToString(bytes);


        OkHttpClient client = new OkHttpClient();
        logger.info("About to request for aurization token");
        Request request = new Request.Builder()
            .url("https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials")
            .get()
            .addHeader("authorization", "Basic " + encoded)
            .addHeader("cache-control", "no-cache")

            .build();
        logger.info("Request: " + request + "|url: " + "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials" + " about to send authorization request");
        Response response = client.newCall(request).execute();
        logger.info("Request: " + request + " the response: " + response + " successfully acquired authorization token");
        String responsePayload = response.body().string();
        accessToken = objectMapper.readValue(responsePayload, AccessToken.class);
        logger.info("Access token " + accessToken);

        return accessToken;
    }

    public static String STKPushSimulation(String consumerKey, String consumerSecret, String businessShortCode, String password, String timestamp, String transactionType, String amount, String phoneNumber, String partyA, String partyB, String callBackURL, String queueTimeOutURL, String accountReference, String transactionDesc, Logger logger) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        String url = "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest";

        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        headers.put("authorization", "Bearer " + authenticate(consumerKey, consumerSecret, logger).getAccess_token());
        headers.put("cache-control", "no-cache");


        Map<String, Object> request = new HashMap<>();
        request.put("BusinessShortCode", businessShortCode);
        request.put("Password", password);
        request.put("Timestamp", timestamp);
        request.put("TransactionType", transactionType);
        request.put("Amount", amount);
        request.put("PhoneNumber", phoneNumber);
        request.put("PartyA", partyA);
        request.put("PartyB", partyB);
        request.put("CallBackURL", callBackURL);
        request.put("AccountReference", accountReference);
        request.put("QueueTimeOutURL", queueTimeOutURL);
        request.put("TransactionDesc", transactionDesc);


        String requestBody = objectMapper.writeValueAsString(request);

        logger.info("Request: " + requestBody + "|headers: " + headers.toString() + "|url: " + url + " about to send stk push request");

        HTTPResponse httpResponse = httpClient.send(url, requestBody, "POST", headers, 2000, 3000, logger);

        logger.info("Request: " + requestBody + "|response: " + httpResponse + " successfully submitted request");

        return httpResponse.getBody();
    }
}
