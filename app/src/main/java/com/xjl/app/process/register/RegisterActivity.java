package com.xjl.app.process.register;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.xjl.app.R;
import com.xjl.app.process.login.bean.User;
import com.xjl.app.process.register.steps.PhoneRegisterFragment;
import com.xjl.app.process.register.steps.PwdSetFragment;
import com.xjl.app.process.register.steps.SmsCheckForRegisterFragment;
import com.xjl.rx_result_x.RXResult;
import com.xjl.rx_result_x.util.IntentBuilder;
import com.xjl.rx_result_x.util.ProcessHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import io.reactivex.rxjava3.core.ObservableTransformer;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    String phone;
    String session;

    PhoneRegisterFragment phoneRegisterFragment;
    SmsCheckForRegisterFragment smsCheckForRegisterFragment;
    PwdSetFragment pwdSetFragment;

    ProcessHelper processHelper;


    TextView step1, step2, step3;
    private int currentStep;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        step1 = findViewById(R.id.step1);
        step2 = findViewById(R.id.step2);
        step3 = findViewById(R.id.step3);


        processHelper = new ProcessHelper(this, R.id.frame_layout);

        phoneRegisterFragment = processHelper.findOrCreateFragment(PhoneRegisterFragment.class);
        smsCheckForRegisterFragment = processHelper.findOrCreateFragment(SmsCheckForRegisterFragment.class);
        pwdSetFragment = processHelper.findOrCreateFragment(PwdSetFragment.class);

        setCurrentStep(1);
        processHelper.push(phoneRegisterFragment);

        phoneRegisterFragment.setCallback((s1, s2) -> {
            this.phone = s1;
            this.session = s2;
            smsCheckForRegisterFragment.setParams(s1, s2);
            setCurrentStep(2);
            processHelper.push(smsCheckForRegisterFragment);

        });

        smsCheckForRegisterFragment.setCallback(() -> {
            setCurrentStep(3);
            pwdSetFragment.setParams(this.phone, this.session);
            processHelper.push(pwdSetFragment);

        });

        pwdSetFragment.setCallback(user -> {

            RXResult.setResult(RegisterActivity.this,
                    Activity.RESULT_OK,
                    IntentBuilder.newInstance().putExtra("user", user).build());

            finish();
        });
    }


    private void setCurrentStep(int i) {
        currentStep = i;
        step1.setTextColor(i == 1 ? Color.parseColor("#ffffff") : getResources().getColor(R.color.colorAccent));
        step2.setTextColor(i == 2 ? Color.parseColor("#ffffff") : getResources().getColor(R.color.colorAccent));
        step3.setTextColor(i == 3 ? Color.parseColor("#ffffff") : getResources().getColor(R.color.colorAccent));

        step1.setBackgroundColor(i == 1 ? getResources().getColor(R.color.colorAccent) : Color.parseColor("#ffffff"));
        step2.setBackgroundColor(i == 2 ? getResources().getColor(R.color.colorAccent) : Color.parseColor("#ffffff"));
        step3.setBackgroundColor(i == 3 ? getResources().getColor(R.color.colorAccent) : Color.parseColor("#ffffff"));
    }

    private static int REQUEST_CODE_LOGIN = 1001;

    public static <T> ObservableTransformer<T, User> register(Fragment fragment) {
        return upstream -> {
            upstream.subscribe(it -> {

                RXResult.startActivityForResult(fragment,
                        IntentBuilder.newInstance(fragment.getContext(), RegisterActivity.class).build(),
                        REQUEST_CODE_LOGIN);

            });

            return RXResult.obtainActivityResult(fragment)
                    .filter(result -> result.requestCode == REQUEST_CODE_LOGIN)
                    .filter(result -> result.resultCode == Activity.RESULT_OK)
                    .map(result -> {
                        Log.e(TAG, "result.getRequestContextData() is null =" + (result.getRequestContextData() == null));
                        return (User) result.getData().getSerializableExtra("user");
                    });
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setCurrentStep(--currentStep);
    }
}
