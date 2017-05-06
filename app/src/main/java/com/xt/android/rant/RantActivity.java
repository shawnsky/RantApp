package com.xt.android.rant;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.xt.android.rant.adapter.CommentAdapter;
import com.xt.android.rant.utils.SpaceItemDecoration;
import com.xt.android.rant.wrapper.CommentItem;
import com.xt.android.rant.wrapper.DetailItem;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RantActivity extends AppCompatActivity {
    private static final String EXTRA_RANT_ID = "extra_rant_id";

    private static final int MSG_SUCCESS = 1;

    private ImageView mAvatarImageView;
    private TextView mNameTextView;
    private ImageView mUpButton;
    private ImageView mDownButton;
    private TextView mValueTextView;
    private TextView mDateTextView;
    private TextView mContentTextView;
    private RecyclerView mCommentsRecyclerView;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private LinearLayout mEmptyView;

    private int rantId;
    private OkHttpClient mClient;
    private Handler mHandler;
    private String mJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rant);

        mToolbar = (Toolbar) findViewById(R.id.activity_rant_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);

        mAvatarImageView = (ImageView)findViewById(R.id.activity_rant_iv_avatar);
        mNameTextView = (TextView)findViewById(R.id.activity_rant_tv_name);
        mUpButton = (ImageView)findViewById(R.id.activity_rant_btn_up);
        mDownButton = (ImageView)findViewById(R.id.activity_rant_btn_down);
        mValueTextView = (TextView)findViewById(R.id.activity_rant_tv_value);
        mDateTextView = (TextView)findViewById(R.id.activity_rant_tv_date);
        mContentTextView = (TextView)findViewById(R.id.activity_rant_tv_content);
        mCommentsRecyclerView = (RecyclerView)findViewById(R.id.activity_rant_recycler_view);
        mCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCommentsRecyclerView.addItemDecoration(new SpaceItemDecoration(1));
        mCommentsRecyclerView.setAdapter(new CommentAdapter(new ArrayList<CommentItem>()));//空列表
        mProgressBar = (ProgressBar)findViewById(R.id.activity_rant_progress_bar);
        mEmptyView = (LinearLayout)findViewById(R.id.activity_rant_empty_view);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MSG_SUCCESS:
                        // TODO: 2017/5/2
                        convertJson2UI();
                        break;
                }
            }
        };

        rantId = getIntent().getIntExtra(EXTRA_RANT_ID,0);
        getData();

    }

    private void getData(){
        mClient = new OkHttpClient();
        Request request = new Request.Builder()
            //    .url("http://120.24.92.198:8080/rant/api/rant.action?rantId="+rantId)
                .url("http://10.0.2.2:8080/api/rant.action?rantId="+rantId)
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // TODO: 2017/5/2
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mJson = response.body().string();
                // FIXME: 2017/5/2 这里还要判断服务器错误的情况

                mHandler.sendEmptyMessage(MSG_SUCCESS);

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void convertJson2UI(){
        Gson gson = new Gson();
        DetailItem detailItem = gson.fromJson(mJson, DetailItem.class);

        Picasso.with(this).load(detailItem.getRantAvatar()).into(mAvatarImageView);
        if(detailItem.getRantHidden()==1){//匿名
            mNameTextView.setText("神秘人");
        }
        else mNameTextView.setText(detailItem.getUserName());
        // TODO: 2017/5/2  mUpButton
        // TODO: 2017/5/2  mDownButton
        mValueTextView.setText(String.valueOf(detailItem.getRantValue()));
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd hh:mm");
        mDateTextView.setText(sdf.format(detailItem.getRantDate()));
        mContentTextView.setText(detailItem.getRantContent());

        List<CommentItem> commentItems = detailItem.getCommentList();
        if(commentItems==null||commentItems.size()==0){
            mEmptyView.setVisibility(View.VISIBLE);
        }
        else{
            mCommentsRecyclerView.setAdapter(new CommentAdapter(commentItems));
        }

        mProgressBar.setVisibility(View.INVISIBLE);
    }
}
