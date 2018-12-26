package com.zzs.networkcalculator;

import java.io.Serializable;


public class ResultData implements Serializable {
    private int mMessageType;
    private String mMessageText;
    private String mCalculateResult;


    public ResultData(int messageType, String calculateResult, String messageText) {
        this.mMessageType = messageType;
        this.mMessageText = messageText;
        this.mCalculateResult = calculateResult;
    }

    public String getMessageText() {
        return mMessageText;
    }

    public void setMessageText(String messageText) {
        mMessageText = messageText;
    }

    public String getCalculateResult() {
        return mCalculateResult;
    }

    public void setCalculateResult(String calculateResult) {
        mCalculateResult = calculateResult;
    }

    public int getMessageType() {

        return mMessageType;
    }

    public void setMessageType(int messageType) {
        mMessageType = messageType;
    }
}

