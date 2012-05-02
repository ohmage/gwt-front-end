package edu.ucla.cens.mobilize.client.common;

public enum LocationStatus {
  UNAVAILABLE,
  VALID,
  STALE,
  INACCURATE,
  NETWORK;
  
  public static LocationStatus fromServerString(String statusInServerFormat) {
    LocationStatus status = null;
    try {
      status = LocationStatus.valueOf(statusInServerFormat.toUpperCase());
    } catch (Exception e) { // invalid location is returned as unavailable
      status = LocationStatus.UNAVAILABLE;
    }
    return status;
  }
}
