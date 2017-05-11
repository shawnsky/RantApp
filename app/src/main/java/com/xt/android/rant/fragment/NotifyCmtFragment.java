package com.xt.android.rant.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xt.android.rant.R;
import com.xt.android.rant.adapter.NewAdapter;
import com.xt.android.rant.adapter.NotifyCmtAdapter;
import com.xt.android.rant.utils.SpaceItemDecoration;
import com.xt.android.rant.utils.TokenUtil;
import com.xt.android.rant.wrapper.CmtNotifyItem;
import com.xt.android.rant.wrapper.RantItem;

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
public class NotifyCmtFragment extends Fragment {

    private static final int MSG_GET_NOTIFY_LIST = 1;

    private RecyclerView mRecyclerView;
    private OkHttpClient mClient;
    private String mJson;
    private Handler mHandler;


    public NotifyCmtFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notify_cmt, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_notify_cmt_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new NotifyCmtAdapter(new ArrayList<CmtNotifyItem>()));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(5));

        getData();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MSG_GET_NOTIFY_LIST:
                        convertJson2UI(mJson);
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
                .url(ip+"api/getCmtNotify.action?token="+ TokenUtil.getToken(getActivity()))
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonBody = response.body().string();
                mJson = jsonBody;
                mHandler.sendEmptyMessage(MSG_GET_NOTIFY_LIST);

            }
        });

    }

    private void convertJson2UI(String body){
        Gson gson = new Gson();
        List<CmtNotifyItem> cmtNotifyItems = gson.fromJson(body, new TypeToken<List<CmtNotifyItem>>(){}.getType());
        NotifyCmtAdapter adapter = new NotifyCmtAdapter(cmtNotifyItems);
        mRecyclerView.setAdapter(adapter);
    }

}
