package com.moh.ihrisupdatetool.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.utils.AppData;
import com.moh.ihrisupdatetool.utils.UIHelper;
import com.moh.ihrisupdatetool.viewmodels.LoginViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private UIHelper uiHelper;
    private Button loginBtn;
    private EditText userCodeTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        uiHelper = new UIHelper(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userCodeTxt = findViewById(R.id.userCodeTxt);
        loginBtn = findViewById(R.id.loginBtn);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginBtn.setOnClickListener(v -> {

            String userCode = userCodeTxt.getText().toString();

            if(!userCode.isEmpty()){
                postLogin(userCode);
            }else {
                Toast.makeText(LoginActivity.this, "Provide a usercode", Toast.LENGTH_SHORT).show();
            }
        });

        observerLoginResponse();
    }


    private void observerLoginResponse() {

        loginViewModel.observeLoginReponse().observe(this,loginReponse->{
            uiHelper.hideLoader();
            if(loginReponse!=null && loginReponse.getStatus()==1){
                AppData.userId = loginReponse.getUserId();
                Intent mainActivity = new Intent(this,MainActivity.class);
                startActivity(mainActivity);
                finish();
            }else{
                Toast.makeText(LoginActivity.this, "Login failed try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postLogin(String userCode){
        this.loginViewModel.doLogin(userCode);
    }
}