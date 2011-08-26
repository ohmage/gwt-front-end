package edu.ucla.cens.mobilize.client.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gwt.http.client.URL;

public class MapUtils {
    /**
     * Translate a map of key/values pairs into a string of parameters in the form of
     * k1=v1&k1=v2&...
     * @param toTranslate The map to translate.
     * @return The translated string.
     */
    public static String translateToParameters(Map<?,?> toTranslate) {
        StringBuffer parameterString = new StringBuffer();
        boolean firstParameter = true;  // Don't put an & for the first parameters
        
        for (Object key:toTranslate.keySet()) {
            // If this is not the first parameter, start with a separating ampersand
            if (!firstParameter) {
                parameterString.append("&");
            }
            
            // Add the key=value
            parameterString.append(URL.encode(key.toString()));
            parameterString.append("=");
            Object value = toTranslate.get(key);
            if (value == null) throw new RuntimeException("MapUtils cannot translate null value for parameter with key: " + key.toString());
            parameterString.append(URL.encode(value.toString()));
            
            firstParameter = false;
        }
        
        return parameterString.toString();
    }
    
    /**
     * Useful when you have a list mapping ids to display names and you want to 
     * generate a gui element (like a drop down) where the display names are in
     * alphabetical order
     * @param map Map of string keys to string values
     * @return List of (string) keys from the map such that if you get and print
     * the associated values the values will be in ascending alphabetical order
     */
    public static List<String> getKeysSortedByValues(Map<String, String> map) {
      String delim = "!!!###!!!"; // unlikely to appear in a real string
      List<String> valueKeyPairs = new ArrayList<String>();
      // append keys to values
      for (String key : map.keySet()) {
        String keyValuePair = map.get(key) + delim + key;
        valueKeyPairs.add(keyValuePair);
      }
      // sort by values
      Collections.sort(valueKeyPairs);
      List<String> keysSortedByValues = new ArrayList<String>();
      // extract keys back out in the sorted order
      for (String keyValuePair : valueKeyPairs) {
        keysSortedByValues.add(keyValuePair.split(delim)[1]);
      }
      return keysSortedByValues;
    }
}
