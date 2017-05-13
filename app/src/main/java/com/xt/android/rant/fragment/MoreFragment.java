package com.xt.android.rant.fragment;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xt.android.rant.LauncherActivity;
import com.xt.android.rant.MainActivity;
import com.xt.android.rant.R;
import com.xt.android.rant.wrapper.CmtNotifyItem;
import com.xt.android.rant.wrapper.RantItem;
import com.xt.android.rant.wrapper.StarNotifyItem;

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

    private Toolbar mToolbar;
    private Button mLogoutButton;
    private OkHttpClient mClient;
    private Button mDbButton;


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
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        activity.setSupportActionBar(mToolbar);

        mLogoutButton = (Button) view.findViewById(R.id.fragment_more_btn_logout);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        mDbButton = (Button) view.findViewById(R.id.fragment_more_btn_db);
        mDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSupport.deleteAll(StarNotifyItem.class);
                DataSupport.deleteAll(CmtNotifyItem.class);
                DataSupport.deleteAll(RantItem.class);
                //Connector.getDatabase();
            }
        });


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


    private void toLogin(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("token", Activity.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        Intent i = new Intent(getActivity(), LauncherActivity.class);
        startActivity(i);
    }

}
