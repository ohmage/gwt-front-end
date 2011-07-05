package edu.ucla.cens.mobilize.client.common;

public enum PlotType {
  SURVEY_RESPONSE_COUNT,
  USER_TIMESERIES,
  PROMPT_TIMESERIES,
  PROMPT_DISTRIBUTION,
  SCATTERPLOT,
  DENSITY_PLOT,
  MAP;
  
  public String toHistoryTokenString() {
    return this.toString().toLowerCase();
  }
  
  public static PlotType fromHistoryTokenString(String str) {
    PlotType retval = null;
    try {
      retval = PlotType.valueOf(str.toUpperCase());
    } catch (Exception e) {}
    return retval; // null if unrecognized
  }
}
