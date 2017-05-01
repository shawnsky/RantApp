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
public class NewFragment extends Fragment {

    private Toolbar mToolbar;


    public NewFragment() {
        // Required empty public constructor
    }


    public static NewFragment newInstance(String param1) {
        NewFragment fragment = new NewFragment();
        Bundle args = new Bundle();
        args.putString("args1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new, container, false);
//        setHasOptionsMenu(true);


        mToolbar = (Toolbar) view.findViewById(R.id.fragment_new_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);






        return view;
    }



}
