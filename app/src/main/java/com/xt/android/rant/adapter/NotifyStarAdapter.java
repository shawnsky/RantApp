package com.xt.android.rant.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xt.android.rant.MainActivity;
import com.xt.android.rant.R;
import com.xt.android.rant.wrapper.CmtNotifyItem;
import com.xt.android.rant.wrapper.StarNotifyItem;

import java.util.Collections;
import java.util.List;

import static com.xt.android.rant.R.id.item_notify_cmt_tv_rant_content;

/**
 * Created by admin on 2017/5/11.
 */

public class NotifyStarAdapter extends RecyclerView.Adapter<NotifyStarAdapter.ViewHolder> {
    private List<StarNotifyItem> mStarNotifyItems;

    public NotifyStarAdapter(List<StarNotifyItem> starNotifyItems){
        Collections.reverse(starNotifyItems);
        this.mStarNotifyItems = starNotifyItems;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notify_star, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StarNotifyItem starNotifyItem = mStarNotifyItems.get(position);
        holder.bind(starNotifyItem);
    }

    @Override
    public int getItemCount() {
        return mStarNotifyItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView mName;
        private TextView mFlag;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.item_notify_star_iv_avatar);
            mName = (TextView) itemView.findViewById(R.id.item_notify_star_tv_name);
            mFlag = (TextView) itemView.findViewById(R.id.item_notify_star_tv_flag);
        }
        public void bind(StarNotifyItem starNotifyItem){
            Picasso.with(MainActivity.sMainActivity).load(starNotifyItem.getUserAvatar()).into(mImageView);
            mName.setText(starNotifyItem.getUserName());
            mFlag.setText(starNotifyItem.getStarValue()==1?R.string.notify_star_flag_up:R.string.notify_star_flag_down);
        }
    }
}
