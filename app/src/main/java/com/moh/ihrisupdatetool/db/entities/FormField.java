package com.moh.ihrisupdatetool.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.moh.ihrisupdatetool.db.typeconverters.MapConverter;

import java.util.Map;

@Entity(tableName = "form_fields")
public class FormField {

    @PrimaryKey
    @NonNull
    private String id;
    private String form_id;
    private String form_field;
    private String display;
    private String label;

    //@TypeConverters(MapConverter.class)
    private String default_data;
    private String data_type;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getForm_id() {
        return form_id;
    }

    public void setForm_id(String form_id) {
        this.form_id = form_id;
    }

    public String getForm_field() {
        return form_field;
    }

    public void setForm_field(String form_field) {
        this.form_field = form_field;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public String getDefault_data() {
        return default_data;
    }

    public void setDefault_data(String default_data) {
        this.default_data = default_data;
    }

    @Override
    public String toString() {
        return "FormField{" +
                "id='" + id + '\'' +
                ", form_id='" + form_id + '\'' +
                ", form_field='" + form_field + '\'' +
                ", display='" + display + '\'' +
                ", label='" + label + '\'' +
                ", default_data='" + default_data + '\'' +
                ", data_type='" + data_type + '\'' +
                '}';
    }
}
