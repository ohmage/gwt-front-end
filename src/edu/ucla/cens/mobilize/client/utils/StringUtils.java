package edu.ucla.cens.mobilize.client.utils;

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

    /**
     * Returns a copy of longString truncated at maxLength with ellipses
     * appended, or returns the original string if not longer than maxLength.
     * @param longString
     * @param maxLength
     * @return "Loooooooooooooooong -> Loooooooo..."
     */
    public static String shorten(String longString, int maxLength) {
      String retval = longString;
      if (longString != null && longString.length() > maxLength) {
        retval = longString.substring(0, maxLength) + "...";
      }
      return retval;
    }
    
    /**
     * Helper function for capitalizing the first letter of a string. (Included
     * here because the apache version is not supported in GWT.) Note that it 
     * only capitalizes the first letter, not the first letter of each word.
     * @param lowercaseString
     * @return string with first letter capitalized, all other letters unchanged
     */
    public static String capitalize(String lowercaseString) {
      return lowercaseString.substring(0, 1).concat(lowercaseString.substring(1));
    }
}
