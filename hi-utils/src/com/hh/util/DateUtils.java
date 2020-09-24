package com.hh.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author TruongNX25
 */

public class DateUtils {
    public String calendarToString(Calendar cal, String dateFormat) {
        SimpleDateFormat spf = new SimpleDateFormat(dateFormat);
        return spf.format(cal.getTime());
    }

    public Calendar stringToCalendar(String theDate, String dateFormat) {
        SimpleDateFormat spf = new SimpleDateFormat(dateFormat);
        try {
            Date date = spf.parse(theDate);
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DATE, date.getDate());
            cal.set(Calendar.MONTH, date.getMonth());
            cal.set(Calendar.YEAR, date.getYear() + 1900);
            return cal;
        } catch (Exception e) {
            return null;
        }
    }

    public String dateStringToDateString(String from, String fromFormat, String toFormat) {
        SimpleDateFormat spf = new SimpleDateFormat(fromFormat);
        SimpleDateFormat spf1 = new SimpleDateFormat(toFormat);
        try {
            Date dateFrom = spf.parse(from);
            return spf1.format(dateFrom);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
