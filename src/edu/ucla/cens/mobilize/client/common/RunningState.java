package edu.ucla.cens.mobilize.client.common;

public enum RunningState {
  STOPPED,
  RUNNING;
  
  public String toServerString() {
    return this.toString().toLowerCase();
  }
  
  public String toUserFriendlyString() { // first char uppercase, others lower
    return this.toString().substring(0, 1).concat(this.toString().substring(1).toLowerCase());
  }
}
