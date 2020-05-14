package com.xjl.app.login.login;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.xjl.app.LoginInfo;
import com.xjl.app.R;
import com.xjl.rx_result_x.bean.ActivityResult;
import com.xjl.rx_result_x.fragment.ActivityResultFragment;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableTransformer;

/**
 *
 * 一个业务流程对应一个 Activity，这个 Activity 作为对外的接口以及流程内部步骤的调度者；
 * 一个流程内部的一个步骤对应一个 Fragment，这个 Fragment 只负责完成自己的任务以及把自己的数据反馈给 Activity；
 * 流程对外暴露的接口应该封装为一个 ObservableTransformer，
 * 流程发起者应该提供发起流程的 Observable（例如以 RxView.clicks 的形式提供），两者通过 compose 操作符关联起来。
 *
 *
 * 可以看到，即使是流程嵌套的情况下，使用 RxJava 封装的流程依然不会使流程跳转的代码显得十分混乱，
 * 这点十分可贵，因为这意味着今后流程相关代码不会成为项目中难以维护的模块，而是清晰且高内聚的。
 *
 * 但是，函数式编程是一把双刃剑，它也会给你带来不利的因素，一方面，这意味着你的团队都需要了解函数式编程的思想，
 * 另一方面，函数式的编程风格，意味着代码会比原先更加抽象。
 * */

public class LoginActivity extends FragmentActivity  {

    private static final String TAG = "LoginActivity";

    public static final int REQUEST_CODE_LOGIN=10001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.btn_login).setOnClickListener(v -> {
            loginState(LoginActivity.this)
                    .subscribe();

        });


    }

    public void doLick(){
        Log.e(TAG,"登录成功点赞");
    }



    public static Observable<ActivityResult> loginState(FragmentActivity fragmentActivity){
        if(LoginInfo.isLogin(1>0)){
            ActivityResultFragment.insertActivityResult(fragmentActivity,new ActivityResult(REQUEST_CODE_LOGIN));
        }else
        {
            Intent intent = new Intent(fragmentActivity, LoginActivity.class);
            ActivityResultFragment.startActivityForResult(fragmentActivity, intent, REQUEST_CODE_LOGIN);
        }

        return ActivityResultFragment.getActivityResultObservable(fragmentActivity)
                .filter(it->it.getRequestCode()==REQUEST_CODE_LOGIN)
                .filter(it->it.getResultCode()==RESULT_OK);


    }


    public static <T> ObservableTransformer<T, ActivityResult> ensureLogin(FragmentActivity activity) {
            return upstream -> {
                   Observable<ActivityResult> loginOkResult = ActivityResultFragment.getActivityResultObservable(activity)
                        .filter(ar -> ar.getRequestCode() == REQUEST_CODE_LOGIN)
                        .filter(ar -> ar.getResultCode() == RESULT_OK);

                   upstream.subscribe(t -> {
                       if (LoginInfo.isLogin(true)) {
                                           ActivityResultFragment.insertActivityResult(
                                               activity,
                                                new ActivityResult(REQUEST_CODE_LOGIN, RESULT_OK, new Intent()));
                                       } else {
                                           Intent intent = new Intent(activity, LoginActivity.class);
                                           ActivityResultFragment.startActivityForResult(activity, intent, REQUEST_CODE_LOGIN);
                                       }
                               });

                          return loginOkResult;
               };
        }


}
