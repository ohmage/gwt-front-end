package org.ohmage.mobilize.client.dataaccess.ohdataobjects;

import com.google.gwt.core.client.JsArray;

/**
 * Used to parse and interpret errors when a query from the Ohmage server
 * returns "result"="failure"
 *
 * @author jhicks
 *
 */
public class ErrorQueryOhData extends QueryOhData {

    protected ErrorQueryOhData() {
    }

    ;
    
    // Grab the array of errors
    public final native JsArray<ErrorOhData> getErrors() /*-{ 
     return this.errors; 
     }-*/;

    public static native ErrorQueryOhData fromJsonString(String jsonString)
            /*-{return eval('(' + jsonString + ')');}-*/;
}
