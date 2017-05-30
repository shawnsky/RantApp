package com.xt.android.rant.service;

import android.app.Activity;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.xt.android.rant.MainActivity;
import com.xt.android.rant.R;
import com.xt.android.rant.RantActivity;
import com.xt.android.rant.utils.TokenUtil;
import com.xt.android.rant.wrapper.CmtNotifyItem;
import com.xt.android.rant.wrapper.StarNotifyItem;


import org.litepal.LitePalApplication;
import org.litepal.crud.DataSupport;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PullService extends Service {
    private static final String TAG = "PullService";
    private NotificationManager manager;
    private MessageThread messageThread;

    private String ip = LitePalApplication.getContext().getResources().getString(R.string.ip_server);

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
        Log.i(TAG, "PullService onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "PullService onStartCommand");
        manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        messageThread = new MessageThread();
        messageThread.isRunning = true;
        messageThread.start();
        
        return super.onStartCommand(intent, flags, startId);
    }

    class MessageThread extends Thread {
        boolean  isRunning;
        Gson gson = new Gson();

        public void run() {
            while (isRunning) {
                try {
                    Log.i(TAG, "PullService running...");
                    Thread.sleep(1000*5);//5s

                    try{
                        Response cmt = download(ip+"api/getCmtNotify.action?token="+TokenUtil.getToken(LitePalApplication.getContext()));
                        Response star = download(ip+"api/getStarNotify.action?token="+TokenUtil.getToken(LitePalApplication.getContext()));
                        String cmtJson = cmt.body().string();
                        String starJson = star.body().string();

                        //如果服务器错误
                        if(cmt.code()==500 || cmt.code()==500){
                            continue;
                        }

                        //用户登出，调用stopService，虽然onDestroy，但是线程在停止之前任务可能没有结束，而此时token是""所以会出现解析错误
                        SharedPreferences sharedPreferences = LitePalApplication.getContext().getSharedPreferences("token", Activity.MODE_PRIVATE);
                        String token = sharedPreferences.getString("token","");
                        if(token.equals("")){
                            Log.i(TAG, "json parse failed!");
                            continue;
                        }




                        List<CmtNotifyItem> cmtNotifyItems = gson.fromJson(cmtJson, new TypeToken<List<CmtNotifyItem>>(){}.getType());
                        List<StarNotifyItem> starNotifyItems = gson.fromJson(starJson, new TypeToken<List<StarNotifyItem>>(){}.getType());





                        List<CmtNotifyItem> cmtNotifyItemsFromDb = DataSupport.findAll(CmtNotifyItem.class);
                        List<StarNotifyItem> starNotifyItemsFromDb = DataSupport.findAll(StarNotifyItem.class);


                        HashSet<Integer> cmtNew = new HashSet<>();
                        HashSet<Integer> starNew = new HashSet<>();
                        for(CmtNotifyItem c:cmtNotifyItems){
                            cmtNew.add(c.getCommentId());
                        }
                        for(StarNotifyItem s:starNotifyItems){
                            starNew.add(s.getStarId());
                        }

                        for(CmtNotifyItem c:cmtNotifyItemsFromDb){
                            if(cmtNew.contains(c.getCommentId())){
                                cmtNew.remove(c.getCommentId());

                            }
                        }
                        for(StarNotifyItem s:starNotifyItemsFromDb){
                            if(starNew.contains(s.getStarId())){
                                starNew.remove(s.getStarId());
                            }
                        }

                        Iterator it0 = cmtNew.iterator();
                        Iterator it1 = starNew.iterator();
                        if(it0.hasNext()){
                            int target = (Integer) it0.next();
                            cmtNew.remove(target);
                            for(CmtNotifyItem c:cmtNotifyItems){
                                if(c.getCommentId()==target){
                                    pushCmtNotify(c);
                                    DataSupport.deleteAll(CmtNotifyItem.class);
                                    DataSupport.saveAll(cmtNotifyItems);
                                }
                            }

                        }
                        if(it1.hasNext()){
                            int target = (Integer) it1.next();
                            starNew.remove(target);
                            for(StarNotifyItem s:starNotifyItems){
                                if(s.getStarId()==target){
                                    pushStarNotify(s);
                                    DataSupport.deleteAll(StarNotifyItem.class);
                                    DataSupport.saveAll(starNotifyItems);
                                }
                            }

                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }



                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Response download(String url)  {
       try {
           OkHttpClient client = new OkHttpClient();
           Request request = new Request.Builder()
                   .url(url)
                   .build();
           Response response = client.newCall(request).execute();
           return response;
       }
       catch (Exception e){
           e.printStackTrace();
       }
        return null;
    }

    private void pushCmtNotify(CmtNotifyItem cmtNotifyItem){
        Intent mainIntent = RantActivity.newIntent(LitePalApplication.getContext(), cmtNotifyItem.getRantId());
        PendingIntent pi = PendingIntent.getActivity(this, 0, mainIntent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Rant社区")
                .setContentText(cmtNotifyItem.getUserName()+"回复你说: "+cmtNotifyItem.getCommentContent())
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_user)
                .setLargeIcon(BitmapFactory.decodeResource(LitePalApplication.getContext().getResources(),R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .build();
        manager.notify(0,notification);
    }

    private void pushStarNotify(StarNotifyItem starNotifyItem){
        Intent mainIntent = RantActivity.newIntent(LitePalApplication.getContext(), starNotifyItem.getRantId());
        PendingIntent pi = PendingIntent.getActivity(this, 0, mainIntent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Rant社区")
                .setContentText(starNotifyItem.getStarValue()==1?starNotifyItem.getUserName()+"赞了你，点击这里查看":starNotifyItem.getUserName()+"朝你扔了鸡蛋，点击这里查看")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_user)
                .setLargeIcon(BitmapFactory.decodeResource(LitePalApplication.getContext().getResources(),R.mipmap.ic_launcher))
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
        Log.i(TAG, "PullService Destroy");
    }
}
