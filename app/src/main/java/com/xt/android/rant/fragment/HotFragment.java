package com.xt.android.rant.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.xt.android.rant.PostActivity;
import com.xt.android.rant.R;
import com.xt.android.rant.adapter.NewAdapter;
import com.xt.android.rant.utils.SpaceItemDecoration;
import com.xt.android.rant.wrapper.RantItem;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.OkHttpClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class HotFragment extends Fragment implements View.OnClickListener {

    private static final int MSG_GET_RANT_LIST = 0;
    private static final String TAG = "HotFragment";
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFAB;
    private ImageView mSortButton;
    private int sortFlag;

    private String mJson;
    private OkHttpClient mClient;
    private Handler mHandler;

    private List<RantItem> hotRantList;


    public static HotFragment newInstance(String param1) {
        HotFragment fragment = new HotFragment();
        Bundle args = new Bundle();
        args.putString("args1", param1);
        fragment.setArguments(args);
        return fragment;
    }


    public HotFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hot, container, false);
//        setHasOptionsMenu(true);



        mToolbar = (Toolbar) view.findViewById(R.id.fragment_hot_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSortButton = (ImageView) view.findViewById(R.id.fragment_hot_sort);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.fragment_hot_recycler_view);
        mFAB = (FloatingActionButton) view.findViewById(R.id.fragment_hot_fab);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(1));
        mRecyclerView.setAdapter(new NewAdapter(new ArrayList<RantItem>()));


        List<RantItem> rantItemsFromDatabase = DataSupport.findAll(RantItem.class);
        hotRantList = new ArrayList<>();
        for(RantItem rantItem:rantItemsFromDatabase){
            if(Math.abs(rantItem.getRantValue())>=5 || rantItem.getCommentsNum()>=5) hotRantList.add(rantItem);
        }


        NewAdapter adapter = new NewAdapter(hotRantList);
        mRecyclerView.setAdapter(adapter);

        sortFlag = 1;


        mSortButton.setOnClickListener(this);
        mFAB.setOnClickListener(this);





        return view;
    }

    private void updateUI(){
        NewAdapter adapter = new NewAdapter(hotRantList);
        mRecyclerView.setAdapter(adapter);
        Toast.makeText(getActivity(),
                sortFlag==1?getActivity().getString(R.string.hot_sort_toast):getActivity().getString(R.string.hot_sort_toast_value),
                Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View view){
	switch(view.getId()){
        case R.id.fragment_hot_sort:
			hotRantList = rantSort(hotRantList,sortFlag);
            sortFlag = -sortFlag;
            updateUI();
            break;
        case R.id.fragment_hot_fab:
            Intent intent = new Intent(getActivity(), PostActivity.class);
            startActivity(intent);
            break;

    }
    }


    /**
     * -1 value
     * 1 comments number
     * @param flag
     */

    private List<RantItem> rantSort(List<RantItem> list, int flag){
	    if(flag==1){
		    Collections.sort(list, new Comparator<RantItem>() {
                @Override
                public int compare(RantItem rantItem, RantItem t1) {
                    return rantItem.getCommentsNum()-t1.getCommentsNum();
                }
            });
    } else{
		Collections.sort(list, new Comparator<RantItem>() {
            @Override
            public int compare(RantItem rantItem, RantItem t1) {
                return rantItem.getRantValue() - t1.getRantValue();
            }
        });
    }
        return list;
    }




}
