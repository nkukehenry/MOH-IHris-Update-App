package com.moh.ihrisupdatetool.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.moh.ihrisupdatetool.R;

import java.util.Random;

public class UIHelper {

    private ProgressDialog progressDialog;
    private Dialog dialog;

    public UIHelper(Context context) {
        progressDialog = new ProgressDialog(context);
        dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
    }

    public  void showLoader(){
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    public  void showLoader(String message){
        hideLoader();
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public  void hideLoader(){
        if(progressDialog.isShowing())
         progressDialog.dismiss();
    }

    public void showDialog(String msg){


        dialog.setContentView(R.layout.custom_light_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text = dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        Button dialogButton = dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }

}
