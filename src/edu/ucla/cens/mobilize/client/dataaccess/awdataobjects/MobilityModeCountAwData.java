package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;

public class MobilityModeCountAwData extends JavaScriptObject {
	protected MobilityModeCountAwData() {};
	
	public final Map<String, Integer> getMobilityModeCount() {
		Map<String, Integer> toReturn = new HashMap<String, Integer>();
		
		toReturn.put("ERROR", getErrorCount());
		toReturn.put("STILL", getStillCount());
		toReturn.put("WALK", getWalkCount());
		toReturn.put("RUN", getRunCount());
		toReturn.put("BIKE", getBikeCount());
		toReturn.put("DRIVE", getDriveCount());
		
		return toReturn;
	}
	
	public final native int getStillCount() /*-{
		if (typeof this.still === 'undefined')
			return 0;
		else
			return this.still;
	}-*/;
	
	public final native int getWalkCount() /*-{
		if (typeof this.walk === 'undefined')
			return 0;
		else
			return this.walk;
	}-*/;
	
	public final native int getRunCount() /*-{
		if (typeof this.run === 'undefined')
			return 0;
		else
			return this.run;
	}-*/;
	
	public final native int getBikeCount() /*-{
		if (typeof this.bike === 'undefined')
			return 0;
		else
			return this.bike;
	}-*/;
	
	public final native int getDriveCount() /*-{
		if (typeof this.drive === 'undefined')
			return 0;
		else
			return this.drive;
	}-*/;
	
	public final native int getErrorCount() /*-{
		if (typeof this.error === 'undefined')
			return 0;
		else
			return this.error;
	}-*/;
}
