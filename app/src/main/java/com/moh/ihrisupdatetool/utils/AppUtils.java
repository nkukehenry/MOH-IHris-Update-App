package com.moh.ihrisupdatetool.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.text.InputType;
import android.util.Base64;

import com.google.gson.Gson;
import com.moh.ihrisupdatetool.dto.FormFieldType;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.Random;

public class AppUtils {

    public static   <T> T objectToType(Object response, Type genType){
        Gson gson = new Gson();
        String json = gson.toJson(response);
        T data = gson.fromJson(json, genType);
        return  data;
    }

    public static   <T> T stringToType(String response, Type genType){
        Gson gson = new Gson();
        T data = gson.fromJson(response, genType);
        return  data;
    }

    public static String formatAmount(Double amount){
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        return formatter.format(amount);
    }

    public static String bitmapTobase64(Bitmap bm) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

    public static int getInputTypeClass(String remoteType){

        switch (remoteType){
            case "date":
                return InputType.TYPE_DATETIME_VARIATION_DATE;
            case "number":
            case "numeric":
                return InputType.TYPE_CLASS_NUMBER;
            case "decimal":
                return InputType.TYPE_NUMBER_FLAG_DECIMAL;
            case "phone":
                return InputType.TYPE_CLASS_PHONE;
            case "email":
                return InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
            default:
                return InputType.TYPE_CLASS_TEXT;
        }
    }


    public static FormFieldType InputType(String fieldType){

        switch (fieldType){
            case "phone":
            case "decimal":
            case "number":
            case "email":
                return FormFieldType.TEXT_BASED_FIELD;

            case "date":
                return FormFieldType.DATE_FIELD;
            case "charmap":
                return FormFieldType.TEXT_AUTOCOMPLETE_FIELD;
            case "blob":
                return FormFieldType.IMAGE_FIELD;
            case "varcharmap":
                return FormFieldType.SPINNER_BASED_FIELD;
        }
        return FormFieldType.TEXT_BASED_FIELD;
    }


    public static String getRandomString(int len)
    {
        Random random = new Random();
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }


    public static Bitmap resizeBitmap(Bitmap bitmap) {

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        float scaleWidth = ((float) width*5) / width;
        float scaleHeight = ((float) height*5) / height;

        Matrix matrix = new Matrix();
        // here we do resize the bitmap
        matrix.postScale(scaleWidth, scaleHeight);

        // and create new one
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        return newBitmap;
    }

}
