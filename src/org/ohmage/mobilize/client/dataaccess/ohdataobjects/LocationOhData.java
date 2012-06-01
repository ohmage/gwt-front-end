package org.ohmage.mobilize.client.dataaccess.ohdataobjects;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Represents location data from the Ohmage server.
 *
 * @author jhicks
 *
 */
public class LocationOhData extends JavaScriptObject {

    protected LocationOhData() {
    }

    ;
    
    public final native String getLatitude() /*
     * -{ return this.latitude; }-
     */;

    public final native String getLongitude() /*
     * -{ return this.longitude; }-
     */;
}
