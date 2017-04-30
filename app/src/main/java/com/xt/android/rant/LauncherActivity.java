package com.xt.android.rant;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;


public class LauncherActivity extends Activity{
    private static final int UPDATE_PIC_AND_INTRO = 0;
    private static final String EXTRA_BUTTON ="extra_button";
    private TextView mLogoTextView;
    private RelativeLayout mLayout;
    private TextView mIntroTextView;
    private int[] pics = {
            R.drawable.launcherbg,
            R.drawable.launcherbg1,
            R.drawable.launcherbg2,
            R.drawable.launcherbg3,
            R.drawable.launcherbg4
            };
    private int[] texts={
            R.string.launcher_intro1,
            R.string.launcher_intro2,
            R.string.launcher_intro3,
            R.string.launcher_intro4,
            R.string.launcher_intro5
            };

    private Timer mTimer;
    private TimerTask mTimerTask;
    private Handler mHandler;
    private int index;
    private Button mRegButton;
    private Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        guider();
        mLogoTextView= (TextView) findViewById(R.id.launcher_tv_logo);
        mLogoTextView.setTypeface(Typeface.createFromAsset(getAssets(),"MAGNETOB.TTF"));
        mLayout= (RelativeLayout) findViewById(R.id.activity_launcher);
        mIntroTextView= (TextView) findViewById(R.id.launcher_tv_intro);
        mRegButton = (Button)findViewById(R.id.launcher_btn_register);
        mLoginButton = (Button)findViewById(R.id.launcher_btn_login);
        mRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LauncherActivity.this, AuthActivity.class);
                i.putExtra(EXTRA_BUTTON,0);
                startActivity(i);
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LauncherActivity.this, AuthActivity.class);
                i.putExtra(EXTRA_BUTTON,1);
                startActivity(i);
            }
        });




        mTimer=new Timer();
        index = 1;
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case UPDATE_PIC_AND_INTRO:
                        mLayout.setBackground(getResources().getDrawable(pics[index%pics.length]));
                        mIntroTextView.setText(texts[index%texts.length]);
                        index++;
                        break;
                }
            }
        };



    }

    private void play(){

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(UPDATE_PIC_AND_INTRO);
                Log.i(TAG, "run: changing launcher background picture");
            }
        };

        mTimer.schedule(mTimerTask, 4000, 4000);//每4秒换一次
    }

    private void guider(){
        SharedPreferences sharedPreferences = getSharedPreferences("token", Activity.MODE_PRIVATE);
        String token = sharedPreferences.getString("token","");
        if(!token.equals("")){
            Intent i = new Intent(LauncherActivity.this, MainActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTimerTask.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        play();
    }




}
