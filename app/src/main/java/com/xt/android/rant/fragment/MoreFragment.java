package com.xt.android.rant.fragment;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.xt.android.rant.LauncherActivity;
import com.xt.android.rant.MainActivity;
import com.xt.android.rant.R;
import com.xt.android.rant.utils.TokenUtil;
import com.xt.android.rant.wrapper.CmtNotifyItem;
import com.xt.android.rant.wrapper.RantItem;
import com.xt.android.rant.wrapper.StarNotifyItem;
import com.xt.android.rant.wrapper.User;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends Fragment {
    private static final int MSG_SUCCESS = 1;
    private static final int MSG_FAILED = 0;


    private Toolbar mToolbar;
    private OkHttpClient mClient;
    private ImageView avatar;
    private TextView nameTextView;
    private Handler mHandler;
    private String json;


    public static MoreFragment newInstance(String param1) {
        MoreFragment fragment = new MoreFragment();
        Bundle args = new Bundle();
        args.putString("args1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    public MoreFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_more, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.fragment_more_toolbar);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        avatar = (ImageView)view.findViewById(R.id.fragment_more_avatar);
        nameTextView = (TextView)view.findViewById(R.id.fragment_more_name);

        getData();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case MSG_SUCCESS:
                        Gson gson = new Gson();
                        User user = gson.fromJson(json, User.class);
                        Picasso.with(getActivity()).load(user.getUserAvatar()).into(avatar);
                        nameTextView.setText(user.getUserName());
                        break;
                    case MSG_FAILED:
                        Snackbar.make(avatar,R.string.network_error,Snackbar.LENGTH_SHORT).show();
                        break;
                }
            }
        };



        return view;
    }

    private void logout(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("token", Activity.MODE_PRIVATE);
        String token = sharedPreferences.getString("token","");
        if(token.equals("")){
            toLogin();
        }
        else{
            String ip = getActivity().getResources().getString(R.string.ip_server);
            mClient = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("token",token)
                    .build();
            Request request = new Request.Builder()
                    .url(ip+"api/logout.action")
                    .post(formBody)
                    .build();
            Call call = mClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    // FIXME: 2017/5/1 这里需要修改，数据库没有删除Token，影响不大，有空再改。

                    toLogin();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    // FIXME: 2017/5/1 这里需要再判断返回的确认码，影响不大，有空再改。

                    toLogin();
                }
            });

        }
    }

    private void getData(){
        String ip = getActivity().getResources().getString(R.string.ip_server);
        mClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ip+"api/userInfo.action?token="+ TokenUtil.getToken(getActivity()))
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(MSG_FAILED);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                json = response.body().string();
                mHandler.sendEmptyMessage(MSG_SUCCESS);

            }
        });

    }


    private void toLogin(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("token", Activity.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        Intent i = new Intent(getActivity(), LauncherActivity.class);
        startActivity(i);
    }

}
