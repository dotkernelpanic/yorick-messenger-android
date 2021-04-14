package com.kernelpanic.yorickmessenger.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Tools {

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
}
