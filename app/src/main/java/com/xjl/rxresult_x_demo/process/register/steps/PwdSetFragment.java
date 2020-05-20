package com.xjl.rxresult_x_demo.process.register.steps;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.xjl.rxresult_x_demo.R;
import com.xjl.rxresult_x_demo.process.login.bean.User;
import com.xjl.rxresult_x.util.BundleBuilder;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PwdSetFragment extends Fragment {

    View rootView;

    String phone;
    String session;

    private EditText et_password, et_password_confirm;

    public void setParams(String phone, String session) {
        setArguments(BundleBuilder.newInstance()
                .putString("phone", phone)
                .putString("session", session)
                .build());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_set_pwd_no_title, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        phone = bundle.getString("phone");
        session = bundle.getString("session");

        et_password = rootView.findViewById(R.id.et_password);
        et_password_confirm = rootView.findViewById(R.id.et_password_confirm);

        rootView.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            if (!TextUtils.equals(et_password.getText().toString(), et_password_confirm.getText().toString())) {
                Toast.makeText(getActivity(), "密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }

            // 发起请求，注册用户，这里直接 Mock 一个注册成功

            callback.pwdSetCallback(new User(
                    UUID.randomUUID().toString(),
                    phone,
                    session,
                    "",
                    15,
                    false
            ));

        });
    }

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }


    public interface Callback {
        public void pwdSetCallback(User user);
    }


}
