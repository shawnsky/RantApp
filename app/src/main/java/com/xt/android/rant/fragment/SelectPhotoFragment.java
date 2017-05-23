package com.xt.android.rant.fragment;


import android.Manifest;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import com.xt.android.rant.R;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;


public class SelectPhotoFragment extends DialogFragment implements View.OnClickListener{
    private static final String TAG = "SelectPhotoFragment";
    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private int userId;
    private Uri imageUri;

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

    private void post(String newBio, String newLocation){
        String ip = getActivity().getString(R.string.ip_server);
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("userId", String.valueOf(userId))
                .add("bio", newBio)
                .add("location", newLocation)
                .build();

        Request request = new Request.Builder()
                .url(ip+"api/editInfo.action")
                .post(formBody)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fragment_dialog_photo_camera:
                File outputImage = new File(getActivity().getExternalCacheDir(), "output_image.jpg");
                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT >= 24){
                    imageUri = FileProvider.getUriForFile(getActivity(),"com.xt.android.rant.provider", outputImage);
                }
                else {
                    imageUri = Uri.fromFile(outputImage);
                }

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
                    Log.i(TAG, "CameraImage: "+imageUri.getPath());
                    File file = new File(imageUri.getPath());
                    Log.i(TAG, "onActivityResult: filepath"+file.getPath());
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    handleImage(data);
                }
        }
    }

    private void openCamera(){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);

    }

    private void handleImage(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
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
        displayImage(imagePath);
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

    private void displayImage(String imagePath){
        Log.i(TAG, "AlbumImage: "+(imagePath==null?"is null":"not null"));
        Log.i(TAG, "AlbumImage: "+imagePath);
        Toast.makeText(getActivity(), imagePath, Toast.LENGTH_LONG).show();
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
}
