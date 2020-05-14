package com.xjl.rx_result_x.bean;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.xjl.rx_result_x.RXResult;

public class ActivityResult {

    public int requestCode;

    public int resultCode;

    public Intent intent;

    private Intent data;

    public ActivityResult(int requestCode, int resultCode, Intent intent) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.intent = intent;
    }


    public ActivityResult(int requestCode) {
        this.requestCode = requestCode;
        this.resultCode = Activity.RESULT_OK;
        this.intent = new Intent();
    }


    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }


    public Bundle getRequestContextData() {
        Bundle requestData = getRequestData();
        if (requestData != null) {
            return requestData.getBundle(RXResult.KEY_REQUEST_CONTEXT_DATA);
        } else {
            return null;
        }
    }

    public Bundle getRequestData() {
        if (data != null) {
            return data.getBundleExtra(RXResult.KEY_REQUEST_DATA);
        } else {
            return null;
        }
    }

}
