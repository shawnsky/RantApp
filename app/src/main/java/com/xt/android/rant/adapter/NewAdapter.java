package com.xt.android.rant.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xt.android.rant.MainActivity;
import com.xt.android.rant.R;
import com.xt.android.rant.RantActivity;
import com.xt.android.rant.utils.RelativeDateFormat;
import com.xt.android.rant.utils.TokenUtil;
import com.xt.android.rant.wrapper.RantItem;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by admin on 2017/5/1.
 */

public class NewAdapter extends RecyclerView.Adapter<NewAdapter.ViewHolder> {

    private List<RantItem> mRantItems;

    public NewAdapter(List<RantItem> rantItems){
        mRantItems = rantItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rantitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RantItem rantItem = mRantItems.get(position);
        holder.bindRantItem(rantItem);
    }

    @Override
    public int getItemCount() {
        return mRantItems.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
        RantItem rantItem;
        CheckBox upCheckbox,downCheckbox;
        TextView valueTextView, contentTextView, nameTextView, commentNumTextView, dateTextView;
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            upCheckbox = (CheckBox) itemView.findViewById(R.id.item_rant_item_checkbox_up);
            downCheckbox = (CheckBox) itemView.findViewById(R.id.item_rant_item_checkbox_down);

            valueTextView = (TextView) itemView.findViewById(R.id.item_rant_item_tv_value);
            contentTextView = (TextView)itemView.findViewById(R.id.item_rant_item_tv_content);
            nameTextView = (TextView)itemView.findViewById(R.id.item_rant_item_tv_name);
            commentNumTextView = (TextView)itemView.findViewById(R.id.item_rant_item_tv_comment_num);
            dateTextView = (TextView)itemView.findViewById(R.id.item_rant_item_tv_date);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.item_rant_item_ll_clickable);

            linearLayout.setOnClickListener(this);
            upCheckbox.setOnCheckedChangeListener(this);
            downCheckbox.setOnCheckedChangeListener(this);



        }
        public void bindRantItem(RantItem rantItem){
            this.rantItem = rantItem;
            valueTextView.setText(String.valueOf(rantItem.getRantValue()));
            contentTextView.setText(rantItem.getRantContent());
            if(rantItem.getRantHidden()==1) nameTextView.setText("神秘人");
            else nameTextView.setText(rantItem.getUserName());
            commentNumTextView.setText(String.valueOf(rantItem.getCommentsNum()));
            dateTextView.setText(RelativeDateFormat.format(rantItem.getRantDate()));



            if(rantItem.getThumbFlag()==1){
                upCheckbox.setChecked(true);
                downCheckbox.setChecked(false);

            }
            else if(rantItem.getThumbFlag()==-1){
                upCheckbox.setChecked(false);
                downCheckbox.setChecked(true);
            }
            else{
                upCheckbox.setChecked(false);
                downCheckbox.setChecked(false);
            }
        }


        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.item_rant_item_ll_clickable:
                    Intent intent = RantActivity.newIntent(MainActivity.sMainActivity, rantItem.getRantId());
                    MainActivity.sMainActivity.startActivity(intent);
                    break;

            }

        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            switch (compoundButton.getId()){
                case R.id.item_rant_item_checkbox_up:
                    downCheckbox.setClickable(false);
                    upCheckbox.setClickable(true);
                    post(1, rantItem.getRantId());
                    break;
                case R.id.item_rant_item_checkbox_down:
                    downCheckbox.setClickable(true);
                    upCheckbox.setClickable(false);
                    post(-1, rantItem.getRantId());
                    break;

            }
        }
    }

    private static void post(int flag, int rantId){
        OkHttpClient client = new OkHttpClient();
        String ip = MainActivity.sMainActivity.getResources().getString(R.string.ip_server);
        RequestBody formBody = new FormBody.Builder()
                .add("token", TokenUtil.getToken(MainActivity.sMainActivity))
                .add("rantId",String.valueOf(rantId))
                .build();
        Request request;
        if(flag==1) {
            request = new Request.Builder()
                    .url(ip + "api/thumbsUp.action")
                    .post(formBody)
                    .build();
        }
        else{
            request = new Request.Builder()
                    .url(ip + "api/thumbsDown.action")
                    .post(formBody)
                    .build();
        }
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
