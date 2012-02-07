package edu.ucla.cens.mobilize.client.common;

import edu.ucla.cens.mobilize.client.utils.StringUtils;

public enum RequestType {
  GET, 
  POST;
  
  public String toServerString() { 
    return this.toString().toLowerCase(); 
  }
  
  public static RequestType fromServerString(String serverString) {
    return RequestType.valueOf(serverString.toUpperCase());
  }

  public String toUserFriendlyString() {
    return StringUtils.capitalize(this.toString().toLowerCase());
  }
}
