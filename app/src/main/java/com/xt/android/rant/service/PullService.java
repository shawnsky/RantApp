package com.xt.android.rant.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xt.android.rant.MainActivity;
import com.xt.android.rant.R;
import com.xt.android.rant.RantActivity;
import com.xt.android.rant.utils.TokenUtil;
import com.xt.android.rant.wrapper.CmtNotifyItem;
import com.xt.android.rant.wrapper.StarNotifyItem;


import org.litepal.crud.DataSupport;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PullService extends Service {
    private static final String TAG = "PullService";
    private NotificationManager manager;
    private MessageThread messageThread;
    String ip = MainActivity.sMainActivity.getResources().getString(R.string.ip_server);

    public PullService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        messageThread = new MessageThread();
        messageThread.isRunning = true;
        messageThread.start();
        
        return super.onStartCommand(intent, flags, startId);
    }

    class MessageThread extends Thread {

        public boolean isRunning = true;

        public void run() {
            while (isRunning) {
                try {
                    // 间隔时间半小时
//                    Thread.sleep(1000*60*30);
                    Thread.sleep(1000*1);//1s
                    // 获取服务器消息
                    String cmt = download(ip+"api/getCmtNotifyCnt.action?token="+ TokenUtil.getToken(MainActivity.sMainActivity));
                    String star = download(ip+"api/getStarNotifyCnt.action?token="+ TokenUtil.getToken(MainActivity.sMainActivity));
                    // 获取上次消息
                    String sharedCmt = MainActivity.sMainActivity.getSharedPreferences("notify", MODE_PRIVATE).getString("cmt", "");
                    String sharedStar = MainActivity.sMainActivity.getSharedPreferences("notify", MODE_PRIVATE).getString("star", "");

                    Log.i(TAG, "run: Pulling:cmt:"+cmt+" sharedPull="+sharedCmt);
                    Log.i(TAG, "run: Pulling:star:"+star+" sharedStar="+sharedStar);

                    //有新消息推送
                    if (!cmt.equals(sharedCmt)) {
                        String json = download(ip+"api/getCmtNotify.action?token="+TokenUtil.getToken(MainActivity.sMainActivity));
                        Gson gson = new Gson();
                        List<CmtNotifyItem> cmtNotifyItems = gson.fromJson(json, new TypeToken<List<CmtNotifyItem>>(){}.getType());
                        List<CmtNotifyItem> cmtNotifyItemsFromDb = DataSupport.findAll(CmtNotifyItem.class);
                        CmtNotifyItem target = new CmtNotifyItem();
                        for(CmtNotifyItem cmtNotifyItem:cmtNotifyItems){
                            if(!cmtNotifyItemsFromDb.contains(cmtNotifyItem)){
                                target = cmtNotifyItem;
                                break;
                            }
                        }
                        //推送一次
                        pushCmtNotify(target);
                        SharedPreferences.Editor editor = MainActivity.sMainActivity.getSharedPreferences("notify", MODE_PRIVATE).edit();
                        editor.putString("cmt", cmt);
                        editor.apply();

                    }

                    if(!star.equals(sharedStar)){
                        String json = download(ip+"api/getStarNotify.action?token="+TokenUtil.getToken(MainActivity.sMainActivity));
                        Gson gson = new Gson();
                        List<StarNotifyItem> starNotifyItems = gson.fromJson(json, new TypeToken<List<StarNotifyItem>>(){}.getType());
                        List<StarNotifyItem> starNotifyItemsFromDb = DataSupport.findAll(StarNotifyItem.class);
                        StarNotifyItem target = new StarNotifyItem();
                        for(StarNotifyItem starNotifyItem:starNotifyItems){
                            if(!starNotifyItemsFromDb.contains(starNotifyItem)){
                                target = starNotifyItem;
                                break;
                            }
                        }
                        //推送一次
                        pushStarNotify(target);
                        SharedPreferences.Editor editor = MainActivity.sMainActivity.getSharedPreferences("notify", MODE_PRIVATE).edit();
                        editor.putString("star", star);
                        editor.apply();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String download(String url)  {
       try {
           OkHttpClient client = new OkHttpClient();
           Request request = new Request.Builder()
                   .url(url)
                   .build();
           Response response = client.newCall(request).execute();
           return response.body().string();
       }
       catch (Exception e){
           e.printStackTrace();
       }
        return null;
    }

    private void pushCmtNotify(CmtNotifyItem cmtNotifyItem){
        Intent mainIntent = RantActivity.newIntent(MainActivity.sMainActivity, cmtNotifyItem.getRantId());
        PendingIntent pi = PendingIntent.getActivity(this, 0, mainIntent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Rant社区")
                .setContentText("有人回复了你的Rant，点击这里查看")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_user)
                .setLargeIcon(BitmapFactory.decodeResource(MainActivity.sMainActivity.getResources(),R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .build();
        manager.notify(0,notification);
    }

    private void pushStarNotify(StarNotifyItem starNotifyItem){
        Intent mainIntent = RantActivity.newIntent(MainActivity.sMainActivity, starNotifyItem.getRantId());
        PendingIntent pi = PendingIntent.getActivity(this, 0, mainIntent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Rant社区")
                .setContentText(starNotifyItem.getStarValue()==1?"有人赞了你，点击这里查看":"有人朝你扔了鸡蛋，点击这里查看")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_user)
                .setLargeIcon(BitmapFactory.decodeResource(MainActivity.sMainActivity.getResources(),R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .build();
        manager.notify(0,notification);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        messageThread.isRunning = false;
        Log.i(TAG, "onDestroy: ");
    }
}
