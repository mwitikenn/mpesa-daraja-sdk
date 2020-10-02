package com.indepthkenya.mpesa.utils;

import com.indepthkenya.mpesa.domain.InboundStkPushRequest;
import com.indepthkenya.mpesa.handlers.MpesaHandler;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.server.Server;
import spark.Spark;

import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ApiCreationUtil {
    static ObjectMapper mapper = new ObjectMapper();
    static Logger logger = Logger.getLogger("");


    public static void startAPIs(int port) {
        String callbackUrl = "http://51.158.100.191:" + port + "/api/request/mpesa/callbackUrl";
        String timeoutCallbackUrl = "http://51.158.100.191:" + port + "/api/request/mpesa/timeoutCallbackUrl";
        Spark.port(port);

        Spark.post("/api/request/mpesa/stkPush", (request, response) -> {

            InboundStkPushRequest inboundStkPushRequest = mapper.readValue(request.body(), InboundStkPushRequest.class);

            logger.info("STK push request incoming " + inboundStkPushRequest);

            Map<String, String> responseMap = new HashMap<>();

            response.type("application/json");
            response.status(HttpServletResponse.SC_OK);

            if (inboundStkPushRequest.getAmount() == null) {
                String reason = "Amount is missing in request";
                responseMap.put("status", "INVALID_REQUEST");
                responseMap.put("reason", reason);
                String responseStr = mapper.writeValueAsString(responseMap);
                return responseStr;
            }

            if (inboundStkPushRequest.getBusinessShortCode() == null) {
                String reason = "Business shortcode is missing in request";
                responseMap.put("status", "INVALID_REQUEST");
                responseMap.put("reason", reason);
                String responseStr = mapper.writeValueAsString(responseMap);
                return responseStr;
            }

            if (inboundStkPushRequest.getConsumerKey() == null) {
                String reason = "Consumer key is missing in request";
                responseMap.put("status", "INVALID_REQUEST");
                responseMap.put("reason", reason);
                String responseStr = mapper.writeValueAsString(responseMap);
                return responseStr;
            }

            if (inboundStkPushRequest.getConsumerSecret() == null) {
                String reason = "Consumer secret is missing in request";
                responseMap.put("status", "INVALID_REQUEST");
                responseMap.put("reason", reason);
                String responseStr = mapper.writeValueAsString(responseMap);
                return responseStr;
            }

            if (inboundStkPushRequest.getPassKey() == null) {
                String reason = "Passkey is missing in request";
                responseMap.put("status", "INVALID_REQUEST");
                responseMap.put("reason", reason);
                String responseStr = mapper.writeValueAsString(responseMap);
                return responseStr;
            }

            if (inboundStkPushRequest.getTransactionDesc() == null) {
                String reason = "Transaction desc is missing in request";
                responseMap.put("status", "INVALID_REQUEST");
                responseMap.put("reason", reason);
                String responseStr = mapper.writeValueAsString(responseMap);
                return responseStr;
            }

            //generate password
            String timeStamp = "20200928122207";

            //"174379" + "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919"
            String stringToEncode = inboundStkPushRequest.getBusinessShortCode() + inboundStkPushRequest.getPassKey() + timeStamp;
            byte[] bytes = stringToEncode.getBytes("ISO-8859-1");
            String encodedStr = Base64.getEncoder().encodeToString(bytes);

            MpesaHandler.STKPushSimulation(inboundStkPushRequest.getConsumerKey(), inboundStkPushRequest.getConsumerSecret(), inboundStkPushRequest.getBusinessShortCode(), encodedStr, timeStamp, "CustomerPayBillOnline", inboundStkPushRequest.getAmount(), inboundStkPushRequest.getMsisdn(), inboundStkPushRequest.getMsisdn(), inboundStkPushRequest.getBusinessShortCode(), callbackUrl, timeoutCallbackUrl, "TRX:1", "Payment for goods test", logger);

            String reason = "Please accept push on your phone";
            response.type("application/json");
            response.status(HttpServletResponse.SC_OK);
            // get all post (using HTTP get method)
            responseMap.put("status", "SUCCESSFULLY_SENT_STK_PUSH");
            responseMap.put("reason", reason);
            String responseStr = mapper.writeValueAsString(responseMap);
            return responseStr;
        });


        Spark.post("/api/request/mpesa/callbackUrl", (request, response) -> {

            logger.info("STK push request incoming " + request.body());
            Map<String, String> resultMap = new HashMap<>();

            String reason = "Received successfully";
            response.type("application/json");
            response.status(HttpServletResponse.SC_OK);
            // get all post (using HTTP get method)
            resultMap.put("status", "success");
            resultMap.put("reason", reason);
            String responseStr = mapper.writeValueAsString(resultMap);
            return responseStr;
        });

        Spark.post("/api/request/mpesa/timeoutCallbackUrl", (request, response) -> {

            logger.info("STK push request incoming " + request.body());
            Map<String, String> resultMap = new HashMap<>();

            String reason = "Received successfully";
            response.type("application/json");
            response.status(HttpServletResponse.SC_OK);
            // get all post (using HTTP get method)
            resultMap.put("status", "success");
            resultMap.put("reason", reason);
            String responseStr = mapper.writeValueAsString(resultMap);
            return responseStr;
        });
    }
}
