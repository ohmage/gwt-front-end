package edu.ucla.cens.AndWellnessVisualizations.client.data;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;


public class HoursSinceLastSurveyAwData extends JavaScriptObject {

    // Overlay types always have protected, zero-arg ctors
    protected HoursSinceLastSurveyAwData() { }

    // Typically, methods on overlay types are JSNI
    public final native String getTimeZone() /*-{ return this.tz; }-*/;
    public final native String getHours()  /*-{ return this.value;  }-*/;
    public final native String getUserName()  /*-{ return this.user;  }-*/;
    
    // Create a JsArray from a JSON String
    public static native JsArray<HoursSinceLastSurveyAwData> fromJSONString(String jsonString) /*-{ 
        return eval('(' + jsonString + ')'); 
    }-*/;
  }