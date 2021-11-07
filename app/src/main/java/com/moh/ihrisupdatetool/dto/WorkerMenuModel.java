package com.moh.ihrisupdatetool.dto;

import android.app.Activity;
import android.graphics.drawable.Drawable;

public class WorkerMenuModel {

    private String label;
    private Drawable icon;
    private Activity naviagatesto;

    public WorkerMenuModel(String label, Drawable icon, Activity naviagatesto) {
        this.label = label;
        this.icon = icon;
        this.naviagatesto = naviagatesto;
    }

    public String getLabel() {
        return label;
    }

    public Drawable getIcon() {
        return icon;
    }

    public Activity getNaviagatesto() {
        return naviagatesto;
    }
}
