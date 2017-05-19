package com.xt.android.rant.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class ManagerRantAdapter extends RecyclerView.Adapter<ManagerRantAdapter.ViewHolder> {
    /**
     * Flag参数
     * 这个Adapter是复用Adapter
     * flag=1 我发布的
     * flag=3 我赞过的
     * flag=4 我踩过的
     *
     * flag=1时 actionTextiew是删除
     * flag=3/4时 actionTextView是取消
     */

    private List<RantItem> mRantItems;
    private int flag;

    public ManagerRantAdapter(List<RantItem> rantItems, int flag){
        mRantItems = rantItems;
        this.flag = flag;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager_rantitem, parent, false);
        return new ViewHolder(view,flag);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final RantItem rantItem = mRantItems.get(position);
        holder.bindRantItem(rantItem);
        holder.actionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post(rantItem.getRantId(), flag);
                mRantItems.remove(rantItem);
                notifyDataSetChanged();
            }
        });

    }


    @Override
    public int getItemCount() {
        return mRantItems.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        RantItem rantItem;
        CheckBox upCheckbox,downCheckbox;
        TextView valueTextView, contentTextView, nameTextView, commentNumTextView, dateTextView;
        LinearLayout linearLayout;
        TextView actionTextView;

        public ViewHolder(View itemView, int flag) {
            super(itemView);
            upCheckbox = (CheckBox) itemView.findViewById(R.id.item_rant_item_checkbox_up);
            downCheckbox = (CheckBox) itemView.findViewById(R.id.item_rant_item_checkbox_down);

            valueTextView = (TextView) itemView.findViewById(R.id.item_rant_item_tv_value);
            contentTextView = (TextView)itemView.findViewById(R.id.item_rant_item_tv_content);
            nameTextView = (TextView)itemView.findViewById(R.id.item_rant_item_tv_name);
            commentNumTextView = (TextView)itemView.findViewById(R.id.item_rant_item_tv_comment_num);
            dateTextView = (TextView)itemView.findViewById(R.id.item_rant_item_tv_date);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.item_rant_item_ll_clickable);
            actionTextView = (TextView) itemView.findViewById(R.id.item_rant_item_tv_action);
            actionTextView.setText(flag==1?"删除":"取消");
            linearLayout.setOnClickListener(this);


        }
        public void bindRantItem(RantItem rantItem){
            this.rantItem = rantItem;
            valueTextView.setText(String.valueOf(rantItem.getRantValue()));
            contentTextView.setText(rantItem.getRantContent());
            if(rantItem.getRantHidden()==1) nameTextView.setText("神秘人");
            else nameTextView.setText(rantItem.getUserName());
            commentNumTextView.setText(String.valueOf(rantItem.getCommentsNum()));
            dateTextView.setText(RelativeDateFormat.format(rantItem.getRantDate()));

            upCheckbox.setClickable(false);
            downCheckbox.setClickable(false);

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
            //不能修改
//            upCheckbox.setOnCheckedChangeListener(this);
//            downCheckbox.setOnCheckedChangeListener(this);
        }


        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.item_rant_item_ll_clickable:
                    Intent intent = RantActivity.newIntent(MainActivity.sMainActivity, rantItem.getRantId());
                    MainActivity.sMainActivity.startActivity(intent);
                    break;
                default:
                    break;
            }

        }

    }

    private static void post(int rantId,int flag){
        OkHttpClient client = new OkHttpClient();
        String ip = MainActivity.sMainActivity.getResources().getString(R.string.ip_server);
        Request request;
        if(flag==1){
            request = new Request.Builder()
                    .url(ip + "api/deleteRant.action?rantId="+rantId+"&token="+TokenUtil.getToken(MainActivity.sMainActivity))
                    .build();
        }
        else{
            request = new Request.Builder()
                    .url(ip + "api/cancelThumb.action?rantId="+rantId+"&token="+TokenUtil.getToken(MainActivity.sMainActivity))
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
