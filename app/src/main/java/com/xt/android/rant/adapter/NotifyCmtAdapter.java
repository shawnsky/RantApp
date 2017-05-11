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

import java.util.Collections;
import java.util.List;

import static com.xt.android.rant.R.id.item_notify_cmt_tv_rant_content;

/**
 * Created by admin on 2017/5/11.
 */

public class NotifyCmtAdapter extends RecyclerView.Adapter<NotifyCmtAdapter.ViewHolder> {
    private List<CmtNotifyItem> mCmtNotifyItems;

    public NotifyCmtAdapter(List<CmtNotifyItem> cmtNotifyItems){
        Collections.reverse(cmtNotifyItems);
        this.mCmtNotifyItems = cmtNotifyItems;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notify_cmt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CmtNotifyItem cmtNotifyItem = mCmtNotifyItems.get(position);
        holder.bind(cmtNotifyItem);
    }

    @Override
    public int getItemCount() {
        return mCmtNotifyItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView mName;
        private TextView mCmtContent;
        private TextView mRantContent;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.item_notify_cmt_iv_avatar);
            mName = (TextView) itemView.findViewById(R.id.item_notify_cmt_tv_name);
            mCmtContent = (TextView) itemView.findViewById(R.id.item_notify_cmt_tv_comment_content);
            mRantContent = (TextView) itemView.findViewById(item_notify_cmt_tv_rant_content);
        }
        public void bind(CmtNotifyItem cmtNotifyItem){
            Picasso.with(MainActivity.sMainActivity).load(cmtNotifyItem.getUserAvatar()).into(mImageView);
            mName.setText(cmtNotifyItem.getUserName());
            String cmtHead = MainActivity.sMainActivity.getResources().getString(R.string.notify_cmt_cmt_head);
            String rantHead = MainActivity.sMainActivity.getResources().getString(R.string.notify_cmt_rant_head);
            String t1 = cmtHead+cmtNotifyItem.getCommentContent();
            String t2 = rantHead+cmtNotifyItem.getRantContent();
            mCmtContent.setText(t1);
            mRantContent.setText(t2);

        }
    }
}
