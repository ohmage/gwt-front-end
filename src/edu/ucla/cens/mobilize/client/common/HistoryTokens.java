package edu.ucla.cens.mobilize.client.common;

public class HistoryTokens {
  public static String campaignList() {
    return "campaign";
  }
  
  public static String campaignDetail(String campaignId) {
    return "campaigns?v=detail&id=" + campaignId;
  }
  
  public static String campaignEdit(String campaignId) {
    return "campaigns?v=edit&id=" + campaignId;
  }
  
  public static String campaignAnalyze(String campaignId) {
    return "explore_data?cid=" + campaignId;
  }
}
