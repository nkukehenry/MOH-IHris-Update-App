package com.moh.ihrisupdatetool.db.typeconverters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class JsonObjectConverter {

    @TypeConverter
    public String fromSource(JsonObject jsonObject){
        Gson gson = new Gson();
        return gson.toJson(jsonObject);
    }

    @TypeConverter
    public JsonObject toSource(String source) {
        Gson gson = new Gson();
        if (source == null) { return new JsonObject(); }
        Type mapType = new TypeToken<JsonObject>() {}.getType();
        return gson.fromJson(source, mapType);
    }
}
