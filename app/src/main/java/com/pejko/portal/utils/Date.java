package com.pejko.portal.utils;

import android.text.TextUtils;

import com.casperise.common.debug.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Date
{
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
    public static final SimpleDateFormat dateFormatDDMM = new SimpleDateFormat("dd.MM", Locale.US);
    public static final SimpleDateFormat timeDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US);
    public static final SimpleDateFormat ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    public static final SimpleDateFormat ISO8601Date = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public static String format(java.util.Date date)
    {
        if (date == null)
            return null;
        return dateFormat.format(date);
    }

    public static String formatDDMM(java.util.Date date)
    {
        if (date == null)
            return null;
        return dateFormatDDMM.format(date);
    }

    public static String formatDateTime(java.util.Date date)
    {
        if (date == null)
            return null;
        return timeDateFormat.format(date);
    }

    public static String formatISO8601Date(java.util.Date date)
    {
        if (date == null)
            return null;
        return ISO8601Date.format(date);
    }

    public static java.util.Date parseISO8601(String string)
    {
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        try {
            return ISO8601.parse(string);
        } catch (Throwable e) {
            Log.e(e);
        }
        return null;
    }

    public static java.util.Date parse(String date)
    {
        if (TextUtils.isEmpty(date) || date.equals("null"))
            return null;
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
