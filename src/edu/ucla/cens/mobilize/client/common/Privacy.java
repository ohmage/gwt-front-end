package edu.ucla.cens.mobilize.client.common;

public enum Privacy {
  UNDEFINED,
  PRIVATE,
  SHARED,
  INVISIBLE;
  
  public String toServerString() {
    return this.toString().toLowerCase();
  }
}
