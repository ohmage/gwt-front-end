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
    public static void addParam(StringBuffer addHere, String key, String value) {
        if (addHere == null) {
            throw new IllegalArgumentException("StringBuffer cannot be null");
        }
        
        // If the key or value is null, also complain
        if (key == null || value == null) {
            throw new IllegalArgumentException("key or value is null");
        }
       
        // Check if this is the first parameter to add
        if (addHere.length() == 0) {
            addHere.append(URL.encode(key) + "=" + URL.encode(value));
        }
        else {
            addHere.append("&" + URL.encode(key) + "=" + URL.encode(value));
        }
    }
}
