package edu.ucla.cens.AndWellnessVisualizations.client.utils;

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
    public final static String timeStampFormat = "yyyy-MM-dd HH:mm:ss";
    
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
    
    /**
     *  Checks to see if a Date is within a given Day.
     * 
     * @param date The date to check.
     * @param day The day that the date should be in.
     * @return True/false.
     */
    public static boolean isDateInDay(Date date, Date day) {
        
    }
}
