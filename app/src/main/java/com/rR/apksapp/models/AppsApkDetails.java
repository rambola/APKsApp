package com.rR.apksapp.models;

import android.graphics.drawable.Drawable;

public class AppsApkDetails {
    private String appName;
    private String apkFilePath;
    private boolean isSelected;
    private Drawable drawable;

    public AppsApkDetails(String appName, String apkFilePath, Drawable drawable, boolean isSelected) {
        this.appName = appName;
        this.apkFilePath = apkFilePath;
        this.drawable = drawable;
        this.isSelected = isSelected;
    }

    public String getAppName() {
        return appName;
    }

    public String getApkFilePath() {
        return apkFilePath;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

}