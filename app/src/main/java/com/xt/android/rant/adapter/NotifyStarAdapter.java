package com.xt.android.rant.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xt.android.rant.MainActivity;
import com.xt.android.rant.ProfileActivity;
import com.xt.android.rant.R;
import com.xt.android.rant.RantActivity;
import com.xt.android.rant.wrapper.StarNotifyItem;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by admin on 2017/5/11.
 */

public class NotifyStarAdapter extends RecyclerView.Adapter<NotifyStarAdapter.ViewHolder> {
    private static final String TAG = "NotifyStarAdapter";
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
        final StarNotifyItem starNotifyItem = mStarNotifyItems.get(position);
        holder.bind(starNotifyItem);
        if(starNotifyItem.getStarRead()==1){
            holder.mRl.setBackgroundColor(MainActivity.sMainActivity.getResources().getColor(R.color.garyLightLight));
        }
        holder.mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ProfileActivity.newIntent(MainActivity.sMainActivity, starNotifyItem.getUserId());
                MainActivity.sMainActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStarNotifyItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private StarNotifyItem mStarNotifyItem;
        private ImageView mImageView;
        private TextView mName;
        private TextView mFlag;
        private RelativeLayout mRl;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.item_notify_star_iv_avatar);
            mName = (TextView) itemView.findViewById(R.id.item_notify_star_tv_name);
            mFlag = (TextView) itemView.findViewById(R.id.item_notify_star_tv_flag);
            mRl = (RelativeLayout) itemView.findViewById(R.id.item_notify_star_rl);
            mRl.setOnClickListener(this);
        }
        public void bind(StarNotifyItem starNotifyItem){
            mStarNotifyItem = starNotifyItem;
            Picasso.with(MainActivity.sMainActivity).load(starNotifyItem.getUserAvatar()).into(mImageView);
            mName.setText(starNotifyItem.getUserName());
            mFlag.setText(starNotifyItem.getStarValue()==1?R.string.notify_star_flag_up:R.string.notify_star_flag_down);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.item_notify_star_rl:
                    post(mStarNotifyItem);
                    break;
            }
        }

        private void post(StarNotifyItem starNotifyItem){
            Intent i = RantActivity.newIntent(MainActivity.sMainActivity, starNotifyItem.getRantId());
            MainActivity.sMainActivity.startActivity(i);
            String ip = MainActivity.sMainActivity.getResources().getString(R.string.ip_server);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(ip+"api/setRead.action?flag=1&id="+starNotifyItem.getStarId())
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });
        }
    }






}
