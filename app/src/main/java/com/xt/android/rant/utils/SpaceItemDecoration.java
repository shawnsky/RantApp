package com.xt.android.rant.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by admin on 2017/5/2.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    public SpaceItemDecoration(int space){
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildPosition(view)!=0){
            outRect.top = space;
        }
    }
}
