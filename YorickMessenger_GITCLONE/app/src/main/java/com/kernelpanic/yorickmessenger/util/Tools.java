package com.kernelpanic.yorickmessenger.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Tools {

    private Context context;

    private static Calendar calendar = new GregorianCalendar();

    public static String getTimeStamp() {
        Date now = new Date();
        calendar.setTime(now);

        String timestamp = null;
        timestamp = String.format(Locale.ENGLISH,"%d -  %d - %d | %d : %d : %d %s", calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE), calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.AM_PM));

        return timestamp;
    }

    public Tools(Context context) {
        this.context = context;
    }

    public static ArrayList<File> getDataDirs(Context context) {
        ArrayList<File> result = null;
        if (Build.VERSION.SDK_INT >= 19) {
            File[] dirs = context.getExternalFilesDirs(null);
            if (dirs != null) {
                for (int a = 0; a < dirs.length; a++) {
                    if (dirs[a] == null) {
                        continue;
                    }
                    String path = dirs[a].getAbsolutePath();

                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(dirs[a]);
                }
            }
        }
        if (result == null) {
            result = new ArrayList<>();
        }
        if (result.isEmpty()) {
            result.add(Environment.getExternalStorageDirectory());
        }
        return result;
    }

    public static ArrayList<File> getRootDirs(Context context) {
        ArrayList<File> result = null;
        if (Build.VERSION.SDK_INT >= 19) {
            File[] dirs = context.getExternalFilesDirs(null);
            if (dirs != null) {
                for (int a = 0; a < dirs.length; a++) {
                    if (dirs[a] == null) {
                        continue;
                    }
                    String path = dirs[a].getAbsolutePath();
                    int idx = path.indexOf("/Android");
                    if (idx >= 0) {
                        if (result == null) {
                            result = new ArrayList<>();
                        }
                        result.add(new File(path.substring(0, idx)));
                    }
                }
            }
        }
        if (result == null) {
            result = new ArrayList<>();
        }
        if (result.isEmpty()) {
            result.add(Environment.getExternalStorageDirectory());
        }
        return result;
    }

}
