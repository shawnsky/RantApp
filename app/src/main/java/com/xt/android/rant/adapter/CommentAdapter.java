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
import com.xt.android.rant.wrapper.CommentItem;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by admin on 2017/5/2.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private List<CommentItem> mCommentItemList;

    public CommentAdapter(List<CommentItem> mList){
        mCommentItemList = mList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CommentItem commentItem = mCommentItemList.get(position);
        holder.bindComment(commentItem);
    }

    @Override
    public int getItemCount() {
        return mCommentItemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CommentItem mCommentItem;
        ImageView avatarImageView;
        TextView contentTextView;
        TextView dateTextView;
        TextView nameTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            avatarImageView = (ImageView)itemView.findViewById(R.id.item_comment_iv_avatar);
            contentTextView = (TextView)itemView.findViewById(R.id.item_comment_content);
//            dateTextView = (TextView)itemView.findViewById(R.id.item_comment_tv_date);
            nameTextView = (TextView)itemView.findViewById(R.id.item_comment_tv_name);
        }
        public void bindComment(CommentItem commentItem){
            mCommentItem = commentItem;
            Picasso.with(MainActivity.sMainActivity).load(commentItem.getUserAvatar()).into(avatarImageView);

            contentTextView.setText(commentItem.getCommentContent());
            SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd hh:mm");
//            dateTextView.setText(sdf.format(commentItem.getCommentDate()));
            nameTextView.setText(commentItem.getUserName());
        }
    }
}
