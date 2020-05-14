package com.xjl.app.main.activity;

import android.os.Bundle;

import com.xjl.app.R;
import com.xjl.app.common.CommentActivity;
import com.xjl.app.login.auth.AuthActivity;
import com.xjl.app.login.login.LoginActivity;
import com.xjl.app.main.adapter.MainAdapter;
import com.xjl.app.main.bean.MainItemBean;
import com.xjl.rx_result_x.util.BundleBuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.rxjava3.subjects.PublishSubject;


public class MainActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    MainAdapter adapter;

    /**
     * 评论操作，绑定登录流程的 RequestCode
     */
    private int REQUEST_CODE_LOGIN_FOR_COMMENT = 1053;

    /**
     * 点赞操作，绑定登录流程的 RequestCode
     */
    private int REQUEST_CODE_LOGIN_FOR_LIKE = 1054;

    PublishSubject<Integer> itemLikeClicks = PublishSubject.create();

    PublishSubject<Integer> itemCommentClicks = PublishSubject.create();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        adapter = new MainAdapter(this);

        itemLikeClicks.map(index -> BundleBuilder.newInstance().putInt("index", index).build())
                .compose(LoginActivity.ensureLogin(MainActivity.this, REQUEST_CODE_LOGIN_FOR_LIKE))
                .map(bundle -> bundle.getInt("index"))
                .subscribe(index -> {
                    MainItemBean itemBean = adapter.getItem(index);
                    itemBean.favCount++;
                    adapter.notifyItemChanged(index);
                });

        itemCommentClicks.map(index -> BundleBuilder.newInstance().putInt("index", index).build())
                .compose(LoginActivity.ensureLogin(MainActivity.this, REQUEST_CODE_LOGIN_FOR_COMMENT))
                .compose(AuthActivity.ensureAuth(MainActivity.this))
                .compose(CommentActivity.startComment(MainActivity.this))
                .map(bundle -> bundle.getInt("index"))
                .subscribe(index -> {
                    MainItemBean itemBean = adapter.getItem(index);
                    itemBean.commentCount++;
                    adapter.notifyItemChanged(index);
                });


        adapter.setClickListener(itemLikeClicks, itemCommentClicks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

}
