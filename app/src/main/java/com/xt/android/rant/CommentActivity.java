package com.xt.android.rant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.xt.android.rant.utils.TokenUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentActivity extends AppCompatActivity {
    private static final String EXTRA_RANT_ID = "extra_rant_id";
    private static final int MSG_SUCCESS = 1;
    private static final int MSG_FAILED = 0;

    private EditText mEditText;
    private Button mButton;
    private TextView mTextLimit;
    private ProgressDialog mProgressDialog;
    private Toolbar mToolbar;
    private int rantId;


    private OkHttpClient mClient;
    private Handler mHandler;

    public static Intent newIntent(Context context, int id){
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra(EXTRA_RANT_ID, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        rantId = getIntent().getIntExtra(EXTRA_RANT_ID,0);

        mEditText = (EditText) findViewById(R.id.activity_comment_et_content);
        mButton = (Button) findViewById(R.id.activity_comment_btn_submit);
        mToolbar = (Toolbar) findViewById(R.id.activity_comment_toolbar);
        mTextLimit = (TextView) findViewById(R.id.activity_comment_word_limit_info);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.ic_action_close);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(R.string.wait_title);
        mProgressDialog.setMessage("正在处理...");
        mProgressDialog.setCancelable(false);


        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MSG_SUCCESS:
                        mProgressDialog.dismiss();
                        finish();
                        break;
                    case MSG_FAILED:
                        mProgressDialog.dismiss();
                        Snackbar.make(mButton, R.string.post_error_post_failed, Snackbar.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEditText.getText().toString().equals("")){
                    mTextLimit.setText(R.string.comment_not_null);
                    mTextLimit.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }
                else{
                    mProgressDialog.show();
                    post();
                }


            }
        });






    }

    private void post(){
        mClient = new OkHttpClient();
        String ip = getResources().getString(R.string.ip_server);
        RequestBody formBody = new FormBody.Builder()
                .add("token", TokenUtil.getToken(this))
                .add("content",mEditText.getText().toString())
                .add("rantId",String.valueOf(rantId))
                .build();
        Request request = new Request.Builder()
                .url(ip+"api/sendComment.action")
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
