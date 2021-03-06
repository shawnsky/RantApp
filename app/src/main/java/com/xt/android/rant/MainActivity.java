package com.xt.android.rant;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xt.android.rant.fragment.HotFragment;
import com.xt.android.rant.fragment.MoreFragment;
import com.xt.android.rant.fragment.NewFragment;
import com.xt.android.rant.fragment.NotifyFragment;
import com.xt.android.rant.service.PullService;
import com.xt.android.rant.utils.TokenUtil;
import com.xt.android.rant.wrapper.CmtNotifyItem;
import com.xt.android.rant.wrapper.StarNotifyItem;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener{
    private static final String TAG = "MainActivity";
    private static final int MSG_GET_DATA = 1;
    private static final int MSG_NETWORK_ERROR = 2;
    private boolean gotCMT = false;
    private boolean gotStar = false;
    private HotFragment mHotFragment;
    private NewFragment mNewFragment;
    private NotifyFragment mNotifyFragment;
    private MoreFragment mMoreFragment;
    private int index = 0;
    private Handler mHandler;
    private Timer mTimer;

    private BadgeItem notifyBadgeItem;
    private String cmtJson;
    private String starJson;

    public static MainActivity sMainActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sMainActivity = this;
/**
 * 支持Android 7.0
 * 从Android 7.0开始，一个应用提供自身文件给其它应用使用时，如果给出一个file://格式的URI的话，应用会抛出FileUriExposedException
 * 谷歌官方推荐的解决方案是使用FileProvider来生成一个content://格式的URI,由于我使用这种方法获取不到文件的filePath(技术有限)
 * 所以选择置入一个不设防的VmPolicy，下面这4行代码就解决了FileUriExposedException错误
 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        Intent intent = new Intent(MainActivity.this, PullService.class);
        startService(intent);


        TimerTask checkTask = new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG, "run: notifies have not been downloaded yet");
                if(gotCMT && gotStar){
                    mHandler.sendEmptyMessage(MSG_GET_DATA);
                }
            }
        };
        mTimer = new Timer();
        mTimer.schedule(checkTask, 100, 100);



        final BottomNavigationBar mBar  = (BottomNavigationBar) findViewById(R.id.activity_main_navigation_bar);
        mBar.setMode(BottomNavigationBar.MODE_FIXED);
        mBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        notifyBadgeItem = new BadgeItem().setBackgroundColor(getResources().getColor(R.color.colorAccentLight)).setText("99").setHideOnSelect(true);
        mBar.addItem(new BottomNavigationItem(R.drawable.ic_bar_new).setActiveColorResource(R.color.colorPrimary))
                .addItem(new BottomNavigationItem(R.drawable.ic_bar_hot).setActiveColorResource(R.color.colorPrimary))
                .addItem(new BottomNavigationItem(R.drawable.ic_bar_noti).setActiveColorResource(R.color.colorPrimary)
                        .setBadgeItem(notifyBadgeItem))
                .addItem(new BottomNavigationItem(R.drawable.ic_bar_more).setActiveColorResource(R.color.colorPrimary))
                .setFirstSelectedPosition(index)
                .initialise();
        setDefaultFragment();
        mBar.setTabSelectedListener(this);





        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case MSG_GET_DATA:
                        Log.i(TAG, "handleMessage: both cmt and star notifies have been downloaded");
                        mTimer.cancel();
                        Gson gson = new Gson();
                        List<CmtNotifyItem> cmtNotifyItems = gson.fromJson(cmtJson, new TypeToken<List<CmtNotifyItem>>(){}.getType());
                        List<StarNotifyItem> starNotifyItems = gson.fromJson(starJson, new TypeToken<List<StarNotifyItem>>(){}.getType());
                        DataSupport.deleteAll(CmtNotifyItem.class);
                        DataSupport.deleteAll(StarNotifyItem.class);
                        DataSupport.saveAll(cmtNotifyItems);
                        DataSupport.saveAll(starNotifyItems);

                        updateBar();


                        break;
                    case MSG_NETWORK_ERROR:
                        mTimer.cancel();
                        break;
                }
            }
        };

        getData();
    }

    private void updateBar() {
        List<CmtNotifyItem> cmtNoRead = DataSupport.where("commentRead = ?", "0").find(CmtNotifyItem.class);
        List<StarNotifyItem> starNoRead = DataSupport.where("starRead = ?", "0").find(StarNotifyItem.class);
        int cnt = cmtNoRead.size()+starNoRead.size();
        if(cnt!=0){
            notifyBadgeItem.setText(String.valueOf(cnt));
        }
        else{
            notifyBadgeItem.hide();
        }
    }




    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mNewFragment = NewFragment.newInstance("最新");
        transaction.replace(R.id.activity_main_container, mNewFragment);
        transaction.commit();
    }


    //拦截Back键，避免回到LauncherActivity
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
        if(position==2) {
            updateBar();
        }

    }

    @Override
    public void onTabReselected(int position) {

    }




    //启动获取通知
    private void getData(){
        String ip = getResources().getString(R.string.ip_server);
        OkHttpClient mClient = new OkHttpClient();
        Request cmtRequest = new Request.Builder()
                .url(ip+"api/getCmtNotify.action?token="+ TokenUtil.getToken(this))
                .build();
        mClient.newCall(cmtRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(MSG_NETWORK_ERROR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                cmtJson = response.body().string();
                Log.i(TAG, "onResponse: "+cmtJson);
                gotCMT = true;
            }
        });

        Request starRequest = new Request.Builder()
                .url(ip+"api/getStarNotify.action?token="+ TokenUtil.getToken(this))
                .build();
        mClient.newCall(starRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(MSG_NETWORK_ERROR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                starJson = response.body().string();
                gotStar = true;
            }
        });

    }





}
