package com.xjl.app.login.login.steps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xjl.app.R;
import com.xjl.app.login.login.bean.User;

import java.util.Random;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

        rootView.findViewById(R.id.tv_reset_pwd).setOnClickListener(v->{




        });


        rootView.findViewById(R.id.tv_register).setOnClickListener(v->{




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
