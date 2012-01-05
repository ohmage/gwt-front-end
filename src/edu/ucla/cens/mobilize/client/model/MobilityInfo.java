package edu.ucla.cens.mobilize.client.model;

import java.util.Date;

import edu.ucla.cens.mobilize.client.common.LocationStatus;
import edu.ucla.cens.mobilize.client.common.MobilityMode;

public class MobilityInfo {
	MobilityMode m;		//mode
	Date ts;			//timestamp
	String tz;			//timezone
	LocationStatus l;	//location status
	
	//*** these are only available if LocationStatus != UNAVAILABLE
	double la;			//latitude
	double lo;			//longitude
	float ac;			//accuracy
	String pr;			//provider
	Date l_ts;			//location timestamp
	
	public MobilityMode getMode() {
		return this.m;
	}
	
	public void setMode(MobilityMode mode) {
		this.m = mode;
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
