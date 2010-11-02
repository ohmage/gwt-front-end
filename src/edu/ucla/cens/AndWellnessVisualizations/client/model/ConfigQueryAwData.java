package edu.ucla.cens.AndWellnessVisualizations.client.model;

import com.google.gwt.core.client.JsArrayString;

public class ConfigQueryAwData extends QueryAwData {
    protected ConfigQueryAwData() {};
    
    // Grab the data
    public final native String getUserRole() /*-{ return this.user_role; }-*/;
    public final native String getConfigurationXML() /*-{ return this.configuration; }-*/;
    public final native JsArrayString getUserList() /*-{ return this.user_list; }-*/;
    public final native JsArrayString getSpecialId() /*-{ return this.special_id; }-*/;
    
    // Create an AuthorizationTokenQueryAwData from a JSON string
    public static native ConfigQueryAwData fromJsonString(String jsonString) /*-{
        return eval('(' + jsonString + ')'); 
    }-*/;
}
