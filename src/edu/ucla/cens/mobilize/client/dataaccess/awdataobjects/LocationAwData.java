package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Represents location data from the AndWellness server.
 * 
 * @author jhicks
 *
 */
public class LocationAwData extends JavaScriptObject {
    protected LocationAwData() {};
    
    public final native String getLatitude() /*-{ return this.latitude; }-*/;
    public final native String getLongitude() /*-{ return this.longitude; }-*/;
}
