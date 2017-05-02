package com.xt.android.rant.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xt.android.rant.MainActivity;
import com.xt.android.rant.R;
import com.xt.android.rant.RantActivity;
import com.xt.android.rant.utils.RelativeDateFormat;
import com.xt.android.rant.wrapper.RantItem;

import java.util.List;

/**
 * Created by admin on 2017/5/1.
 */

public class NewAdapter extends RecyclerView.Adapter<NewAdapter.ViewHolder> {

    private static final String EXTRA_RANT_ID = "extra_rant_id";

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


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RantItem rantItem;
        ImageView upButton,downButton;
        TextView valueTextView, contentTextView, nameTextView, commentNumTextView, dateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            upButton = (ImageView) itemView.findViewById(R.id.item_rant_item_btn_up);
            downButton = (ImageView)itemView.findViewById(R.id.item_rant_item_btn_down);
            valueTextView = (TextView) itemView.findViewById(R.id.item_rant_item_tv_value);
            contentTextView = (TextView)itemView.findViewById(R.id.item_rant_item_tv_content);
            nameTextView = (TextView)itemView.findViewById(R.id.item_rant_item_tv_name);
            commentNumTextView = (TextView)itemView.findViewById(R.id.item_rant_item_tv_comment_num);
            dateTextView = (TextView)itemView.findViewById(R.id.item_rant_item_tv_date);
            itemView.setOnClickListener(this);

        }
        public void bindRantItem(RantItem rantItem){
            this.rantItem = rantItem;
            valueTextView.setText(String.valueOf(rantItem.getRantValue()));
            contentTextView.setText(rantItem.getRantContent());
            nameTextView.setText(rantItem.getUserName());
            commentNumTextView.setText(String.valueOf(rantItem.getCommentsNum()));
            dateTextView.setText(RelativeDateFormat.format(rantItem.getRantDate()));
        }


        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.sMainActivity, RantActivity.class);
            intent.putExtra(EXTRA_RANT_ID, rantItem.getRantId());
            MainActivity.sMainActivity.startActivity(intent);
        }
    }
}
