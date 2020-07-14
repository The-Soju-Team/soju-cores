package com.viettel.authen.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	public java.util.Date sqlDateToJavaDate(java.sql.Date sqlDate) {
		java.util.Date javaDate = null;
		if (sqlDate != null) {
			javaDate = new Date(sqlDate.getTime());
		}
		return javaDate;
	}

	public String normalizationInputDate(String date) {
		SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
		date = spf.format(date);
		return date;
	}

	public Date StringToUtilDate(String date, String format) {
		SimpleDateFormat spf = new SimpleDateFormat(format);
		try {
			return spf.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public String utilDateString(Date date, String format) {
		SimpleDateFormat spf = new SimpleDateFormat(format);
		return spf.format(date);
	}

	public String calendarToString(Calendar cal, String dateFormat) {
		SimpleDateFormat spf = new SimpleDateFormat(dateFormat);
		String returnDate = spf.format(cal.getTime());
		return returnDate;
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
