package edu.ucla.cens.AndWellnessVisualizations.client.model;

public class ConfigQueryAwData extends QueryAwData {
    protected ConfigQueryAwData() {};
    
    // Grab the data
    public final native ConfigAwData getData() /*-{ return this.data; }-*/;
    
    // Create an AuthorizationTokenQueryAwData from a JSON string
    public static native ConfigQueryAwData fromJsonString(String jsonString) /*-{
        return eval('(' + jsonString + ')'); 
    }-*/;
}
