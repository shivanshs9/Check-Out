package com.checkin.app.checkin.Home;

import android.os.Bundle;

import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.R;

import androidx.annotation.Nullable;
import butterknife.OnClick;

public class UserBrownieCashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user_brownie_cash);
    }

    @OnClick(R.id.im_home_brownie_cash_back)
    public void onBackPress(){
        onBackPressed();

    }
}