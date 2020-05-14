package com.xjl.app.login.pwdReset;

import android.app.Activity;
import android.os.Bundle;

import com.xjl.app.R;
import com.xjl.app.login.pwdReset.steps.PhoneCheckFragment;
import com.xjl.app.login.pwdReset.steps.PwdResetFragment;
import com.xjl.app.login.pwdReset.steps.SmsCheckForPwdResetFragment;
import com.xjl.rx_result_x.RXResult;
import com.xjl.rx_result_x.util.ProcessHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
}
