package com.xjl.app.login.register;

import android.os.Bundle;

import com.xjl.app.R;
import com.xjl.app.login.register.steps.PhoneRegisterFragment;
import com.xjl.app.login.register.steps.PwdSetFragment;
import com.xjl.app.login.register.steps.SmsCheckForRegisterFragment;
import com.xjl.rx_result_x.util.ProcessHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    PhoneRegisterFragment phoneRegisterFragment;
    PwdSetFragment pwdSetFragment;
    SmsCheckForRegisterFragment smsCheckForRegisterFragment;

    ProcessHelper processHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    }
}
