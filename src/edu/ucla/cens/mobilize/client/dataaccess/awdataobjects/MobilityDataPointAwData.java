package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/*
"result":"success",
"data":
[
    { <--- *** MobilityDataPoint JsArray ***
        "m":"still",
        "ts":"09-13-2010 12:34:56",
        "tz":"PST",
        "ls": "valid",
        "l":{
           "la": 34.44434343,
           "lo": -43.3443343,
           "pr":"GPS",
           "t":"1322720340280",
           "ac":30
        }
    },..
]
*/

public class MobilityDataPointAwData extends QueryAwData {
	protected MobilityDataPointAwData() {};
	
	public final native String getMode() /*-{ return this.m; }-*/;
	public final native String getTimeStamp() /*-{ return this.ts; }-*/;
	public final native double getTimeRaw() /*-{ return this.t; }-*/;	//needs to be cast to long
	public final long getTime() { return (long)getTimeRaw(); }
	public final native String getTimezone() /*-{ return this.tz; }-*/;
	public final native String getLocStatus() /*-{ return this.ls; }-*/;
	
	//public final native MobilityLocationAwData getLocation() /*-{ return eval('(' + this.l + ')');  }-*/;
	public final native MobilityLocationAwData getLocation() /*-{ return this.l;  }-*/;
}
