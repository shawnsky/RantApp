package com.xt.android.rant.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xt.android.rant.MainActivity;
import com.xt.android.rant.R;
import com.xt.android.rant.adapter.MyPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotifyFragment extends Fragment {
    private static final String TAG = "NotifyFragment";
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;



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
        setRetainInstance(true);

        mToolbar = (Toolbar) view.findViewById(R.id.fragment_notify_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);


        mTabLayout = (TabLayout) view.findViewById(R.id.fragment_notify_tab_layout);
        mViewPager = (ViewPager) view.findViewById(R.id.fragment_notify_view_pager);

        mViewPager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));

        mViewPager.setOffscreenPageLimit(2);

        mTabLayout.setupWithViewPager(mViewPager);

        return view;
    }




}
