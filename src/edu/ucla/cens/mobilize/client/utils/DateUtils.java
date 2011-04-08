package edu.ucla.cens.mobilize.client.utils;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;


/**
 * Contains various Date based helper utilities.
 * 
 * @author jhicks
 *
 */
public class DateUtils {
    // Format of the timestamp string returned from the server
    public final static String timeStampFormat = "yyyy-MM-dd HH:mm:ss.S";
    // Format of the timestamp string to upload to server POST api
    public final static String uploadTimeStampFormat = "yyyy-MM-dd";
    
    /**
     * Translates a String from the server time format to a Date.
     * 
     * @param toTranslate The Date to translate.
     * @return The translated Date.
     */
    public static Date translateFromServerFormat(String toTranslate) {
        DateTimeFormat dateFormat = DateTimeFormat.getFormat(timeStampFormat);
        return dateFormat.parse(toTranslate);
    }
    
    public static String translateToServerUploadFormat(Date toTranslate) {
        DateTimeFormat dateFormat = DateTimeFormat.getFormat(uploadTimeStampFormat);
        return dateFormat.format(toTranslate);
    }
    
    /**
     *  Checks to see if a Date is within a given Day.  Uses deprecated Date functionality,
     *  but GWT does not yet include the replacement functionality in Calendar.
     * 
     * @param date The date to check.
     * @param day The day that the date should be in.
     * @return True/false.
     */
    @SuppressWarnings("deprecation")
    public static boolean isDateInDay(Date date, Date day) {
        return (date.getYear() == day.getYear() &&
                date.getMonth() == day.getMonth() &&
                date.getDate() == day.getDate());
    }
    
    /**
     * Checks to see if a Date is within a given month.  Uses deprecated Date functionality,
     * but GWT does not yet include the replacement functionality in Calendar.
     * 
     * @param date The Date to check.
     * @param month The month the date should be in.
     * @return True/false.
     */
    @SuppressWarnings("deprecation")
    public static boolean isDateInMonth(Date date, Date month) {
        return (date.getYear() == month.getYear() &&
                date.getMonth() == month.getMonth());
    }
}
