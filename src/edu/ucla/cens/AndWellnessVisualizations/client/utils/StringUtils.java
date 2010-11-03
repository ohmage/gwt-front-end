package edu.ucla.cens.AndWellnessVisualizations.client.utils;

import com.google.gwt.http.client.URL;

public class StringUtils {
    /**
     * Adds the passed key/value pair onto the end of the string.  Call repeatedly
     * to add multiple parameters onto a string.
     * 
     * @param addHere The string to add to.
     * @param key The key to add to the string.
     * @param value The value to add to the string.
     * @return Returns the new, modified string.
     */
    public static String addParam(String addHere, String key, String value) {
        // If addHere is null, create a new String and return
        if (addHere == null)
            return URL.encode(key) + "=" + URL.encode(value);
        else 
            return addHere + "&" + URL.encode(key) + "=" + URL.encode(value); 
    }
}
