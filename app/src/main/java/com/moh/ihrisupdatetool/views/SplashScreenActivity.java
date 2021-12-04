package com.moh.ihrisupdatetool.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.utils.AppConstants;
import com.moh.ihrisupdatetool.utils.AppData;
import com.moh.ihrisupdatetool.utils.AppUtils;

public class SplashScreenActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sharedPreferences = getSharedPreferences(AppConstants.SHAREDPREF_KEY, Context.MODE_PRIVATE);
        goIn();

    }

    private void goIn(){


        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                  Intent  intent=new Intent(SplashScreenActivity.this,

                          ( getLoginCache()>0 )? MainActivity.class : LoginActivity.class
                  );
                startActivity(intent);
                finish();
            }
        },3000);
    }

    private int getLoginCache(){
        int userId = sharedPreferences.getInt(AppConstants.LOGIN_CODE,0);
        AppData.userId = userId;
        return userId;
    }
}