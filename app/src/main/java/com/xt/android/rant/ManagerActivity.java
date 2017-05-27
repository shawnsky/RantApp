package com.xt.android.rant;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xt.android.rant.adapter.ManagerCmtAdapter;
import com.xt.android.rant.adapter.ManagerRantAdapter;
import com.xt.android.rant.adapter.NewAdapter;
import com.xt.android.rant.utils.SpaceItemDecoration;
import com.xt.android.rant.utils.TokenUtil;
import com.xt.android.rant.wrapper.CmtNotifyItem;
import com.xt.android.rant.wrapper.RantItem;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ManagerActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "ManagerActivity";
    private static final String EXTRA_FLAG = "extra_flag";
    private static final int MSG_SUCCESS = 1;
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private String mJson;
    private Handler mHandler;
    private TextView mToolbarText;
    private int flag;
    /**
     * 注意！！！这个Activity根据不同的flag展示不同的内容
     * FLAG
     * 我发布的 1
     * 我的评论 2
     * 我赞过的 3
     * 我踩过的 4
     * 1 3 4 共用一个Adapter: ManagerRantAdapter(list, int flag)
     * 2 单独用一个Adapter: ManagerCmtAdapter(list)
     */

    public static Intent newIntent(Context context, int flag){
        Intent intent = new Intent(context, ManagerActivity.class);
        intent.putExtra(EXTRA_FLAG, flag);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        flag = getIntent().getIntExtra(EXTRA_FLAG, 0);
        mToolbar = (Toolbar) findViewById(R.id.activity_manager_toolbar);
        this.setSupportActionBar(mToolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbarText = (TextView) findViewById(R.id.activity_manager_toolbar_title);
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);

        //动态地设置标题
        switch (flag){
            case 1:
                mToolbarText.setText("我发布的");
                break;
            case 2:
                mToolbarText.setText("我的评论");
                break;
            case 3:
                mToolbarText.setText("我赞过的");
                break;
            case 4:
                mToolbarText.setText("我踩过的");
            default:
                break;
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_manager_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        mRecyclerView = (RecyclerView)findViewById(R.id.activity_manager_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        SpaceItemDecoration sd;
        if(flag==2){
            sd = new SpaceItemDecoration(5);
        }
        else{
            sd = new SpaceItemDecoration(1);
        }
        mRecyclerView.addItemDecoration(sd);
        mRecyclerView.setAdapter(new NewAdapter(new ArrayList<RantItem>()));


        getData(flag);//根据flag下载不同的数据

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MSG_SUCCESS:
                        Gson gson = new Gson();
                        if(flag!=2){//因为“我的评论”独立地使用了一个Adapter，所以此处特别处理
                            List<RantItem> rantItems = gson.fromJson(mJson, new TypeToken<List<RantItem>>(){}.getType());
                            ManagerRantAdapter adapter = new ManagerRantAdapter(rantItems, flag);
                            mRecyclerView.setAdapter(adapter);
                        }
                        else{
                            List<CmtNotifyItem> cmtNotifyItems = gson.fromJson(mJson, new TypeToken<List<CmtNotifyItem>>(){}.getType());
                            ManagerCmtAdapter adapter = new ManagerCmtAdapter(cmtNotifyItems);
                            mRecyclerView.setAdapter(adapter);
                        }

                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                }
            }
        };
    }

    private void getData(int flag){
        String ip = getResources().getString(R.string.ip_server);
        OkHttpClient mClient = new OkHttpClient();
        Request request;
        switch (flag){
            case 1://我发布的
                request = new Request.Builder()
                        .url(ip+"api/myRant.action?token="+ TokenUtil.getToken(this))
                        .build();
                break;
            case 2://我的评论
                request = new Request.Builder()
                        .url(ip+"api/myComment.action?token="+ TokenUtil.getToken(this))
                        .build();
                break;
            case 3://我赞过的
                request = new Request.Builder()
                        .url(ip+"api/myUp.action?token="+ TokenUtil.getToken(this))
                        .build();
                break;
            case 4://我踩过的
                request = new Request.Builder()
                        .url(ip+"api/myDown.action?token="+ TokenUtil.getToken(this))
                        .build();
                break;
            default:
                request = new Request.Builder()
                        .url(ip+"api/myRant.action?token="+ TokenUtil.getToken(this))
                        .build();
                break;

        }

        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mJson = response.body().string();
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

    @Override
    public void onRefresh() {
        getData(flag);
    }


}
