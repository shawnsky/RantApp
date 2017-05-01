package com.xt.android.rant.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xt.android.rant.R;
import com.xt.android.rant.adapter.NewAdapter;
import com.xt.android.rant.utils.SpaceItemDecoration;
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
public class NewFragment extends Fragment {

    private static final int MSG_GET_RANT_LIST = 0;
    private static final String TAG = "NewFragment";
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    private String mJson;
    private OkHttpClient mClient;
    private Handler mHandler;



    public NewFragment() {
        // Required empty public constructor
    }


    public static NewFragment newInstance(String param1) {
        NewFragment fragment = new NewFragment();
        Bundle args = new Bundle();
        args.putString("args1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new, container, false);


        mToolbar = (Toolbar) view.findViewById(R.id.fragment_new_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        mProgressBar = (ProgressBar)view.findViewById(R.id.fragment_new_progress_bar);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.fragment_new_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(1));
        mRecyclerView.setAdapter(new NewAdapter(new ArrayList<RantItem>()));

        getData();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MSG_GET_RANT_LIST:
                        convertJson2UI(mJson);
                        mProgressBar.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        };





        return view;
    }


    private void getData(){
        mClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/allRants.action")
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
                mHandler.sendEmptyMessage(MSG_GET_RANT_LIST);

            }
        });

    }

    private void convertJson2UI(String body){
        Gson gson = new Gson();
        Log.i(TAG, "convertJson2UI: "+body);
        List<RantItem> rantItemList = gson.fromJson(body, new TypeToken<List<RantItem>>(){}.getType());
        if (rantItemList==null) Log.i(TAG, "convertJson2UI: NULLLLLLLLLL");
        Log.i(TAG, "convertsize"+rantItemList.size());
        NewAdapter adapter = new NewAdapter(rantItemList);
        mRecyclerView.setAdapter(adapter);
    }





}
