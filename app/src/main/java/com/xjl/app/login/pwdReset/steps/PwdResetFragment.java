package com.xjl.app.login.pwdReset.steps;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.xjl.app.R;
import com.xjl.app.login.login.bean.User;
import com.xjl.rx_result_x.util.BundleBuilder;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PwdResetFragment extends Fragment {

    View rootView;
    String phone;
    String session;
    EditText et_password, et_password_confirm;

    public void setParams(String phone, String session) {
        setArguments(BundleBuilder.newInstance()
                .putString("phone", phone)
                .putString("session", session)
                .build());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_set_pwd, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        phone = bundle.getString("phone");
        session = bundle.getString("session");
        rootView.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            if (!TextUtils.equals(et_password.getText().toString(), et_password_confirm.getText().toString())) {
                Toast.makeText(getActivity(), "密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }

            callback.pwdResetCallback(new User(
                    UUID.randomUUID().toString(),
                    "Jack",
                    phone,
                    "",
                    18,
                    false
            ));

        });

    }


    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        public void pwdResetCallback(User user);
    }

}
