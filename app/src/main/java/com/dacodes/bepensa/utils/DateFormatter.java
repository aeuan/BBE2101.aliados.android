package com.dacodes.bepensa.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Mario Guillermo on 20,December,2017
 * Correo: mario.guillermo@dacodes.com.mx
 */

public class DateFormatter {

    private Date startDate;
    private String inputFormat, outputFormat;

    public DateFormatter(Date startDate, String format) {
        this.startDate = startDate;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
    }

    public static String formatDate (String inputFormat, String outputFormat, String dateString, Locale locale){
        String formattedDate = "";
        Date date;
        SimpleDateFormat df = new SimpleDateFormat(inputFormat, locale);
        String upToNCharacters = dateString.substring(0, Math.min(dateString.length(), 19));
        try {
            date = df.parse(upToNCharacters);
            TimeZone tz = TimeZone.getDefault();
            SimpleDateFormat destFormat = new SimpleDateFormat(outputFormat, locale);
            destFormat.setTimeZone(tz);
            formattedDate = destFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }


}
