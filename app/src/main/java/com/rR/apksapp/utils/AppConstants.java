package com.rR.apksapp.utils;

import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AppConstants {
    public static final int GRID_COLUMNS = 3;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 1234;
    public static final String FOLDER_NAME = "AppsAPK";
    public static final String FOLDER_PATH = Environment.getExternalStorageDirectory().
            getAbsolutePath();
    public static String CURRENT_INSIDE_FOLDER_NAME = getTodayDateWithCurrentTime();
    private static final String SIMPLE_DATE_FORMAT = "dd-MM-yyyy";

    private static String getTodayDateWithCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
        return simpleDateFormat.format(new Date(System.currentTimeMillis()));
    }

}