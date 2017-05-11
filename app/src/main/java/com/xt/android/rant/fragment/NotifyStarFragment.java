package com.xt.android.rant.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xt.android.rant.R;
import com.xt.android.rant.adapter.NotifyStarAdapter;
import com.xt.android.rant.utils.SpaceItemDecoration;
import com.xt.android.rant.utils.TokenUtil;
import com.xt.android.rant.wrapper.StarNotifyItem;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotifyStarFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "NotifyStarFragment";
    private static final int MSG_GET_NOTIFY_LIST = 1;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private OkHttpClient mClient;
    private String mJson;
    private Handler mHandler;


    public NotifyStarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notify_star, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_notify_star_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new NotifyStarAdapter(new ArrayList<StarNotifyItem>()));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(5));

        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.fragment_notify_star_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));


        List<StarNotifyItem> starNotifyItemsFromDatabase = DataSupport.findAll(StarNotifyItem.class);
        if(starNotifyItemsFromDatabase==null){
            Log.i(TAG, "onCreateView: starNotifyItemsFromDatabase==null");
            getData();
        }
        else{
            Log.i(TAG, "onCreateView: load star from database");
            NotifyStarAdapter adapter = new NotifyStarAdapter(starNotifyItemsFromDatabase);
            mRecyclerView.setAdapter(adapter);
        }

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MSG_GET_NOTIFY_LIST:
                        Gson gson = new Gson();
                        List<StarNotifyItem> starNotifyItems = gson.fromJson(mJson, new TypeToken<List<StarNotifyItem>>(){}.getType());
                        NotifyStarAdapter adapter = new NotifyStarAdapter(starNotifyItems);
                        DataSupport.deleteAll(StarNotifyItem.class);
                        DataSupport.saveAll(starNotifyItems);
                        mRecyclerView.setAdapter(adapter);
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                    default:
                        break;
                }
            }
        };
        return view;
    }


    private void getData(){
        String ip = getActivity().getResources().getString(R.string.ip_server);
        mClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ip+"api/getStarNotify.action?token="+ TokenUtil.getToken(getActivity()))
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mJson = response.body().string();
                mHandler.sendEmptyMessage(MSG_GET_NOTIFY_LIST);

            }
        });

    }


    @Override
    public void onRefresh() {
        getData();
    }
}
