package edu.ucla.cens.AndWellnessVisualizations.client.model;

import com.google.gwt.core.client.JsArray;

public class ChunkedMobilityQueryAwData extends QueryAwData {
    protected ChunkedMobilityQueryAwData() {};
    
    // Grab the data
    public final native JsArray<ChunkedMobilityAwData> getData() /*-{ return this.data; }-*/;
    
    // Create an AuthorizationTokenQueryAwData from a JSON string
    public static native ChunkedMobilityQueryAwData fromJsonString(String jsonString) /*-{
        return eval('(' + jsonString + ')'); 
    }-*/;
}
