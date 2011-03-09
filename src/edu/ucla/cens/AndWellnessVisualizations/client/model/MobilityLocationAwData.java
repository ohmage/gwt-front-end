package edu.ucla.cens.AndWellnessVisualizations.client.model;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Represents location data from the AndWellness server.
 * 
 * @author jhicks
 *
 */
public class MobilityLocationAwData extends JavaScriptObject {
    protected MobilityLocationAwData() {};
    
    public final native double getLatitude() /*-{ return this.la; }-*/;
    public final native double getLongitude() /*-{ return this.lo; }-*/;
}
