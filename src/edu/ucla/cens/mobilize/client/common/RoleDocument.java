package edu.ucla.cens.mobilize.client.common;

public enum RoleDocument {
  UNRECOGNIZED_OR_NONE, READER, WRITER, OWNER;
  
  public String toUserFriendlyString() {
    return this.toString().substring(0, 1).concat(this.toString().substring(1).toLowerCase());
  }

  public String toServerString() {
    return this.toString().toLowerCase();
  }
  
  public static RoleDocument fromServerString(String serverString) {
    RoleDocument retval = null;
    try {
      // assumes server string is same as enum name but lowercase (reader, writer, owner)
      retval = RoleDocument.valueOf(serverString.trim().toUpperCase());
    } catch (Exception e) {
      retval = RoleDocument.UNRECOGNIZED_OR_NONE;
    }
    return retval;
  }
}
