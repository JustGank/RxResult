package com.xjl.rx_result_x.bean;

import android.app.Activity;
import android.content.Intent;

public class ActivityResult {



    private int requestCode;

    private int resultCode;

    private Intent intent;

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
}
