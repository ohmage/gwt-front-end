package edu.ucla.cens.mobilize.client.common;

public enum MobilityMode {
	STILL,
	WALK,
	RUN,
	BIKE,
	DRIVE,
	ERROR;	//new in AW 2.9
	
	public static MobilityMode fromServerString(String statusInServerFormat) {
		MobilityMode status = null;
		try {
			status = MobilityMode.valueOf(statusInServerFormat.toUpperCase());
		} catch (Exception e) { // invalid mode is returned as ERROR
			status = MobilityMode.ERROR;
		}
		return status;
	}
}
