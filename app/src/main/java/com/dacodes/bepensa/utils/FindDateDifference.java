package com.dacodes.bepensa.utils;

import java.util.Date;

/**
 * Created by Mario Guillermo on 20,December,2017
 * Correo: mario.guillermo@dacodes.com.mx
 */

public class FindDateDifference {

    private Date startDate;
    private Date endDate;

    private long elapsedDays;
    private long elapsedHours;
    private long elapsedMinutes;
    private long elapsedSeconds;


    public FindDateDifference(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String calculateTimeSinceDate (){
        String dateTimeString = "Hace un momento";
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        setElapsedDays(different / daysInMilli);
        different = different % daysInMilli;
        setElapsedHours(different / hoursInMilli);
        different = different % hoursInMilli;
        setElapsedMinutes(different / minutesInMilli);
        different = different % minutesInMilli;
        setElapsedSeconds(different / secondsInMilli);

        if(elapsedDays>=7)
            dateTimeString = "Hace " + (elapsedDays/7) + " semanas";
        else if(elapsedDays>=1)
            dateTimeString ="Hace " + elapsedDays + " días";
        else if (elapsedHours>=1)
            dateTimeString ="Hace " + elapsedHours + " horas";
        else if (elapsedMinutes>=1)
            dateTimeString = "Hace " + elapsedMinutes + " minutos";
        return dateTimeString;
    }


    public long getElapsedDays() {
        return elapsedDays;
    }

    public void setElapsedDays(long elapsedDays) {
        this.elapsedDays = elapsedDays;
    }

    public long getElapsedHours() {
        return elapsedHours;
    }

    public void setElapsedHours(long elapsedHours) {
        this.elapsedHours = elapsedHours;
    }

    public long getElapsedMinutes() {
        return elapsedMinutes;
    }

    public void setElapsedMinutes(long elapsedMinutes) {
        this.elapsedMinutes = elapsedMinutes;
    }

    public long getElapsedSeconds() {
        return elapsedSeconds;
    }

    public void setElapsedSeconds(long elapsedSeconds) {
        this.elapsedSeconds = elapsedSeconds;
    }
}
