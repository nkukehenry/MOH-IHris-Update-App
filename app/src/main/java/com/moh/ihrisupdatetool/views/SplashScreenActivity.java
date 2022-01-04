package com.moh.ihrisupdatetool.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.utils.AppConstants;
import com.moh.ihrisupdatetool.utils.AppData;
import com.moh.ihrisupdatetool.utils.AppUtils;
import com.moh.ihrisupdatetool.viewmodels.LoginViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SplashScreenActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    LoginViewModel loginViewModel;
    TextView versionNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        versionNumber=findViewById(R.id.versionNumber);

        sharedPreferences = getSharedPreferences(AppConstants.SHAREDPREF_KEY, Context.MODE_PRIVATE);
        versionNumber.setText("App Verion: "+AppConstants.APP_VERSION);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        goIn();

    }

    private void goIn(){


        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(getLoginCache() >0)
                    postLogin(sharedPreferences.getInt(AppConstants.USER_CODE,0));
                else
                {
                    Intent  intent=new Intent(SplashScreenActivity.this, LoginActivity.class
                    );
                    startActivity(intent);
                    finish();
                }
            }
        },3000);
    }


    private void postLogin(int userCode){

        this.loginViewModel.doLogin(userCode).observe(this, loginReponse->{

            if(loginReponse!=null && loginReponse.getStatus() == 1){
                AppData.userId = loginReponse.getUserId();
                AppData.session = loginReponse;

                Intent mainActivity = new Intent(this,MainActivity.class);
                startActivity(mainActivity);
                finish();

            }else{

                Intent  intent=new Intent(SplashScreenActivity.this, LoginActivity.class
                );
                startActivity(intent);
                finish();
            }

        });
    }

    private int getLoginCache(){
        int userId = sharedPreferences.getInt(AppConstants.LOGIN_CODE,0);
        AppData.userId = userId;
        return userId;
    }

}