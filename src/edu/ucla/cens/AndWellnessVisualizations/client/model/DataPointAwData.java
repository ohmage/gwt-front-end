package edu.ucla.cens.AndWellnessVisualizations.client.model;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A single data point returned from the AndWellness data_point query API.
 * 
 * @author jhicks
 *
 */
public class DataPointAwData extends JavaScriptObject {
    protected DataPointAwData() {};
    
    public final native String getLabel() /*-{ return this.label; }-*/;
    public final native String getValue() /*-{ return this.value.toString(); }-*/;
    public final native String getUnit() /*-{ return this.unit; }-*/;
    public final native String getTimeStamp() /*-{ return this.timestamp; }-*/;
    public final native String getTz() /*-{ return this.tz; }-*/;
    public final native LocationAwData getLocation() /*-{ return this.location; }-*/;
    public final native String getText() /*-{ return this.text; }-*/;
    public final native String getImage() /*-{ return this.image; }-*/;
    public final native String getType() /*-{ return this.type; }-*/;
}
