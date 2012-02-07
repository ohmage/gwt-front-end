package edu.ucla.cens.mobilize.client.common;

import edu.ucla.cens.mobilize.client.utils.StringUtils;

public enum ResponseStatus {
  SUCCESS, 
  FAILURE;
  
  public String toServerString() {
    return this.toString().toLowerCase();
  }
  
  public static ResponseStatus fromServerString(String serverString) {
    return ResponseStatus.valueOf(serverString.toUpperCase());
  }
  
  public String toUserFriendlyString() {
    return StringUtils.capitalize(this.toString().toLowerCase());
  }
}
