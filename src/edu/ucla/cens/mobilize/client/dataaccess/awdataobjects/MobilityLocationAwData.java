package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import com.google.gwt.core.client.JavaScriptObject;

public class MobilityLocationAwData extends JavaScriptObject {
	protected MobilityLocationAwData() {};
	
	//user should make sure ls != "UNAVAILABLE" before calling these functions
	public final native double getLatitude() /*-{ return this.la; }-*/;
	public final native double getLongitude() /*-{ return this.lo; }-*/;
	public final native String getProvider() /*-{ return this.pr; }-*/;
	public final native double getTimeRaw() /*-{ return this.t; }-*/;	//needs to be cast to (long)
	public final long getTime() { return (long)getTimeRaw(); }
	public final native float getAccuracy() /*-{ return this.ac; }-*/;
}
