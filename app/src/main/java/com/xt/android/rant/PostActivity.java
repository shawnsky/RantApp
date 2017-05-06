package com.xt.android.rant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostActivity extends AppCompatActivity {
    private static final int MSG_SUCCESS = 1;
    private static final int MSG_FAILED = 0;

    private Switch mSwitch;
    private EditText mEditText;
    private Button mButton;
    private TextView mIsHidden;
    private TextView mHiddenInfo;
    private ProgressDialog mProgressDialog;
    private Toolbar mToolbar;


    private OkHttpClient mClient;
    private int isHidden;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mSwitch = (Switch) findViewById(R.id.activity_post_switch);
        mIsHidden = (TextView) findViewById(R.id.activity_post_tv_is_hidden);
        mHiddenInfo = (TextView) findViewById(R.id.activity_post_tv_hidden_info);
        mEditText = (EditText) findViewById(R.id.activity_post_et_content);
        mButton = (Button) findViewById(R.id.activity_post_btn_submit);
        mToolbar = (Toolbar) findViewById(R.id.activity_post_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.ic_action_close);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(R.string.wait_title);
        mProgressDialog.setMessage("正在处理...");
        mProgressDialog.setCancelable(false);

        init();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MSG_SUCCESS:
                        mProgressDialog.dismiss();
                        onBackPressed();
                        break;
                    case MSG_FAILED:
                        mProgressDialog.dismiss();
                        Snackbar.make(mButton, "发送失败", Snackbar.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog.show();
                post();


            }
        });

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    mIsHidden.setText("匿名");
                    mHiddenInfo.setVisibility(View.VISIBLE);
                    isHidden = 1;
                }
                else{
                    mIsHidden.setText("公开");
                    mHiddenInfo.setVisibility(View.INVISIBLE);
                    isHidden = 0;
                }
            }
        });



    }

    private void post(){
        mClient = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("token",getToken())
                .add("content",mEditText.getText().toString())
                .add("isHidden",String.valueOf(isHidden))
                .build();
        Request request = new Request.Builder()
//                .url("http://120.24.92.198:8080/rant/api/postrant.action")
                .url("http://10.0.2.2:8080/api/postrant.action")
                .post(formBody)
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // TODO: 2017/5/7
                mHandler.sendEmptyMessage(MSG_FAILED);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // TODO: 2017/5/7
                String body = response.body().string();
                if(body.equals("0")) mHandler.sendEmptyMessage(MSG_FAILED);
                else mHandler.sendEmptyMessage(MSG_SUCCESS);
            }
        });
    }

    private void init(){
        isHidden = 0;
        mIsHidden.setText("公开");
        mHiddenInfo.setVisibility(View.INVISIBLE);
    }

    private String getToken(){
        SharedPreferences sharedPreferences = getSharedPreferences("token", Activity.MODE_PRIVATE);
        return sharedPreferences.getString("token","");
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
}
