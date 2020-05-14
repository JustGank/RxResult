package com.xjl.app.login.login.steps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xjl.app.R;
import com.xjl.app.login.login.bean.User;
import com.xjl.rx_result_x.util.BundleBuilder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoginBySmsFragment extends Fragment {

    View rootView;

    private User user;

    public void setParams(User user) {
        setArguments(BundleBuilder.newInstance().putSerializable("user", user).build());


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sms_verify, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = (User) getArguments().getSerializable("user");

        rootView.findViewById(R.id.check).setOnClickListener(v -> {
            user.hasAuth = true;
            callback.LoginBySmsCallback(user);
        });


    }


    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        public void LoginBySmsCallback(User user);
    }


}

