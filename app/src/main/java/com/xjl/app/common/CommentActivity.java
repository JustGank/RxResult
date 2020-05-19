package com.xjl.app.common;

import android.app.Activity;
import android.os.Bundle;

import com.xjl.app.R;
import com.xjl.rx_result_x.RXResult;
import com.xjl.rx_result_x.util.IntentBuilder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.rxjava3.core.ObservableTransformer;

public class CommentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        findViewById(R.id.btn_confirm).setOnClickListener(v->{

            RXResult.setResult(this,Activity.RESULT_OK,null);

            finish();

        });
    }

    private static final int REQUEST_CODE_COMMENT = 1000;

    public static ObservableTransformer<Bundle, Bundle> startComment(AppCompatActivity activity) {
        return upstream -> {
            upstream.subscribe(contextdata -> {
                RXResult.startActivityForResult(activity,
                        IntentBuilder.newInstance(activity, CommentActivity.class).build(),
                        REQUEST_CODE_COMMENT,
                        contextdata);
            });

            return RXResult.obtainActivityResult(activity)
                    .filter(result -> result.requestCode == REQUEST_CODE_COMMENT)
                    .filter(result -> result.resultCode == Activity.RESULT_OK)
                    .map(result->result.getRequestContextData());
        };
    }


}
