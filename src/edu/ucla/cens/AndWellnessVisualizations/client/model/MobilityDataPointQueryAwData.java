package edu.ucla.cens.AndWellnessVisualizations.client.model;

import com.google.gwt.core.client.JsArray;

public class MobilityDataPointQueryAwData extends QueryAwData {
    protected MobilityDataPointQueryAwData() {};
    
    // Grab the data
    public final native JsArray<MobilityDataPointAwData> getData() /*-{ return this.data; }-*/;
    
    // Create an AuthorizationTokenQueryAwData from a JSON string
    public static native MobilityDataPointQueryAwData fromJsonString(String jsonString) /*-{
        return eval('(' + jsonString + ')'); 
    }-*/;
}
