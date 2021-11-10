package com.moh.ihrisupdatetool.services;

import android.app.Application;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Handler;
import android.view.WindowManager;

import javax.inject.Inject;

import okhttp3.Response;
import okhttp3.ResponseBody;

public class ApiErrorHandlingService  implements IApiErrorHandlingService {

    private final Application _context;
    private final ProgressDialog _loader;
    private int LAYOUT_FLAG;

    @Inject
    public ApiErrorHandlingService(Application context, ProgressDialog progressDialog) {
        _context = context;
        _loader = progressDialog;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY-1;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_TOAST;
        }

    }

    public Response networkErrorHandling(Response response){

        String rawJson= "";
        try {

        }catch (Exception ex){
            ex.printStackTrace();
        }

        final Handler handler = new Handler(_context.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                //If successful, don't show the dialog
            }
        });

        // Re-create the response before returning it because body can be read only once
        return response.newBuilder().body(ResponseBody.create(response.body().contentType(), rawJson)).build();

    }

    public void showNetworkRequestLoader(){

        final Handler handler = new Handler(_context.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                _loader.getWindow().setType(LAYOUT_FLAG);

                if(!_loader.isShowing())
                    _loader.show();
            }
        });
    }

    public void closeNetworkRequestLoader(){

        final Handler handler = new Handler(_context.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                _loader.getWindow().setType(LAYOUT_FLAG);

                if(_loader.isShowing())
                    _loader.dismiss();
            }
        });

    }
}
