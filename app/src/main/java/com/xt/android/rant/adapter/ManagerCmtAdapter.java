package com.xt.android.rant.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xt.android.rant.MainActivity;
import com.xt.android.rant.R;
import com.xt.android.rant.RantActivity;
import com.xt.android.rant.utils.TokenUtil;
import com.xt.android.rant.wrapper.CmtNotifyItem;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.xt.android.rant.R.id.item_notify_cmt_tv_rant_content;

/**
 * Created by admin on 2017/5/11.
 */

public class ManagerCmtAdapter extends RecyclerView.Adapter<ManagerCmtAdapter.ViewHolder> {
    private static final String TAG = "NotifyCmtAdapter";
    private List<CmtNotifyItem> mCmtNotifyItems;

    public ManagerCmtAdapter(List<CmtNotifyItem> cmtNotifyItems){
        Collections.reverse(cmtNotifyItems);
        this.mCmtNotifyItems = cmtNotifyItems;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager_cmtitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CmtNotifyItem cmtNotifyItem = mCmtNotifyItems.get(position);
        holder.bind(cmtNotifyItem);
        holder.mActionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post(cmtNotifyItem.getCommentId());
                mCmtNotifyItems.remove(cmtNotifyItem);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mCmtNotifyItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageView;
        private TextView mName;
        private TextView mCmtContent;
        private TextView mRantContent;
        private LinearLayout mll;
        private TextView mActionTextView;
        private CmtNotifyItem mCmtNotifyItem;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.item_manager_cmt_iv_avatar);
            mName = (TextView) itemView.findViewById(R.id.item_manager_cmt_tv_name);
            mCmtContent = (TextView) itemView.findViewById(R.id.item_manager_cmt_tv_comment_content);
            mRantContent = (TextView) itemView.findViewById(R.id.item_manager_cmt_tv_rant_content);
            mll = (LinearLayout)itemView.findViewById(R.id.item_manager_cmt_ll);
            mActionTextView = (TextView) itemView.findViewById(R.id.item_manager_cmt_tv_action);
        }
        public void bind(final CmtNotifyItem cmtNotifyItem){
            mCmtNotifyItem = cmtNotifyItem;
            Picasso.with(MainActivity.sMainActivity).load(cmtNotifyItem.getUserAvatar()).into(mImageView);
            mName.setText(cmtNotifyItem.getUserName());
            String t1 = cmtNotifyItem.getCommentContent();
            String t2 = cmtNotifyItem.getRantContent();
            mCmtContent.setText(t1);
            mRantContent.setText(t2);
            mActionTextView.setText("删除");
            mll.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.item_manager_cmt_ll:
                    Intent i = RantActivity.newIntent(MainActivity.sMainActivity, mCmtNotifyItem.getRantId());
                    MainActivity.sMainActivity.startActivity(i);
                    String ip = MainActivity.sMainActivity.getResources().getString(R.string.ip_server);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(ip+"api/setRead.action?flag=0&id="+mCmtNotifyItem.getCommentId())
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.i(TAG, "onResponse: "+response.body().string());
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }



    private static void post(int commentId){
        OkHttpClient client = new OkHttpClient();
        String ip = MainActivity.sMainActivity.getResources().getString(R.string.ip_server);
        Request request;
        request = new Request.Builder()
                .url(ip + "api/deleteComment.action?commentId="+commentId+"&token="+ TokenUtil.getToken(MainActivity.sMainActivity))
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
