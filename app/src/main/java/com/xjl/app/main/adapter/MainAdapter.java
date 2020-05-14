package com.xjl.app.main.adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xjl.app.R;
import com.xjl.app.main.bean.MainItemBean;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<MainItemBean> list;

    private Activity activity;

    private LayoutInflater inflater;


    public MainAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = this.activity.getLayoutInflater();
        list = new ArrayList<>();
        list.add(new MainItemBean("电视剧西游记挣了多少钱？", R.drawable.t1, 10, 5, false));
        list.add(new MainItemBean("史泰龙前妻宣布怀孕，与第5任老公生第5胎！网友：把史泰龙害好惨", R.drawable.t2, 5, 10, false));
        list.add(new MainItemBean("细数女星结婚,时的伴娘服，阿娇最良心，Angelababy的最差劲", R.drawable.t3, 3, 35, false));
        list.add(new MainItemBean("《放羊的星星》2018年启动重拍，主角人选大家还满意吗？", R.drawable.t4, 123, 4, false));
        list.add(new MainItemBean("参加湖南卫视的《变形计》的孩子们现在都怎么样了？", R.drawable.t5, 0, 0, false));
        list.add(new MainItemBean("易烊千玺高考前爆蓄须照，荷尔蒙爆表引迷妹尖叫：鼻血止不住了！", R.drawable.t6, 3, 0, false));
        list.add(new MainItemBean("李亚鹏谈与王菲离婚眼含泪水，大半年才释然，但仍有最愧疚的人", R.drawable.t7, 19, 0, false));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.layout_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainItemBean item = list.get(position);
        holder.image.setImageResource(item.imageRes);
        holder.title.setText(item.title);
        holder.comment.setText(String.format("评论 %d", item.commentCount));
        holder.favor.setText(String.valueOf(item.favCount));

        holder.fav_icon.setImageResource(item.fav ? R.drawable.favorite : R.drawable.favorite_border);

        holder.fav_icon.setOnClickListener(v -> itemLikeClicks.onNext(position));

        holder.comment.setOnClickListener(v -> itemCommentClicks.onNext(position));

    }

    PublishSubject<Integer> itemLikeClicks = PublishSubject.create();

    PublishSubject<Integer> itemCommentClicks = PublishSubject.create();

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
