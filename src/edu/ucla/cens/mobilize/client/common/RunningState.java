package edu.ucla.cens.mobilize.client.common;

public enum RunningState {
  STOPPED,
  RUNNING;
  
  /**
   * @return Running state string in format recognized by server and history tokens
   */
  public String toServerString() {
    return this.toString().toLowerCase();
  }
  
  /**
   * @return First char upper, other chars lower
   */
  public String toUserFriendlyString() { // first char uppercase, others lower
    return this.toString().substring(0, 1).concat(this.toString().substring(1).toLowerCase());
  }

  /**
   * @param runningStateStringInServerFormat
   * @return null if string doesn't match any known state
   */
  public static RunningState fromServerString(String runningStateStringInServerFormat) {
    RunningState retval = null;
    try {
      retval = RunningState.valueOf(runningStateStringInServerFormat.toUpperCase());
    } catch (Exception e) { } // leave it as null
    return retval;
  }
}
