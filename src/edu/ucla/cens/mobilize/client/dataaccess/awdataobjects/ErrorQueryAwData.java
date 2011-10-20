package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import com.google.gwt.core.client.JsArray;


/**
 * Used to parse and interpret errors when a query from the AndWellness server
 * returns "result"="failure"
 * 
 * @author jhicks
 *
 */
public class ErrorQueryAwData extends QueryAwData {
    protected ErrorQueryAwData() {};
    
    // Grab the array of errors
    public final native JsArray<ErrorAwData> getErrors()  /*-{ return this.errors; }-*/;
    
    public static native ErrorQueryAwData fromJsonString(String jsonString) /*-{
        return eval('(' + jsonString + ')'); 
    }-*/;
}
