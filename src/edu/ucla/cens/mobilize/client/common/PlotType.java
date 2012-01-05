package edu.ucla.cens.mobilize.client.common;

public enum PlotType {
  SURVEY_RESPONSE_COUNT, // total count
  SURVEY_RESPONSES_PRIVACY_STATE, // responses by privacy
  SURVEY_RESPONSES_PRIVACY_STATE_TIME, // responses timeseries
  LEADER_BOARD,
  USER_TIMESERIES,
  PROMPT_TIMESERIES,
  PROMPT_DISTRIBUTION,
  SCATTER_PLOT,
  DENSITY_PLOT,
  MAP,
  MOBILITY_MAP,
  MOBILITY_GRAPH;
  
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
  
  // special cases 2d_density b/c can't have an enum that starts with a number
  public String toServerString() {
    String retval = null;
    if (this.equals(DENSITY_PLOT)) {
      retval = "2d_density";
    } else {
      retval = this.toString().toLowerCase();
    }
    return retval;
  }
  
  public static PlotType fromServerString(String plotTypeInServerFormat) {
    PlotType plotType = null;
    if ("2d_density".equals(plotTypeInServerFormat)) {
      plotType = DENSITY_PLOT;
    } else {
      try {
        plotType = valueOf(plotTypeInServerFormat);
      } catch (Exception e) {} // will return null
    }
    return plotType;
  }
}
