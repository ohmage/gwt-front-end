package edu.ucla.cens.AndWellnessVisualizations.client.model;

import com.google.gwt.core.client.JavaScriptObject;


/**
 * A single data point returned from the AndWellness data_point query API.
 * 
 * @author jhicks
 *
 */
public class ChunkedMobilityAwData extends JavaScriptObject {
    protected ChunkedMobilityAwData() {};
    
    // Standard JSON overlays
    public final native MobilityListAwData getMode() /*-{ return this.v; }-*/;
    public final native int getDuration() /*-{ return this.d; }-*/;
    public final native String getTimeStamp() /*-{ return this.ts; }-*/;
    public final native String getTz() /*-{ return this.tz; }-*/;
    public final native String getLocationStatus() /*-{ return this.ls; }-*/;
    public final native MobilityLocationAwData getLocation() /*-{ return this.l; }-*/;
}
