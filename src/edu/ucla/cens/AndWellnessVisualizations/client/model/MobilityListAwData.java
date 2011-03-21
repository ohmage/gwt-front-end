package edu.ucla.cens.AndWellnessVisualizations.client.model;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Represents location data from the AndWellness server.
 * 
 * @author jhicks
 *
 */
public class MobilityListAwData extends JavaScriptObject {
    protected MobilityListAwData() {};
    
    public final native int getStill() /*-{ return this.still; }-*/;
    public final native int getWalk() /*-{ return this.walk; }-*/;
    public final native int getRun() /*-{ return this.run; }-*/;
    public final native int getBike() /*-{ return this.bike; }-*/;
    public final native int getDrive() /*-{ return this.drive; }-*/;
}
