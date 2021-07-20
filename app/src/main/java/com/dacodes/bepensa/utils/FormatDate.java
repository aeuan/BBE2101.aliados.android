package com.dacodes.bepensa.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatDate {

    public static String getFormatMin(String date, String format){
        String displayValue = null;
        SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
        try {
            Date date2 = dateFormatter.parse(date);
            SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
            displayValue= timeFormatter.format(date2);
        } catch (ParseException e) {
            Log.e("formatDate",""+e.getLocalizedMessage());
            e.printStackTrace();
        }
        return displayValue;
    }

    public static String getDateLocale(String formar){
        java.util.Calendar cal = java.util.Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(String.valueOf(formar),Locale.ENGLISH);
        Date date1 = new Date();
        return String.valueOf(sdf.format(date1));
    }

    public static Date getDate(String date,String format){
        SimpleDateFormat sdf = new SimpleDateFormat(String.valueOf(format), Locale.ENGLISH);
        Date date1 = null;
        try {
            date1=sdf.parse(date);
        } catch (ParseException e) {
            Log.e("FORMAT_DATE",""+e.getLocalizedMessage());
        }
        return date1;
    }
}
