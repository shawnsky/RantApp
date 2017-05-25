package com.xt.android.rant.fragment;


import android.Manifest;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;

import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.xt.android.rant.R;
import com.xt.android.rant.utils.Bimp;
import com.xt.android.rant.utils.FileManagerUtils;
import com.xt.android.rant.utils.Helper;
import com.xt.android.rant.utils.TokenUtil;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;


public class SelectPhotoFragment extends DialogFragment implements View.OnClickListener{
    private static final String TAG = "SelectPhotoFragment";
    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;

    private static final int MSG_GET_TOKEN = 3;
    private static final int MSG_UPLOAD_SUCCESS = 4;
    private String upToken;//用来保存服务端返回的上传凭证
    private int userId;
    private Uri photoUri;
    private String tempPicPath;
    private String filePath;//用来保存拍照/选择照片 的图片路径
    private String key;
    private OkHttpClient client;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_dialog_photo, null);
        final TextView camera = (TextView) v.findViewById(R.id.fragment_dialog_photo_camera);
        final TextView album = (TextView) v.findViewById(R.id.fragment_dialog_photo_album);
        camera.setOnClickListener(this);
        album.setOnClickListener(this);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

    }

    public void setInfo(int userId){
        this.userId = userId;
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fragment_dialog_photo_camera:
                String strImgPath = FileManagerUtils.getAppPath("rant") + "/";
                String fileName = System.currentTimeMillis() + ".jpg";// 照片命名
                File out = new File(strImgPath);
                if (!out.exists()) {
                    out.mkdirs();
                }
                out = new File(strImgPath, fileName);

//                if(Build.VERSION.SDK_INT>=24){
//                    photoUri = FileProvider.getUriForFile(getActivity(), "com.xt.android.rant.provider", out);
//                }
//                else{
                    photoUri = Uri.fromFile(out);
//                }
                tempPicPath = photoUri.getPath();
                //检查权限
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
                }
                else{
                    openCamera();
                }

                break;
            case R.id.fragment_dialog_photo_album:
                //检查权限
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                else{
                    openAlbum();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    Bitmap bitmap = null;
                    try {
                        bitmap = Helper.getInstance().rotaingImageView(Helper.getInstance().readPictureDegree(tempPicPath), Bimp.revitionImageSize(tempPicPath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(bitmap!=null){
                        tempPicPath = Helper.getInstance().saveMyBitmap(bitmap);
                        filePath = tempPicPath;
                        getQiniuUpToken();
                    }

                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    handleImage(data.getData());
                }
        }
    }

    private void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);

    }

    private void handleImage(Uri uri){
        String imagePath = null;
        if(DocumentsContract.isDocumentUri(getActivity(), uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);

            }
            else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }
        else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri, null);
        }
        else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        filePath = imagePath;
        Log.i(TAG, "handleImage: Album filepath"+filePath);
        getQiniuUpToken();
    }

    private String getImagePath(Uri uri, String selection){
        String path = null;
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        Log.i(TAG, "getImagePath: "+path);
        return path;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }
                else{
                    Toast.makeText(getActivity(), "需要权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    /**
     * 这里使用七牛云存储用户上传的图片
     * 流程
     * 1.得到照片File
     * 2.请求服务端获取七牛的上传Token
     * 3.构建一个七牛上传体
     * 4.执行上传成功回调: 图片地址发送给服务端
     */

    private void getQiniuUpToken(){
        String ip = getActivity().getString(R.string.ip_server);
         client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ip+"api/getQiniuUploadToken.action")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // TODO: 2017/5/24
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                upToken = String.valueOf(response.body().string());
                mHandler.sendEmptyMessage(MSG_GET_TOKEN);
            }
        });
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MSG_GET_TOKEN:
                    //此时filePath可能为空
                    uploadAvatar(new File(filePath), upToken);
                    Log.i(TAG, "handleMessage: upload"+filePath+" "+upToken);
                    break;
                case MSG_UPLOAD_SUCCESS:
                    Log.i(TAG, "handleMessage: image upload success, ready post to server");
                    changeAvatar();
                    break;
                default:
                    break;

            }
        }
    };

    private void uploadAvatar(File file, String token){

        Configuration config = new Configuration.Builder().zone(Zone.zone0).build();

        UploadManager uploadManager = new UploadManager(config);
        key = String.valueOf(userId)+String.valueOf(new Date().getTime())+".png";

        uploadManager.put(file, key, token, new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //res包含hash、key等信息，具体字段取决于上传策略的设置
                        if(info.isOK())
                        {
                            Log.i("qiniu", "Upload Success");
                            mHandler.sendEmptyMessage(MSG_UPLOAD_SUCCESS);
                        }
                        else{
                            Log.i("qiniu", "Upload Fail");
                            //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                        }
                        Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
                    }
                }, null);




    }

    private void changeAvatar(){
        String img = "http://oqfusne05.bkt.clouddn.com/"+key+"?imageView2/1/w/300/h/300/format/png/q/75|imageslim";
        String ip = getActivity().getString(R.string.ip_server);
        client = new OkHttpClient();
        RequestBody form = new FormBody.Builder()
                .add("token", TokenUtil.getToken(getActivity()))
                .add("userId", String.valueOf(userId))
                .add("img", img)
                .build();
        Request request = new Request.Builder()
                .post(form)
                .url(ip+"api/changeAvatar.action")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // TODO: 2017/5/24
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "onResponse: update img url to [server] success");
                dismiss();
            }
        });
    }

}
