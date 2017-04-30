package com.xt.android.rant;

import android.app.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.xt.android.rant.fragment.LoginFragment;
import com.xt.android.rant.fragment.RegisterFragment;


public class AuthActivity extends FragmentActivity {

    private static final String EXTRA_BUTTON="extra_button";

    private int fragmentFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        fragmentFlag = getIntent().getIntExtra(EXTRA_BUTTON,0);
        FragmentManager fm = getSupportFragmentManager();
        switch (fragmentFlag){
            case 0:
                RegisterFragment registerFragment = new RegisterFragment();
                fm.beginTransaction().add(R.id.activity_auth,registerFragment).commit();
                break;
            case 1:
                LoginFragment loginFragment = new LoginFragment();
                fm.beginTransaction().add(R.id.activity_auth, loginFragment).commit();
                break;
        }
    }
}
