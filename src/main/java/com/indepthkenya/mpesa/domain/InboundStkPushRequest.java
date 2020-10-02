package com.indepthkenya.mpesa.domain;

public class InboundStkPushRequest {

    private String msisdn;

    private String amount;

    private String consumerKey;

    private String consumerSecret;

    private String businessShortCode;

    private String transactionDesc;

    private String passKey;


    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getBusinessShortCode() {
        return businessShortCode;
    }

    public void setBusinessShortCode(String businessShortCode) {
        this.businessShortCode = businessShortCode;
    }

    public String getTransactionDesc() {
        return transactionDesc;
    }

    public void setTransactionDesc(String transactionDesc) {
        this.transactionDesc = transactionDesc;
    }

    public String getPassKey() {
        return passKey;
    }

    public void setPassKey(String passKey) {
        this.passKey = passKey;
    }

    @Override
    public String toString() {
        return "InboundRequest{" +
            "msisdn='" + msisdn + '\'' +
            ", amount='" + amount + '\'' +
            ", consumerKey='" + consumerKey + '\'' +
            ", consumerSecret='" + consumerSecret + '\'' +
            ", businessShortCode='" + businessShortCode + '\'' +
            ", transactionDesc='" + transactionDesc + '\'' +
            ", passKey='" + passKey + '\'' +
            '}';
    }
}
