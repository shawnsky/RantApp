package com.xt.android.rant.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xt.android.rant.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotifyFragment extends Fragment {

    private Toolbar mToolbar;

    public static NotifyFragment newInstance(String param1) {
        NotifyFragment fragment = new NotifyFragment();
        Bundle args = new Bundle();
        args.putString("args1", param1);
        fragment.setArguments(args);
        return fragment;
    }


    public NotifyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notify, container, false);


        mToolbar = (Toolbar) view.findViewById(R.id.fragment_notify_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        activity.setSupportActionBar(mToolbar);


        return view;
    }

}
