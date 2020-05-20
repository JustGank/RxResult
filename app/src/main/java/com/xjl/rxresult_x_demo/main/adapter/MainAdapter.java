package com.xjl.rxresult_x_demo.main.adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xjl.rxresult_x_demo.R;
import com.xjl.rxresult_x_demo.main.bean.MainItemBean;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<MainItemBean> list;

    private Activity activity;

    private LayoutInflater inflater;

    PublishSubject<Integer> itemLikeClicks=null;

    PublishSubject<Integer> itemCommentClicks=null;

    public MainAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = this.activity.getLayoutInflater();
        list = new ArrayList<>();
        list.add(new MainItemBean("猩红女巫.旺达，被无限宝石赋予宇宙秘能，一位颜值身材爆标女法师", R.drawable.t1, 19, 0, false));
        list.add(new MainItemBean("钢铁侠，托尼.斯塔克，以凡人之躯，比肩神明，不知是他成就了战甲，还是战甲成就了他", R.drawable.t2, 10, 5, false));
        list.add(new MainItemBean("美国队长，一位精神高尚的超级战士，人物形象几乎贯穿整个漫威电影宇宙", R.drawable.t3, 5, 10, false));
        list.add(new MainItemBean("雷神.托尔，一名来自阿斯加德的中二王子，复联中既可以坑揍，又有输出的一位狠角色", R.drawable.t4, 3, 35, false));
        list.add(new MainItemBean("绿巨人，浩克。非常收影迷喜爱的一位超级英雄，平常时是一位温文尔雅的博士，一但发怒，见什么都想砸了它，型象完全颠倒", R.drawable.t5, 123, 4, false));
        list.add(new MainItemBean("黑寡妇，寡姐，颜值身材双双在线，神盾局高级特工，没有搞不定的情报", R.drawable.t6, 0, 0, false));
        list.add(new MainItemBean("幻视，使用心灵宝石和贾维斯创造出来的生化人，并且具有独立人格", R.drawable.t7, 3, 0, false));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.layout_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainItemBean item = list.get(position);

        Glide.with(activity).load(item.imageRes).into(holder.image);

        holder.title.setText(item.title);
        holder.comment.setText(String.format("评论 %d", item.commentCount));
        holder.favor.setText(String.valueOf(item.favCount));

        holder.fav_icon.setImageResource(item.fav ? R.drawable.favorite : R.drawable.favorite_border);

        holder.fav_icon.setOnClickListener(v ->{
            itemLikeClicks.onNext(position);
        } );

        holder.comment.setOnClickListener(v -> itemCommentClicks.onNext(position));

    }



    public void setClickListener(PublishSubject<Integer> itemLikeClicks, PublishSubject<Integer> itemCommentClicks) {
        this.itemLikeClicks = itemLikeClicks;
        this.itemCommentClicks = itemCommentClicks;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public MainItemBean getItem(int position) {
        return list.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public TextView comment;
        public ImageView fav_icon;
        public TextView favor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.image);
            this.title = itemView.findViewById(R.id.title);
            this.comment = itemView.findViewById(R.id.comment);
            this.fav_icon = itemView.findViewById(R.id.fav_icon);
            this.favor = itemView.findViewById(R.id.favor);
        }
    }

}
