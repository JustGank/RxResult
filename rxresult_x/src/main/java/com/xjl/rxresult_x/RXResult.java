package com.xjl.rxresult_x;

import android.content.Intent;
import android.os.Bundle;

import com.xjl.rxresult_x.bean.ActivityResult;
import com.xjl.rxresult_x.fragment.ActivityResultFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import io.reactivex.rxjava3.core.Observable;

public class RXResult {

    public static final String KEY_REQUEST_DATA = "__key_request_data";
    public static final String KEY_REQUEST_CONTEXT_DATA = "__key_request_context_data";
    

    public static void setResult(AppCompatActivity activity, int resultCode) {
        setResult(activity, resultCode, null);
    }

    public static void setResult(AppCompatActivity activity, int resultCode, Intent resultData) {

        if (resultData == null) {
            resultData = new Intent();
        }

        Bundle requestData = activity.getIntent().getExtras();

        if (requestData != null) {
            resultData.putExtra(KEY_REQUEST_DATA, requestData);
        }

        activity.setResult(resultCode, resultData);
    }

    public synchronized static Observable<ActivityResult> obtainActivityResult(AppCompatActivity activity) {
        return ActivityResultFragment.getActivityResultObservable(activity);
    }

    public synchronized static Observable<ActivityResult> obtainActivityResult(Fragment fragment) {
        return ActivityResultFragment.getActivityResultObservable(fragment);
    }

    public synchronized static void insertActivityResult(AppCompatActivity activity, ActivityResult activityResult) {
        ActivityResultFragment.insertActivityResult(activity, activityResult);
    }

    public synchronized static void insertActivityResult(Fragment fragment, ActivityResult activityResult) {
        ActivityResultFragment.insertActivityResult(fragment, activityResult);
    }

    public static void startActivityForResult(AppCompatActivity activity, Intent intent, int requestCode) {
        startActivityForResult(activity, intent, requestCode, null);
    }

    public static void startActivityForResult(Fragment fragment, Intent intent, int requestCode) {
        startActivityForResult(fragment, intent, requestCode, null);
    }

    public static void startActivityForResult(AppCompatActivity activity, Intent intent, int requestCode, Bundle requestContextData) {
        if (requestContextData != null && requestContextData.size() > 0) {
            intent.putExtra(KEY_REQUEST_CONTEXT_DATA, requestContextData);
        }
        ActivityResultFragment.startActivityForResult(activity, intent, requestCode);
    }

    public static void startActivityForResult(Fragment fragment, Intent intent, int requestCode, Bundle requestContextData) {
        if (requestContextData != null && requestContextData.size() > 0) {
            intent.putExtra(KEY_REQUEST_CONTEXT_DATA, requestContextData);
        }
        ActivityResultFragment.startActivityForResult(fragment, intent, requestCode);
    }


}
