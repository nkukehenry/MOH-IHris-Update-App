package com.moh.ihrisupdatetool.utils;

public class AppConstants {

    public static  final  String SHAREDPREF_KEY= "IHRIS_TOOL";
    public static  final  String LOGIN_CODE= "LOGIN_CODE";
    public static  final  String USER_CODE= "USER_CODE";
    public static  final  String SELECTED_FORM ="selectedForm";
    public  static  final String BASE_URL  ="https://mobileihris.health.go.ug/api/data/";
    public  static  final String BASE_URL2 ="https://hris2.health.go.ug/api_systems/index.php/api/";
    public  static  final String BASE_URL3 ="https://hris.health.go.ug/apiv1/index.php/api/";

    public static String GET_FACILITIES_URL() { return String.format("%s%s",BASE_URL,"facilities"); }
    public static String GET_DISTRICTS_URL() { return String.format("%s%s",BASE_URL,"districts"); }
    public static String GET_JOBS_URL() { return String.format("%s%s",BASE_URL,"jobs"); }
    public static String GET_FORMS_URL() { return String.format("%s%s",BASE_URL,"forms"); }
    public static String GET_FORM_FIELDS_URL(Integer formId) { return String.format("%s%s%s",BASE_URL,"fields/",formId); }

    public static String POST_FORM_DATA_URL() { return String.format("%s%s",BASE_URL,"create"); }

    public static String GET_MINISTRY_WORKER_DATA_URL(String districtName) { return  String.format("%s%s%s",BASE_URL3,"hwdata/",districtName);}
    public static String GET_COMMUNITY_WORKER_DATA_URL(String districtName) { return String.format("%s%s%s",BASE_URL2,"chwdata/",districtName);}

    public static String GET_LOGIN_BYCODE(int userCode) { return String.format("%s%s%s",BASE_URL,"auth/",userCode); }
}
