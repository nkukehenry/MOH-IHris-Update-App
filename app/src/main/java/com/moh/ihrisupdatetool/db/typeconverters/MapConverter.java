package com.moh.ihrisupdatetool.db.typeconverters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MapConverter {

    @TypeConverter
    public String fromSource(Map<String,String> map){
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    @TypeConverter
    public Map<String,String> toSource(String source) {
        Gson gson = new Gson();
        if (source == null) { return new HashMap<>(); }
        Type mapType = new TypeToken<Map<String,String>>() {}.getType();
        return gson.fromJson(source, mapType);
    }
}
