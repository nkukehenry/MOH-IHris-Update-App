package com.moh.ihrisupdatetool.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
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

    private String  default_data;
    private String  data_type;
    private Boolean is_required;
    private Boolean is_visible;
    private Integer db_constraint;
    private String  data_format;
    private Boolean is_disabled;

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

    public Boolean getIs_required() {
        return is_required;
    }

    public void setIs_required(Boolean is_required) {
        this.is_required = is_required;
    }

    public Boolean getIs_visible() {
        return is_visible;
    }

    public void setIs_visible(Boolean is_visible) {
        this.is_visible = is_visible;
    }

    public Integer getDb_constraint() {
        return db_constraint;
    }

    public void setDb_constraint(Integer db_constraint) {
        this.db_constraint = db_constraint;
    }

    public String getData_format() {
        return data_format;
    }

    public void setData_format(String data_format) {
        this.data_format = data_format;
    }

    public Boolean getIs_disabled() {
        return is_disabled;
    }

    public void setIs_disabled(Boolean is_disabled) {
        this.is_disabled = is_disabled;
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
                ", is_required=" + is_required +
                ", is_visible=" + is_visible +
                ", db_constraint=" + db_constraint +
                '}';
    }
}
