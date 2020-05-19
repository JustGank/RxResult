package com.xjl.app.process.login.steps;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xjl.app.R;
import com.xjl.app.process.login.bean.User;
import com.xjl.app.process.pwdReset.PwdResetActivity;
import com.xjl.app.process.register.RegisterActivity;

import java.util.Random;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.reactivex.rxjava3.core.Observable;

public class LoginByPwdFragment extends Fragment {

    private View rootView;

    private Random random;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login_by_pwd, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        random = new Random();

        rootView.findViewById(R.id.sign_in).setOnClickListener(v -> {
            // 50% chance to enter sms verify
            if (random.nextBoolean()) {
                callback.loginByPwdCallback(true, new User(
                        UUID.randomUUID().toString(),
                        "Jack",
                        "13012345678"
                ));
            } else {
                callback.loginByPwdCallback(false, new User(UUID.randomUUID().toString(),
                        "Jack",
                        "13012345678",
                        "Shanghai",
                        29,
                        false));
            }
        });

        rootView.findViewById(R.id.tv_reset_pwd).setOnClickListener(v -> {
            Observable.just(v)
                    .compose(PwdResetActivity.resetPwd(this))
                    .subscribe(it -> {
                        Toast.makeText(getContext(),it.resultCode== Activity.RESULT_OK?"重置成功":"重置失败",Toast.LENGTH_SHORT).show();
                    });
        });


        rootView.findViewById(R.id.tv_register).setOnClickListener(v -> {

            Observable.just(v)
                    .compose(RegisterActivity.register(this))

                    .subscribe(user -> {
                        callback.registerCallback(user);
                    });

        });

    }

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {

        public void loginByPwdCallback(Boolean needSmsVerify, User user);

        public void registerCallback(User user);

    }

}
