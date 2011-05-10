package edu.ucla.cens.mobilize.client.common;

public enum RunningState {
  STOPPED,
  RUNNING;
  
  public String toServerString() {
    return this.toString().toLowerCase();
  }
}
