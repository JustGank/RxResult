package com.xjl.rxresult_x_demo.process.pwdReset;

import android.app.Activity;
import android.os.Bundle;

import com.xjl.rxresult_x_demo.R;
import com.xjl.rxresult_x_demo.process.pwdReset.steps.PhoneCheckFragment;
import com.xjl.rxresult_x_demo.process.pwdReset.steps.PwdResetFragment;
import com.xjl.rxresult_x_demo.process.pwdReset.steps.SmsCheckForPwdResetFragment;
import com.xjl.rxresult_x.RXResult;
import com.xjl.rxresult_x.bean.ActivityResult;
import com.xjl.rxresult_x.util.IntentBuilder;
import com.xjl.rxresult_x.util.ProcessHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import io.reactivex.rxjava3.core.ObservableTransformer;

public class PwdResetActivity extends AppCompatActivity {

    PhoneCheckFragment phoneCheckFragment;
    PwdResetFragment pwdResetFragment;
    SmsCheckForPwdResetFragment smsCheckForPwdResetFragment;

    String phone;
    String session;

    ProcessHelper processHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_reset);

        processHelper = new ProcessHelper(this, R.id.frame_layout);
        phoneCheckFragment = processHelper.findOrCreateFragment(PhoneCheckFragment.class);
        smsCheckForPwdResetFragment = processHelper.findOrCreateFragment(SmsCheckForPwdResetFragment.class);
        pwdResetFragment = processHelper.findOrCreateFragment(PwdResetFragment.class);

        processHelper.push(phoneCheckFragment);

        phoneCheckFragment.setCallback((phone, session) -> {
            this.phone = phone;
            this.session = session;
            smsCheckForPwdResetFragment.setParams(phone, session);
            processHelper.push(smsCheckForPwdResetFragment);
        });

        smsCheckForPwdResetFragment.setCallback(() -> {
            pwdResetFragment.setParams(phone, session);
            processHelper.push(pwdResetFragment);
        });

        pwdResetFragment.setCallback(user -> {
            RXResult.setResult(this, Activity.RESULT_OK);
            finish();
        });
    }

    private static int REQUEST_CODE_PWD_RESET = 1014;

    public static ObservableTransformer<Object, ActivityResult> resetPwd(Fragment fragment) {
        return upstream -> {
            upstream.subscribe(it -> {
                RXResult.startActivityForResult(fragment,
                        IntentBuilder.newInstance(fragment.getContext(),PwdResetActivity.class).build(),
                        REQUEST_CODE_PWD_RESET);
            });

            return RXResult.obtainActivityResult(fragment);
        };

    }


}
