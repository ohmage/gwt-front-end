package edu.ucla.cens.mobilize.client.common;

public enum RoleDocument {
  READER, WRITER, OWNER;
  
  public String toUserFriendlyString() {
    return this.toString().substring(0, 1).concat(this.toString().substring(1).toLowerCase());
  }

  public String toServerString() {
    return this.toString().toLowerCase();
  }
}
