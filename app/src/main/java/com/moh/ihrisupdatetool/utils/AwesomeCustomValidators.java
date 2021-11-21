package com.moh.ihrisupdatetool.utils;

import android.util.Patterns;

import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AwesomeCustomValidators {

    public SimpleCustomValidation optionalEmailValidator =  new SimpleCustomValidation() {
        @Override
        public boolean compare(String input) {
            if(input!=null && input.length()>0){
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                Matcher matcher = pattern.matcher(input);
                return matcher.find();
            }
            return true;
        }
    };

    public static  SimpleCustomValidation maxLengthValidator(Integer maxLength)
    {
        return input -> input.length() <= maxLength;
    }


}