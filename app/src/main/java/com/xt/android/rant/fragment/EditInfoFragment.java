package com.xt.android.rant.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.xt.android.rant.R;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class EditInfoFragment extends DialogFragment {

    private int userId;
    private String bio;
    private String location;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_dialog_edit, null);
        final EditText mBioEditText = (EditText) v.findViewById(R.id.fragment_dialog_edit_bio);
        final EditText mLocationEditText = (EditText) v.findViewById(R.id.fragment_dialog_edit_location);
        mBioEditText.setText(bio);
        mLocationEditText.setText(location);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(R.string.dialog_edit_submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newBio = mBioEditText.getText().toString();
                        String newLocation = mLocationEditText.getText().toString();
                        post(newBio, newLocation);
                    }
                })
                .setTitle(R.string.dialog_edit_title)
                .create();

    }

    public void setInfo(int userId, String bio, String location){
        this.userId = userId;
        this.bio = bio;
        this.location = location;
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


}
