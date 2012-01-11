package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

/*
"data":
[
    { <--- *** MobilityChunkedDataPoint JsArray
        "mc":{"still":1,"walk":2,"run":7},
        "d":600000,
        "t":39282033
        "tz":"PST",
        "ls": "valid",
        "l":{
           "la": 34.44434343,
           "lo": -43.3443343,
           "pr":"GPS",
           "t":1322720340280,
           "tz":"America/LosAngeles",
           "ac":30
        }
    },..
]
*/

public class MobilityChunkedDataPointAwData extends QueryAwData {
	protected MobilityChunkedDataPointAwData() {};
	
	public final native MobilityModeCountAwData getModesCountObj() /*-{
		return this.mc;
	}-*/;
	
	public final native int getDuration() /*-{ return this.d; }-*/;
	public final native String getTimeStamp() /*-{ return this.ts; }-*/;
	public final native double getTimeRaw() /*-{ return this.t; }-*/;
	public final long getTime() { return (long)getTimeRaw(); }
	public final native String getTimezone() /*-{ return this.tz; }-*/;
	public final native String getLocStatus() /*-{ return this.ls; }-*/;
	
	public final native MobilityLocationAwData getLocation() /*-{ return eval('(' + this.l + ')');  }-*/;
}
