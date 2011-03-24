package edu.ucla.cens.AndWellnessVisualizations.client.model;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Represents location data from the AndWellness server.
 * 
 * @author jhicks
 *
 */
public class MobilityListAwData extends JavaScriptObject {
    protected MobilityListAwData() {};
    
    public final Map<String, Integer> getModes() {
    	Map<String, Integer> toReturn = new HashMap<String, Integer>();
    	
    	// Super specific, but we are in the data model so we can be
    	toReturn.put("none", getUnknown());
    	toReturn.put("still", getStill());
    	toReturn.put("walk", getWalk());
    	toReturn.put("run", getRun());
    	toReturn.put("bike", getBike());
    	toReturn.put("drive", getDrive());
    	
    	return toReturn;
    }
    
    public final native int getUnknown() /*-{
    	if (this.unknown) 
    		return this.unknown;
    	else
    	 	return 0;
    }-*/;
    
    public final native int getStill() /*-{
    	if (this.still) 
    		return this.still;
    	else
    	 	return 0;
    }-*/;
    public final native int getWalk() /*-{ 
    	if (this.walk)
    		return this.walk; 
    	else
    		return 0;
    }-*/;
    public final native int getRun() /*-{ 
    	if (this.run)
    		return this.run;
    	else
    	 	return 0;
    }-*/;
    public final native int getBike() /*-{
    	if (this.bike) 
    		return this.bike;
    	else
    	 	return 0;
    }-*/;
    public final native int getDrive() /*-{
    	if (this.drive) 
    		return this.drive;
    	else
    	 	return 0;
    }-*/;
}
