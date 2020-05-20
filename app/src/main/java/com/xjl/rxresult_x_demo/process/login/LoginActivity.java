package com.xjl.rxresult_x_demo.process.login;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.xjl.rxresult_x_demo.R;
import com.xjl.rxresult_x_demo.process.login.bean.LoginInfo;
import com.xjl.rxresult_x_demo.process.login.bean.User;
import com.xjl.rxresult_x_demo.process.login.steps.LoginByPwdFragment;
import com.xjl.rxresult_x_demo.process.login.steps.LoginBySmsFragment;
import com.xjl.rxresult_x.RXResult;
import com.xjl.rxresult_x.bean.ActivityResult;
import com.xjl.rxresult_x.util.IntentBuilder;
import com.xjl.rxresult_x.util.ProcessHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.rxjava3.core.ObservableTransformer;

/**
 * 一个业务流程对应一个 Activity，这个 Activity 作为对外的接口以及流程内部步骤的调度者；
 * 一个流程内部的一个步骤对应一个 Fragment，这个 Fragment 只负责完成自己的任务以及把自己的数据反馈给 Activity；
 * 流程对外暴露的接口应该封装为一个 ObservableTransformer，
 * 流程发起者应该提供发起流程的 Observable（例如以 RxView.clicks 的形式提供），两者通过 compose 操作符关联起来。
 * <p>
 * <p>
 * 可以看到，即使是流程嵌套的情况下，使用 RxJava 封装的流程依然不会使流程跳转的代码显得十分混乱，
 * 这点十分可贵，因为这意味着今后流程相关代码不会成为项目中难以维护的模块，而是清晰且高内聚的。
 * <p>
 * 但是，函数式编程是一把双刃剑，它也会给你带来不利的因素，一方面，这意味着你的团队都需要了解函数式编程的思想，
 * 另一方面，函数式的编程风格，意味着代码会比原先更加抽象。
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    public static final int REQUEST_CODE_LOGIN = 10001;

    private LoginByPwdFragment loginByPwdFragment;

    private LoginBySmsFragment loginBySmsFragment;

    private ProcessHelper processHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        processHelper = new ProcessHelper(this, R.id.frame_layout);
        loginByPwdFragment = ProcessHelper.findOrCreateFragment(LoginByPwdFragment.class, this);
        loginBySmsFragment = ProcessHelper.findOrCreateFragment(LoginBySmsFragment.class, this);

        processHelper.push(loginByPwdFragment);

        loginByPwdFragment.setCallback(new LoginByPwdFragment.Callback() {
            @Override
            public void loginByPwdCallback(Boolean needSmsVerify, User user) {

                if (needSmsVerify) {
                    loginBySmsFragment.setParams(user);
                    processHelper.push(loginBySmsFragment);
                } else {
                    LoginInfo.currentLoginUser = user;
                    RXResult.setResult(LoginActivity.this,
                            Activity.RESULT_OK,
                            IntentBuilder.newInstance().putExtra("user", user).build());
                    finish();
                }

            }

            @Override
            public void registerCallback(User user) {
                LoginInfo.currentLoginUser = user;
                RXResult.setResult(LoginActivity.this,
                        Activity.RESULT_OK,
                        IntentBuilder.newInstance().putExtra("user", user).build());
                finish();
            }
        });


        loginBySmsFragment.setCallback(new LoginBySmsFragment.Callback() {
            @Override
            public void LoginBySmsCallback(User user) {
                LoginInfo.currentLoginUser = user;
                RXResult.setResult(LoginActivity.this,
                        Activity.RESULT_OK,
                        IntentBuilder.newInstance().putExtra("user", user).build());
                finish();
            }
        });


    }

    private static final int REQUEST_LOGIN = 401;

    public static ObservableTransformer<Bundle, Bundle> ensureLogin(final AppCompatActivity activity, int requestCode) {
        return upstream -> {
            upstream.subscribe(inputBundle -> {
                if (LoginInfo.isLogin()) {
                    RXResult.insertActivityResult(activity,
                            new ActivityResult(requestCode,
                                    Activity.RESULT_OK,
                                    IntentBuilder.newInstance()
                                            .putExtra("user", LoginInfo.currentLoginUser)
                                            .build(),
                                    null,
                                    inputBundle )
                    );
                } else {
                    RXResult.startActivityForResult(
                            activity,
                            new Intent(activity, LoginActivity.class),
                            requestCode,
                            inputBundle
                    );

                }
            });

            return RXResult.obtainActivityResult(activity)
                    .filter(result -> result.requestCode == requestCode)
                    .filter(result -> result.resultCode == Activity.RESULT_OK)
                    .map(result ->{
                        Bundle bundle =result.getRequestContextData();
                        return bundle;
                    });
        };
    }

}
