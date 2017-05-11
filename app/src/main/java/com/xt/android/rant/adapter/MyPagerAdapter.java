package com.xt.android.rant.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.xt.android.rant.fragment.NotifyCmtFragment;
import com.xt.android.rant.fragment.NotifyStarFragment;

import java.util.List;

/**
 * Created by admin on 2017/5/10.
 */

public class MyPagerAdapter extends FragmentPagerAdapter{

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:

                return new NotifyCmtFragment();
            case 1:
                return new NotifyStarFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "评论";
            case 1:
                return "通知";
            default:
                return null;
        }
    }
}