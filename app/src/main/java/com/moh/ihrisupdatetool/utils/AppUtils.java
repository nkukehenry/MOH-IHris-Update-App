package com.moh.ihrisupdatetool.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.text.DecimalFormat;

public class AppUtils {

        public static   <T> T ToBaseType(Object response, Type genType){
            Gson gson = new Gson();
            String json = gson.toJson(response);
            T data = gson.fromJson(json, genType);
            return  data;
        }

        public static   <T> T ToBaseType(String response, Type genType){
            Gson gson = new Gson();
            T data = gson.fromJson(response, genType);
            return  data;
        }

        public static String formatAmount(Double amount){
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            String formatedAmount = formatter.format(amount);
            return formatedAmount;
        }

}
