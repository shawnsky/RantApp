package com.xt.android.rant;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.xt.android.rant.adapter.CommentAdapter;
import com.xt.android.rant.utils.SpaceItemDecoration;
import com.xt.android.rant.utils.TokenUtil;
import com.xt.android.rant.wrapper.CommentItem;
import com.xt.android.rant.wrapper.DetailItem;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RantActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{
    private static final String BUNDLE_RANT_ID = "bundle_rant_id";

    private static final int MSG_SUCCESS = 1;
    private static final String TAG = "RantActivity";

    private ImageView mAvatarImageView;
    private TextView mNameTextView;
    private CheckBox mUpCheckbox;
    private CheckBox mDownCheckbox;
    private TextView mValueTextView;
    private TextView mDateTextView;
    private TextView mContentTextView;
    private RecyclerView mCommentsRecyclerView;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private LinearLayout mEmptyView;
    private RelativeLayout mUserInfoLayout;
    private ImageView mWeChatShareButton;
    private ImageView mQuanShareButton;
    private ImageView mShareButton;
    private Button mPostButton;

    private int rantId;
    private OkHttpClient mClient;
    private Handler mHandler;
    private String mJson;
    private DetailItem mDetailItem = new DetailItem();

    public static Intent newIntent(Context context, int rantId){
        Intent intent = new Intent(context, RantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_RANT_ID, rantId);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

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
        mUpCheckbox = (CheckBox) findViewById(R.id.activity_rant_checkbox_up);
        mDownCheckbox = (CheckBox)findViewById(R.id.activity_rant_checkbox_down);
        mValueTextView = (TextView)findViewById(R.id.activity_rant_tv_value);
        mDateTextView = (TextView)findViewById(R.id.activity_rant_tv_date);
        mContentTextView = (TextView)findViewById(R.id.activity_rant_tv_content);
        mCommentsRecyclerView = (RecyclerView)findViewById(R.id.activity_rant_recycler_view);
        mCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCommentsRecyclerView.addItemDecoration(new SpaceItemDecoration(1));
        mCommentsRecyclerView.setAdapter(new CommentAdapter(new ArrayList<CommentItem>()));//空列表
        mProgressBar = (ProgressBar)findViewById(R.id.activity_rant_progress_bar);
        mEmptyView = (LinearLayout)findViewById(R.id.activity_rant_empty_view);
        mUserInfoLayout = (RelativeLayout) findViewById(R.id.activity_rant_user_into_rl_clickable);
        mWeChatShareButton = (ImageView) findViewById(R.id.activity_rant_share_wechat);
        mQuanShareButton = (ImageView) findViewById(R.id.activity_rant_share_quan);
        mShareButton = (ImageView) findViewById(R.id.activity_rant_share);
        mPostButton = (Button) findViewById(R.id.activity_rant_btn_submit);


        mUserInfoLayout.setOnClickListener(this);
        mWeChatShareButton.setOnClickListener(this);
        mQuanShareButton.setOnClickListener(this);
        mShareButton.setOnClickListener(this);
        mPostButton.setOnClickListener(this);


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

        rantId = getIntent().getExtras().getInt(BUNDLE_RANT_ID, 0);
        getData();

    }



    private void getData(){
        mClient = new OkHttpClient();
        String ip = getResources().getString(R.string.ip_server);
        Request request = new Request.Builder()
                .url(ip+"api/rant.action?rantId="+rantId+"&token="+ TokenUtil.getToken(this))
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
        Log.i(TAG, "convertJson2UI: "+mJson);
        Gson gson = new Gson();
        mDetailItem = gson.fromJson(mJson, DetailItem.class);

        Picasso.with(this).load(mDetailItem.getRantAvatar()).into(mAvatarImageView);
        if(mDetailItem.getRantHidden()==1){//匿名
            mNameTextView.setText(R.string.rant_hidden_user_name);
        }
        else mNameTextView.setText(mDetailItem.getUserName());
        mValueTextView.setText(String.valueOf(mDetailItem.getRantValue()));
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd hh:mm");
        mDateTextView.setText(sdf.format(mDetailItem.getRantDate()));
        mContentTextView.setText(mDetailItem.getRantContent());

        if(mDetailItem.getThumbValue()==1){
            mUpCheckbox.setChecked(true);
            mDownCheckbox.setChecked(false);
        }
        else if(mDetailItem.getThumbValue()==-1){
            mUpCheckbox.setChecked(false);
            mDownCheckbox.setChecked(true);
        }
        else{
            mUpCheckbox.setChecked(false);
            mDownCheckbox.setChecked(false);
        }
        //在这里注册是避免上面的setChecked也被监听处理
        mUpCheckbox.setOnCheckedChangeListener(this);
        mDownCheckbox.setOnCheckedChangeListener(this);


        List<CommentItem> commentItems = mDetailItem.getCommentList();
        if(commentItems==null||commentItems.size()==0){
            mEmptyView.setVisibility(View.VISIBLE);
        }
        else{
            mCommentsRecyclerView.setAdapter(new CommentAdapter(commentItems));
        }

        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.activity_rant_user_into_rl_clickable:
                break;
            case R.id.activity_rant_btn_submit:
                Intent intent = CommentActivity.newIntent(this, rantId);
                startActivity(intent);
                break;
            case R.id.activity_rant_share_wechat:
                break;
            case R.id.activity_rant_share_quan:
                break;
            case R.id.activity_rant_share:
                Intent i=new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT,mDetailItem.getRantContent());
                String server = getResources().getString(R.string.ip_server);
                i.putExtra(Intent.EXTRA_SUBJECT,server+"rant/rant.action?rantId="+mDetailItem.getRantId());
                i=Intent.createChooser(i,getString(R.string.send_report));
                startActivity(i);
                break;

        }
    }

    private void postThumb(int flag, int rantId){
        OkHttpClient client = new OkHttpClient();
        String ip = getResources().getString(R.string.ip_server);
        RequestBody formBody = new FormBody.Builder()
                .add("token", TokenUtil.getToken(this))
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

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch(compoundButton.getId()){
            case R.id.activity_rant_checkbox_up:
                mDownCheckbox.setClickable(false);
                mUpCheckbox.setClickable(true);
                postThumb(1, mDetailItem.getRantId());
                break;
            case R.id.activity_rant_checkbox_down:
                mDownCheckbox.setClickable(true);
                mUpCheckbox.setClickable(false);
                postThumb(-1, mDetailItem.getRantId());
                break;
        }

    }


}
