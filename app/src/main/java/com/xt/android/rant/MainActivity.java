package com.xt.android.rant;

import android.app.Activity;
import android.content.Intent;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.xt.android.rant.fragment.HotFragment;
import com.xt.android.rant.fragment.MoreFragment;
import com.xt.android.rant.fragment.NewFragment;
import com.xt.android.rant.fragment.NotifyFragment;
import com.xt.android.rant.utils.TokenUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener{
    private static final String TAG = "MainActivity";
    private static final int MSG_GET_NOTIFY_DATA = 1;
    private HotFragment mHotFragment;
    private NewFragment mNewFragment;
    private NotifyFragment mNotifyFragment;
    private MoreFragment mMoreFragment;
    private int index = 0;
    private Handler mHandler;

    public static MainActivity sMainActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sMainActivity = this;

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case MSG_GET_NOTIFY_DATA:
                        break;
                }
            }
        };
        BottomNavigationBar mBar  = (BottomNavigationBar) findViewById(R.id.activity_main_navigation_bar);
        mBar.setMode(BottomNavigationBar.MODE_FIXED);
        mBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);


        mBar.addItem(new BottomNavigationItem(R.drawable.ic_bar_new).setActiveColorResource(R.color.colorPrimary))
                .addItem(new BottomNavigationItem(R.drawable.ic_bar_hot).setActiveColorResource(R.color.colorPrimary))
                .addItem(new BottomNavigationItem(R.drawable.ic_bar_noti).setActiveColorResource(R.color.colorPrimary)
                        .setBadgeItem(new BadgeItem().setBackgroundColor(getResources().getColor(R.color.colorAccentLight)).setText("99").setHideOnSelect(true)))
                .addItem(new BottomNavigationItem(R.drawable.ic_bar_more).setActiveColorResource(R.color.colorPrimary))
                .setFirstSelectedPosition(index)
                .initialise();
        setDefaultFragment();
        mBar.setTabSelectedListener(this);
    }


    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mNewFragment = NewFragment.newInstance("最新");
        transaction.replace(R.id.activity_main_container, mNewFragment);
        transaction.commit();
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }


    @Override
    public void onTabSelected(int position) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch(position){
            case 0:
                if(mNewFragment==null){
                    mNewFragment = NewFragment.newInstance("最新");
                }

                transaction.replace(R.id.activity_main_container,mNewFragment);
                break;
            case 1:
                if(mHotFragment==null){
                    mHotFragment = HotFragment.newInstance("最热");
                }
                transaction.replace(R.id.activity_main_container,mHotFragment);
                break;
            case 2:
                if(mNotifyFragment==null){
                    mNotifyFragment = NotifyFragment.newInstance("通知");
                }
                transaction.replace(R.id.activity_main_container,mNotifyFragment);

                break;
            case 3:
                if(mMoreFragment==null){
                    mMoreFragment = MoreFragment.newInstance("更多");
                }
                transaction.replace(R.id.activity_main_container,mMoreFragment);
                break;
            default:
                break;
        }
        transaction.commit();
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    //启动获取notifies
    private void getNotifies(){
        String ip = getResources().getString(R.string.ip_server);
        OkHttpClient mClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ip+"api/getCmtNotify.action?token="+ TokenUtil.getToken(this))
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonBody = response.body().string();
                //mJson = jsonBody;
                mHandler.sendEmptyMessage(MSG_GET_NOTIFY_DATA);

            }
        });

    }
}
