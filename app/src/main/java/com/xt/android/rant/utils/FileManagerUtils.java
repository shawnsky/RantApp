package com.xt.android.rant.utils;

import android.os.Environment;

import java.io.File;

/**
 * 文件管理方法
 *
 * @author 等待
 * @class FileManagerUtils.java
 * @time 2014年5月16日 上午11:27:56
 * @QQ 2743569843
 */
public class FileManagerUtils {
    /**
     * 判断是否有存储卡
     */
    public static boolean isSdcard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取存储卡路径
     */
    public static String getSdcardPath() {
        if (isSdcard()) {
            return Environment.getExternalStorageDirectory().getPath() + "/";
        } else {
            return "";
        }
    }

    /**
     * 获取应用程序本地文件夹路径
     */
    public static String getAppPath(String filePath) {
        String path = getSdcardPath() + filePath;
        if (FileManagerUtils.isSdcard()) {
            File dirFile = new File(path);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
        }
        return path;
    }
}
