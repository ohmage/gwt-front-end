package edu.ucla.cens.mobilize.client.common;

/**
 * Helper class for keeping track of history tokens.
 * 
 * (A history token is a GWT construct that lets you save states of your
 *   program and navigate between them as you would with a url.)
 *   
 * @author vhajdik
 *
 */
public class HistoryTokens {
  public static String campaignList() {
    return "campaigns";
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

  public static String classList() {
    return "classes";
  }
  
  public static String classDetail(String classId) {
    return "classes?v=detail&id=" + classId;
  }
  
  public static String classEdit(String classId) {
    return "classes?v=edit&id=" + classId;
  }

  public static String documentList() {
    return "documents";
  }
  
  public static String documentDetail(String documentId) {
    return "documents?v=detail&id=" + documentId;
  }

  public static String documentEdit(String documentId) {
    return "documents?v=edit&id=" + documentId;
  }

  public static String documentCreate() {
    return "documents?v=create";
  }
  
}
