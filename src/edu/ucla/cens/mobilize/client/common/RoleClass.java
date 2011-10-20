package edu.ucla.cens.mobilize.client.common;

public enum RoleClass {
  UNRECOGNIZED, PRIVILEGED, RESTRICTED;
  
  public String toUserFriendlyString() {
    return this.toString().substring(0, 1).concat(this.toString().substring(1).toLowerCase());
  }

  public String toServerString() {
    return this.toString().toLowerCase();
  }
  
  public static RoleClass fromServerString(String roleStringInServerFormat) {
    RoleClass retval = null;
    try {
      retval = RoleClass.valueOf(roleStringInServerFormat.toUpperCase());
    } catch (Exception e) { 
      retval = UNRECOGNIZED;
    }
    return retval;
  }
}
