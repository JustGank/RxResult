package com.xjl.rxresult_x_demo.process.register.steps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xjl.rxresult_x_demo.R;
import com.xjl.rxresult_x.util.BundleBuilder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SmsCheckForRegisterFragment extends Fragment {

    View rootView;
    String phone;
    String session;

    public void setParams(String phone, String session) {
        setArguments(BundleBuilder.newInstance()
                .putString("phone", phone)
                .putString("session", session)
                .build());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sms_verify_no_title, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        phone = bundle.getString("phone");
        session = bundle.getString("session");
        rootView.findViewById(R.id.check).setOnClickListener(v->{
            callback.smsCheckForRegister();
        });
    }


    private  Callback callback;

    public void setCallback( Callback callback) {
        this.callback=callback;
    }

    public interface Callback {
        public void smsCheckForRegister();
    }

}
