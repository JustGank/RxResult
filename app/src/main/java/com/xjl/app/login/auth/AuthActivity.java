package com.xjl.app.login.auth;

import android.app.Activity;
import android.os.Bundle;

import com.xjl.app.R;
import com.xjl.app.login.login.bean.LoginInfo;
import com.xjl.rx_result_x.RXResult;
import com.xjl.rx_result_x.bean.ActivityResult;
import com.xjl.rx_result_x.util.IntentBuilder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.rxjava3.core.ObservableTransformer;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            LoginInfo.currentLoginUser.hasAuth = true;
            RXResult.setResult(AuthActivity.this, Activity.RESULT_OK, null);
            finish();
        });
    }


    private static int REQUEST_CODE_AUTH = 834;

    public static ObservableTransformer<Bundle, Bundle> ensureAuth(AppCompatActivity activity) {

        return upstream -> {
            upstream.subscribe(contextData -> {
                if (LoginInfo.currentLoginUser != null) {
                    if (!LoginInfo.currentLoginUser.hasAuth) {
                        RXResult.startActivityForResult(
                                activity,
                                IntentBuilder.newInstance(activity, AuthActivity.class)
                                        .build(),
                                REQUEST_CODE_AUTH,
                                contextData
                        );
                    } else {
                        RXResult.insertActivityResult(
                                activity,
                                new ActivityResult(
                                        REQUEST_CODE_AUTH,
                                        Activity.RESULT_OK,
                                        null));
                    }
                }
            });

            return RXResult.obtainActivityResult(activity)
                    .filter(result -> result.requestCode == REQUEST_CODE_AUTH)
                    .filter(result -> result.resultCode == Activity.RESULT_OK)
                    .map(result -> result.getRequestContextData());

        };


    }


}
