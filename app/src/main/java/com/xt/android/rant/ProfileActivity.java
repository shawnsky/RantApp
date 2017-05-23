package com.xt.android.rant;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.xt.android.rant.adapter.NewAdapter;
import com.xt.android.rant.fragment.EditInfoFragment;
import com.xt.android.rant.fragment.SelectPhotoFragment;
import com.xt.android.rant.utils.TokenUtil;
import com.xt.android.rant.wrapper.RantItem;
import com.xt.android.rant.wrapper.UserItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MSG_GET_DATA = 1;
    private static final String ARG_EDIT_DIALOG = "arg_edit_dialog";
    private static final String ARG_PHOTO_DIALOG = "arg_photo_dialog";
    private static final String TAG = "ProfileActivity";
    private static final String EXTRA_USER_ID = "extra_user_id";
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private ImageView mAvatarImageView;
    private TextView mNameTextView;
    private TextView mBioTextView;
    private TextView mValueTextView;
    private TextView mLocationTextView;
    private CardView mEditCardView;
    private Button mEditButton;
    private int userId;
    private String mJson;
    private UserItem mUserItem;
    private OkHttpClient mClient;

    public static Intent newIntent(Context context, int userId){
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userId = getIntent().getIntExtra(EXTRA_USER_ID,0);
        mToolbar = (Toolbar) findViewById(R.id.activity_profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);

        mAvatarImageView = (ImageView) findViewById(R.id.activity_profile_avatar);
        mNameTextView = (TextView) findViewById(R.id.activity_profile_name);
        mBioTextView = (TextView) findViewById(R.id.activity_profile_bio);
        mValueTextView = (TextView) findViewById(R.id.activity_profile_value);
        mLocationTextView = (TextView) findViewById(R.id.activity_profile_location);
        mEditCardView = (CardView) findViewById(R.id.activity_profile_cv_edit);
        mEditButton = (Button) findViewById(R.id.activity_profile_button_edit);
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_profile_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new NewAdapter(new ArrayList<RantItem>()));


        getData();

    }


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_GET_DATA:
                    Gson gson = new Gson();
                    mUserItem = gson.fromJson(mJson, UserItem.class);
                    Picasso.with(ProfileActivity.this).load(mUserItem.getUserAvatar()).into(mAvatarImageView);
                    mNameTextView.setText(mUserItem.getUserName());
                    mBioTextView.setText(mUserItem.getUserBio());
                    mValueTextView.setText(String.valueOf(mUserItem.getUserValue()));
                    mLocationTextView.setText(mUserItem.getUserLocation());
                    List<RantItem> list = mUserItem.getUserRants();
                    if(mUserItem.isMyself()){
                        mEditCardView.setVisibility(View.VISIBLE);
                        mEditButton.setOnClickListener(ProfileActivity.this);
                        mAvatarImageView.setOnClickListener(ProfileActivity.this);
                    }
                    //展示柜移除匿名内容
                    Iterator<RantItem> iterator = list.iterator();
                    while (iterator.hasNext()){
                        RantItem item = iterator.next();
                        if(item.getRantHidden()==1){
                            list.remove(item);
                        }
                    }
                    Collections.reverse(list);
                    mRecyclerView.setAdapter(new NewAdapter(list));
                    break;
            }
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        FragmentManager fm = getSupportFragmentManager();
        switch (view.getId()){
            case R.id.activity_profile_button_edit:
                EditInfoFragment DialogEdit = new EditInfoFragment();
                DialogEdit.setInfo(mUserItem.getUserId(), mUserItem.getUserBio(), mUserItem.getUserLocation());
                DialogEdit.show(fm,ARG_EDIT_DIALOG);
                break;
            case R.id.activity_profile_avatar:
                SelectPhotoFragment DialogSelect = new SelectPhotoFragment();
                DialogSelect.setInfo(mUserItem.getUserId());
                DialogSelect.show(fm,ARG_PHOTO_DIALOG);
                break;
        }

    }

    private void getData(){
        String ip = getResources().getString(R.string.ip_server);
        mClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ip+"api/userProfile.action?userId="+userId+"&token="+TokenUtil.getToken(this))
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mJson = response.body().string();
                mHandler.sendEmptyMessage(MSG_GET_DATA);

            }
        });

    }


}
