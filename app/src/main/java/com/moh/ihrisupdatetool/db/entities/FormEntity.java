package com.moh.ihrisupdatetool.db.entities;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "forms")
public class FormEntity implements Serializable {

    @PrimaryKey
    @NonNull
    private  Integer id;
    private  String form_title;
    private  String description;
    private  String parent_form;
    private  String status;

    public FormEntity(@NonNull Integer id, String form_title, String description, String parent_form, String status) {
        this.id = id;
        this.form_title = form_title;
        this.description = description;
        this.parent_form = parent_form;
        this.status = status;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public String getForm_title() {
        return form_title;
    }

    public String getDescription() {
        return description;
    }

    public String getParent_form() {
        return parent_form;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "FormEntity{" +
                "id=" + id +
                ", form_title='" + form_title + '\'' +
                ", description='" + description + '\'' +
                ", parent_form=" + parent_form +
                ", status=" + status +
                '}';
    }

    @Override
    public int hashCode(){
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormEntity that = (FormEntity) o;
        return id.equals(that.id);
    }

}
