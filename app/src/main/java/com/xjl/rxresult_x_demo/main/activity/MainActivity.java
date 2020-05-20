package com.xjl.rxresult_x_demo.main.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xjl.rxresult_x_demo.R;
import com.xjl.rxresult_x_demo.common.CommentActivity;
import com.xjl.rxresult_x_demo.main.adapter.MainAdapter;
import com.xjl.rxresult_x_demo.main.bean.MainItemBean;
import com.xjl.rxresult_x_demo.process.auth.AuthActivity;
import com.xjl.rxresult_x_demo.process.login.LoginActivity;
import com.xjl.rxresult_x.util.BundleBuilder;

import io.reactivex.rxjava3.subjects.PublishSubject;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

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

    PublishSubject<Integer> itemLikeClicks = null;

    PublishSubject<Integer> itemCommentClicks = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        adapter = new MainAdapter(this);

        itemLikeClicks = PublishSubject.create();
        itemCommentClicks = PublishSubject.create();

        itemLikeClicks
                //Map变化将传递过来的index转换成bundle
                .map(index -> BundleBuilder.newInstance().putInt("index", index).build())
                //处理第一个逻辑判断是否登录 入是一个bundile 出一个bundle
                .compose(LoginActivity.ensureLogin(MainActivity.this, REQUEST_CODE_LOGIN_FOR_LIKE))
                //对出的结果进行转换
                .map(bundle -> bundle.getInt("index"))
                //给观察者抛出结果
                .subscribe(index -> {
                    MainItemBean itemBean = adapter.getItem(index);
                    itemBean.favCount++;
                    adapter.notifyItemChanged(index);

                });

        itemCommentClicks
                //Map变化将传递过来的index转换成bundle
                .map(index -> BundleBuilder.newInstance().putInt("index", index).build())
                //体现 逻辑 流  是否登录-》是否验证-》开始评论-》评论成功回调
                .compose(LoginActivity.ensureLogin(MainActivity.this, REQUEST_CODE_LOGIN_FOR_COMMENT))
                .compose(AuthActivity.ensureAuth(MainActivity.this))
                .compose(CommentActivity.startComment(MainActivity.this))
                //Map变化将传递出去的index转换回来
                .map(bundle -> bundle.getInt("index"))
                //根据返回值处理业务
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
