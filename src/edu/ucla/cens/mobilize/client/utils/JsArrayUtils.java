package edu.ucla.cens.mobilize.client.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * Various functionality for JsArrays.
 * 
 * @author jhicks
 *
 */
public class JsArrayUtils {
    public static <T extends JavaScriptObject> List<T> translateToList(JsArray<T> jsArray) {
        List<T> toReturn = new ArrayList<T>();
        
        // Run through the JsArray, adding everything to the List
        for (int i = 0; i < jsArray.length(); ++i) {
            toReturn.add(jsArray.get(i));
        }
        
        return toReturn;
    }
}
