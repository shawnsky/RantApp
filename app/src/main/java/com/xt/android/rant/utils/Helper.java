package com.xt.android.rant.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

/**
 * Created by Hawking on 2015/11/7.
 */
public class Helper {

    private volatile static Helper helper = null;

    public String md5(String string){
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuffer hex = new StringBuffer();

        for (int i = 0; i < hash.length; i++)  {
            if (Integer.toHexString(0xFF & hash[i]).length() == 1)
                hex.append("0").append(Integer.toHexString(0xFF & hash[i]));
            else
                hex.append(Integer.toHexString(0xFF & hash[i]));
        }

        return hex.toString();
    }

    public static Helper getInstance(){
        if( helper == null )
            synchronized(Helper.class){
                if( helper == null )
                    helper = new Helper();
            }

        return helper;
    }

    public long getUnixTime(){
        return Calendar.getInstance().getTimeInMillis() / 1000;
    }

    public int getWidth( Context mContext ){
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public int getHeight( Context mContext ){
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public float getDensity( Context mContext ){
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        return dm.density;
    }

    public void toast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public boolean getNetWorkStatus( Context context ){
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if( mNetworkInfo != null )
            return mNetworkInfo.isAvailable();

        return false;
    }

    public int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return degree;
    }

    public Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    public String saveMyBitmap(Bitmap mBitmap) {
        String path = FileManagerUtils.getAppPath("reactor") + "/" + System.currentTimeMillis() + ".jpg";
        File f = new File(path);
        try {
            f.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return path;
    }
}
