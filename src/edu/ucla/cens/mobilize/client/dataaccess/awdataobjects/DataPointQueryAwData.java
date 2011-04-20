package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import com.google.gwt.core.client.JsArray;


public class DataPointQueryAwData extends QueryAwData {
    protected DataPointQueryAwData() {};
    
    // Grab the data
    public final native JsArray<DataPointAwData> getData() /*-{ return this.data; }-*/;
    
    // Create an AuthorizationTokenQueryAwData from a JSON string
    public static native DataPointQueryAwData fromJsonString(String jsonString) /*-{
        return eval('(' + jsonString + ')'); 
    }-*/;
}
