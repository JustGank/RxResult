package com.xjl.rxresult_x.bean;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.xjl.rxresult_x.RXResult;

public class ActivityResult {

    public int requestCode;

    public int resultCode;

    private Intent data;

    public ActivityResult(int requestCode) {
        this.requestCode = requestCode;
        this.resultCode = Activity.RESULT_OK;
        this.data = new Intent();
    }

    public ActivityResult(int requestCode, int resultCode, Intent intent) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = intent;
    }

    public ActivityResult(int requestCode, int resultCode, Intent data, Bundle requestData, Bundle requestContextData) {

        this.requestCode = requestCode;
        this.resultCode = resultCode;

        if (requestData != null || requestContextData != null) {
            if (requestData == null) {
                requestData = new Bundle();
            }
            if (data == null) {
                data = new Intent();
            }
            requestData.putBundle(RXResult.KEY_REQUEST_CONTEXT_DATA, requestContextData);
            data.putExtra(RXResult.KEY_REQUEST_DATA, requestData);
        }

        this.data = data;
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
        return data;
    }

    public void setIntent(Intent data) {
        this.data = data;
    }

    public Intent getData() {
        return data;
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
