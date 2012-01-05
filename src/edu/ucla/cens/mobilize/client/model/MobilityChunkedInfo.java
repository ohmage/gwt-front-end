package edu.ucla.cens.mobilize.client.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.ucla.cens.mobilize.client.common.LocationStatus;
import edu.ucla.cens.mobilize.client.common.MobilityMode;

/*
"data":
[
    {
        "mc":{"still:1,"walk":2,"run":7},
        "d":600000,
        "ts":"09-13-2010 12:34:56",
        "tz":"PST",
        "ls": "valid",
        "l":{
           "la": 34.44434343,
           "lo": -43.3443343,
           "pr":"GPS",
           "ts":"09-13-2010 12:35:56",
           "ac":30
        }
    },..
]
*/

public class MobilityChunkedInfo {
	Map<MobilityMode, Integer> mc = new HashMap<MobilityMode, Integer>();
	Integer d;			//duration - FIXME: may need to change to Long to be safe
	Date ts;			//timestamp
	String tz;			//timezone
	LocationStatus l;	//location status
	
	//*** these are only available if LocationStatus != UNAVAILABLE
	double la;			//latitude
	double lo;			//longitude
	float ac;			//accuracy
	String pr;			//provider
	Date l_ts;			//location timestamp
	
	public Map<MobilityMode, Integer> getModeCount() {
		return this.mc;
	}
	
	public void setModeCount(Map<MobilityMode, Integer> mapping) {
		for (Map.Entry<MobilityMode, Integer> entry : mapping.entrySet()) {
			mc.put(entry.getKey(), entry.getValue());
		}
	}
	
	public int getDuration() {
		return this.d.intValue();
	}
	
	public void setDuration(int duration) {
		this.d = duration;
	}
	
	public Date getDate() {
		return this.ts;
	}
	
	public void setDate(Date timestamp) {
		this.ts = timestamp;
	}
	
	public String getTimezone() {
		return this.tz;
	}
	
	public void setTimezone(String timezone) {
		this.tz = timezone;
	}
	
	public LocationStatus getLocationStatus() {
		return this.l;
	}
	
	public void setLocationStatus(LocationStatus locationstatus) {
		this.l = locationstatus;
	}
	
	public double getLocationLat() {
		return this.la;
	}
	
	public void setLocationLat(double latitude) {
		this.la = latitude;
	}
	
	public double getLocationLong() {
		return this.lo;
	}
	
	public void setLocationLong(double longitude) {
		this.lo = longitude;
	}
	
	public float getLocationAccuracy() {
		return this.ac;
	}
	
	public void setLocationAccuracy(float accuracy) {
		this.ac = accuracy;
	}
	
	public String getLocationProvider() {
		return this.pr;
	}
	
	public void setLocationProvider(String provider) {
		this.pr = provider;
	}
	
	public Date getLocationTimestamp() {
		return this.l_ts;
	}
	
	public void setLocationTimestamp(Date loc_timestamp) {
		this.l_ts = loc_timestamp;
	}
}
