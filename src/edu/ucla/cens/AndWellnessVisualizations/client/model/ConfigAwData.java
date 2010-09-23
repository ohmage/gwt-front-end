package edu.ucla.cens.AndWellnessVisualizations.client.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class ConfigAwData extends JavaScriptObject {
    protected ConfigAwData() {};
    
    public final native String getUserRole() /*-{ return this.user_role; }-*/;
    public final native String getConfigurationXML() /*-{ return this.configuration; }-*/;
    public final native JsArray<StringAwData> getUserList() /*-{ return this.user_list; }-*/;
    public final native JsArray<StringAwData> getSpecialId() /*-{ return this.special_id; }-*/;
}
