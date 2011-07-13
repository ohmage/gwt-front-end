package edu.ucla.cens.mobilize.client.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.ucla.cens.mobilize.client.utils.DateUtils;
import edu.ucla.cens.mobilize.client.utils.MapUtils;

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
  public static String logout() {
    return "logout";
  }
  
  public static String campaignList() {
    return "campaigns";
  }
  
  /**
   * Set missing values to null
   * @param runningState
   * @param userRole
   * @param fromDate
   * @param toDate
   * @return String History token for filtered campaign list
   */
  public static String campaignList(RunningState runningState,
                                    RoleCampaign userRole,
                                    Date fromDate,
                                    Date toDate) {
    Map<String, String> params = new HashMap<String, String>();
    if (runningState != null) params.put("state", runningState.toServerString());
    if (userRole != null) params.put("role", userRole.toServerString());
    if (fromDate != null) params.put("from", DateUtils.translateToHistoryTokenFormat(fromDate));
    if (toDate != null) params.put("to", DateUtils.translateToHistoryTokenFormat(toDate));
    return params.isEmpty() ? "campaigns" : "campaigns?v=list&" + MapUtils.translateToParameters(params);
  }
  
  public static String campaignDetail(String campaignId) {
    return "campaigns?v=detail&id=" + campaignId;
  }

  public static String campaignCreate() {
    return "campaigns?v=create";
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
  
  public static String dashboard() {
    return "dashboard";
  }

  public static String documentListAll() {
    return "documents?v=all";
  }
  
  public static String documentListMy() {
    return "documents?v=my";
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
  
  public static String exploreData() {
    return "explore_data";
  }
  
  public static String exploreData(PlotType plotType,
                                   String campaign,
                                   String participant,
                                   String promptX,
                                   String promptY) {
    Map<String, String> params = new HashMap<String, String>();
    if (plotType != null) params.put("plot", plotType.toHistoryTokenString());
    if (campaign != null) params.put("cid", campaign);
    if (participant != null) params.put("uid", participant);
    if (promptX != null) params.put("x", promptX);
    if (promptY != null) params.put("y", promptY);
    return params.isEmpty() ? "explore_data" : "explore_data?" + MapUtils.translateToParameters(params);
  }
  
  public static String responseList() {
    return "responses";
  }
  
  public static String responseList(String view,
                                    String participant,
                                    String campaign,
                                    String survey,
                                    Privacy privacy,
                                    boolean onlyPhotoResponses,
                                    Date startDate,
                                    Date endDate) {
    Map<String, String> params = new HashMap<String, String>();
    if (view != null) params.put("v", view);
    if (participant != null) params.put("uid", participant);
    if (campaign != null) params.put("cid", campaign);
    if (survey != null) params.put("sid", survey);
    if (privacy != null) params.put("privacy", privacy.toServerString());
    if (onlyPhotoResponses) params.put("photo", "true"); 
    if (startDate != null) params.put("from", DateUtils.translateToHistoryTokenFormat(startDate));
    if (endDate != null) params.put("to", DateUtils.translateToHistoryTokenFormat(endDate));
    return params.isEmpty() ? "responses" : "responses?" + MapUtils.translateToParameters(params);
  }
                                    

  
}
