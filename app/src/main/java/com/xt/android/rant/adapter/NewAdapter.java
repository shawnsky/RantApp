package com.xt.android.rant.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xt.android.rant.R;
import com.xt.android.rant.wrapper.RantItem;

import java.util.List;

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
        holder.valueTextView.setText(String.valueOf(rantItem.getRantValue()));
        holder.contentTextView.setText(rantItem.getRantContent());
        holder.nameTextView.setText(rantItem.getUserName());
        holder.commentNumTextView.setText(String.valueOf(rantItem.getCommentsNum()));

    }

    @Override
    public int getItemCount() {
        return mRantItems.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView upButton,downButton;
        TextView valueTextView, contentTextView, nameTextView, commentNumTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            upButton = (ImageView) itemView.findViewById(R.id.item_rant_item_btn_up);
            downButton = (ImageView)itemView.findViewById(R.id.item_rant_item_btn_down);
            valueTextView = (TextView) itemView.findViewById(R.id.item_rant_item_tv_value);
            contentTextView = (TextView)itemView.findViewById(R.id.item_rant_item_tv_content);
            nameTextView = (TextView)itemView.findViewById(R.id.item_rant_item_tv_name);
            commentNumTextView = (TextView)itemView.findViewById(R.id.item_rant_item_tv_comment_num);

        }
    }
}
