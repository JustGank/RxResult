package com.xjl.app.login.register;

import android.app.Activity;
import android.os.Bundle;

import com.xjl.app.R;
import com.xjl.app.login.login.bean.User;
import com.xjl.app.login.register.steps.PhoneRegisterFragment;
import com.xjl.app.login.register.steps.PwdSetFragment;
import com.xjl.app.login.register.steps.SmsCheckForRegisterFragment;
import com.xjl.rx_result_x.RXResult;
import com.xjl.rx_result_x.util.IntentBuilder;
import com.xjl.rx_result_x.util.ProcessHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import io.reactivex.rxjava3.core.ObservableTransformer;

public class RegisterActivity extends AppCompatActivity {

    String phone;
    String session;

    PhoneRegisterFragment phoneRegisterFragment;
    SmsCheckForRegisterFragment smsCheckForRegisterFragment;
    PwdSetFragment pwdSetFragment;


    ProcessHelper processHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        processHelper = new ProcessHelper(this, R.id.frame_layout);

        phoneRegisterFragment = processHelper.findOrCreateFragment(PhoneRegisterFragment.class);
        smsCheckForRegisterFragment = processHelper.findOrCreateFragment(SmsCheckForRegisterFragment.class);
        pwdSetFragment = processHelper.findOrCreateFragment(PwdSetFragment.class);

        processHelper.push(phoneRegisterFragment);

        phoneRegisterFragment.setCallback((s1, s2) -> {
            this.phone = s1;
            this.session = s2;

            smsCheckForRegisterFragment.setParams(s1, s2);

            processHelper.push(smsCheckForRegisterFragment);

        });

        smsCheckForRegisterFragment.setCallback(() -> {
            pwdSetFragment.setParams(this.phone, this.session);
            processHelper.push(pwdSetFragment);

        });

        pwdSetFragment.setCallback(user -> {
            RXResult.setResult(RegisterActivity.this, Activity.RESULT_OK, IntentBuilder.newInstance().putExtra("user", user).build());
            finish();
        });
    }

    private static int REQUEST_CODE_LOGIN = 1001;

    public static <T> ObservableTransformer<T, User> register(Fragment fragment) {
        return upstream -> {
            upstream.subscribe(it -> {
                RXResult.startActivityForResult(fragment, IntentBuilder.newInstance(fragment.getContext(), RegisterActivity.class).build(), REQUEST_CODE_LOGIN);

            });

            return RXResult.obtainActivityResult(fragment)
                    .filter(result -> result.requestCode == REQUEST_CODE_LOGIN)
                    .filter(result -> result.resultCode == Activity.RESULT_OK)
                    .map(result -> (User) result.getRequestContextData().getSerializable("user"));
        };
    }

}
