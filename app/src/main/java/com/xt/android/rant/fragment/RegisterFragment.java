package com.xt.android.rant.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xt.android.rant.MainActivity;
import com.xt.android.rant.R;
import com.xt.android.rant.utils.HashUtil;

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
public class RegisterFragment extends Fragment {
    private static final int MSG_ERROR_NETWORK = 0;
    private static final int MSG_ERROR_EXIST = 2;
    private static final int MSG_ERROR_HTTP = 3;

    private static final String TAG = "RegisterFragment";
    private OkHttpClient mClient;
    private TextView mDiffTextView;
    private TextView mNullTextView;
    private EditText mUsernameEditText;
    private EditText mPwdEditText;
    private EditText mPwdConfirmEditText;
    private Button mSubmitButton;
    private ProgressDialog mProgressDialog;
    private Handler mHandler;



    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        mUsernameEditText = (EditText) view.findViewById(R.id.fragment_register_et_username);
        mPwdEditText = (EditText) view.findViewById(R.id.fragment_register_et_password);
        mPwdConfirmEditText = (EditText)view.findViewById(R.id.fragment_register_et_password_confirm);
        mSubmitButton = (Button)view.findViewById(R.id.fragment_register_btn_submit);
        mDiffTextView = (TextView)view.findViewById(R.id.fragment_register_tv_diff);
        mNullTextView = (TextView)view.findViewById(R.id.fragment_register_tv_null);
        //进度框
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(R.string.wait_title);
        mProgressDialog.setMessage("正在处理...");
        mProgressDialog.setCancelable(false);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MSG_ERROR_EXIST:
                        mProgressDialog.dismiss();
                        Toast.makeText(getActivity(),R.string.register_username_exist,Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_ERROR_HTTP:
                        mProgressDialog.dismiss();
                        Toast.makeText(getActivity(),R.string.http_error,Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_ERROR_NETWORK:
                        mProgressDialog.dismiss();
                        Toast.makeText(getActivity(),R.string.network_error,Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        };




        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsernameEditText.getText().toString();
                String password = mPwdEditText.getText().toString();
                String passwordConfirm = mPwdConfirmEditText.getText().toString();
                //存在未填项
                if(username.equals("")||password.equals("")||passwordConfirm.equals("")){
                    mNullTextView.setVisibility(View.VISIBLE);
                }
                //两次输入密码不一致
                else if(!password.equals(passwordConfirm)){
                    mDiffTextView.setVisibility(View.VISIBLE);
                }
                else{
                    mProgressDialog.show();
                    register(username,password);
                }

            }
        });
        return view;
    }

    private void register(String username, String password){
        String android_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceMd5 = HashUtil.md5(android_id);

        mClient = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("username",username)
                .add("password",password)
                .add("device",deviceMd5)
                .build();
        Request request = new Request.Builder()
                .url("http://120.24.92.198:8080/rant/api/reg.action")
                .post(formBody)
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(MSG_ERROR_NETWORK);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                //遇到http错误
                if(body.contains("<html>")){
                    mHandler.sendEmptyMessage(MSG_ERROR_HTTP);
                }
                //用户名已存在
                else if(body.equals("exist")){
                    mHandler.sendEmptyMessage(MSG_ERROR_EXIST);
                }
                else{
                    mProgressDialog.dismiss();
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("token", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", body);
                    editor.apply();  // 将返回的 token 保存到本地的 SharedPreference 里
                    Intent i = new Intent(getActivity(), MainActivity.class);
                    startActivity(i);
                }
            }
        });
    }



}
