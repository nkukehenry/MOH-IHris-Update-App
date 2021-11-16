package com.moh.ihrisupdatetool.utils;

import android.graphics.Bitmap;
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
        String formatedAmount = formatter.format(amount);
        return formatedAmount;
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
                return InputType.TYPE_CLASS_DATETIME;
            case "number":
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
            case "date":
            case "email":
                return FormFieldType.TEXT_BASED_FIELD;
            case "charmap":
                return FormFieldType.TEXT_AUTOCOMPLETE_FIELD;
            case "blob":
                return FormFieldType.IMAGE_FIELD;
            case "map":
                return FormFieldType.SPINNER_BASED_FIELD;
        }
        return FormFieldType.TEXT_BASED_FIELD;
    }


    public static String getRandomString(int len)
    {
        Random random = new Random();
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk"
                +"lmnopqrstuvwxyz";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

}
