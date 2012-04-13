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
  public static String login() {
    return "login";
  }
  
  public static String register() {
    return "register";
  }
  
  public static String reset_password() {
    return "reset_password";
  }
  
  public static String activate() {
    return "activate";
  }
  
  public static String logout() {
    return "logout";
  }
  
  public static String admin() {
    return "admin";
  }
  
  public static String auditLog() {
    return "admin_audit";
  }
  
  public static String auditLog(Date date, 
                                String uri, 
                                Boolean onlyFailures) {
    Map<String, String> params = new HashMap<String, String>();
    if (date != null) params.put("date", DateUtils.translateToHistoryTokenFormat(date));
    if (uri != null) params.put("uri", uri);
    if (onlyFailures == true) params.put("fail", "1");
    String token = auditLog();
    if (!params.isEmpty()) token += "?" + MapUtils.translateToParameters(params);
    return token;
  }
  
  public static String adminClassList() {
    return "admin_class_list";
  }
  
  public static String adminClassList(String className, String classMember, String classUrn) {
    Map<String, String> params = new HashMap<String, String>();
    if (className != null && !className.isEmpty()) {
      params.put("class", className);
    }
    if (classMember != null && !classMember.isEmpty()) {
      params.put("member", classMember);
    }
    if (classUrn != null && !classUrn.isEmpty()) {
      params.put("urn", classUrn);
    }
    return "admin_class_list?" + MapUtils.translateToParameters(params);
  }
  
  public static String adminClassDetail(String class_id) {
    return "admin_class_detail?cid=" + class_id;
  }
  
  public static String adminClassCreate() {
    return "admin_class_create";
  }
  
  public static String adminClassEdit(String class_id) {
    return "admin_class_edit?cid=" + class_id;
  }

  
  public static String adminUserList() {
    return "admin_user_list";
  }
  
  public static String adminUserList(String username,
                                     String personalId,
                                     Boolean isEnabled,
                                     Boolean canCreateCampaigns,
                                     Boolean isAdmin,
                                     String firstName,
                                     String lastName,
                                     String email,
                                     String organization,
                                     int startIndex,
                                     int pageSize) {
    Map<String, String> params = new HashMap<String, String>();
    if (username != null && !username.isEmpty()) {
      params.put("username", username);
    }
    if (personalId != null && !personalId.isEmpty()) {
      params.put("pid", personalId);
    }
    if (isEnabled != null) {
      params.put("enabled", isEnabled ? "true" : "false");
    }
    if (canCreateCampaigns != null) {
      params.put("can_create", canCreateCampaigns ? "true" : "false");
    }
    if (isAdmin != null) {
      params.put("admin", isAdmin ? "true" : "false");
    }
    if (firstName != null && !firstName.isEmpty()) {
      params.put("first_name", firstName);
    }
    if (lastName != null && !lastName.isEmpty()) {
      params.put("last_name", lastName);
    }
    if (email != null && !email.isEmpty()) { 
      params.put("email", email);
    }
    if (organization != null && !organization.isEmpty()) {
      params.put("organization", organization);
    }
    params.put("start_index", Integer.toString(startIndex));
    params.put("page_size", Integer.toString(pageSize));
    return "admin_user_list?" + MapUtils.translateToParameters(params);
  }
  
  public static String adminUserDetail(String user_id) {
    return "admin_user_detail?uid=" + user_id;
  }
  
  public static String adminUserCreate() {
    return "admin_user_create";
  }
  
  public static String adminUserEdit(String user_id) {
    return "admin_user_edit?uid=" + user_id;
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
                                   String survey,
                                   String classId,
                                   String participant,
                                   String promptX,
                                   String promptY,
                                   Date fromDate,
                                   Date toDate) {
    Map<String, String> params = new HashMap<String, String>();
    if (plotType != null) params.put("plot", plotType.toHistoryTokenString());
    if (campaign != null) params.put("cid", campaign);
    if (classId != null) params.put("classid", classId);
    if (survey != null) params.put("sid", survey);
    if (participant != null) params.put("uid", participant);
    if (promptX != null) params.put("x", promptX);
    if (promptY != null) params.put("y", promptY);
    if (fromDate != null) params.put("from", DateUtils.translateToHistoryTokenFormat(fromDate));
    if (toDate != null) params.put("to", DateUtils.translateToHistoryTokenFormat(toDate));
    return params.isEmpty() ? "explore_data" : "explore_data?" + MapUtils.translateToParameters(params);
  }
  
  public static String responseList() {
    return "responses";
  }
  
  public static String responseList(String view, // "edit" or "browse"
                                    String participant,
                                    String campaign,
                                    String survey,
                                    Privacy privacy,
                                    boolean onlyPhotoResponses,
                                    Date startDate,
                                    Date endDate,
                                    Integer startIndex,
                                    Integer pageSize) {
    Map<String, String> params = new HashMap<String, String>();
    if (view != null) params.put("v", view);
    if (participant != null) params.put("uid", participant);
    if (campaign != null) params.put("cid", campaign);
    if (survey != null && !survey.isEmpty()) params.put("sid", survey);
    if (privacy != null && !privacy.equals(Privacy.UNDEFINED)) params.put("privacy", privacy.toServerString());
    if (onlyPhotoResponses) params.put("photo", "true"); 
    if (startDate != null) params.put("from", DateUtils.translateToHistoryTokenFormat(startDate));
    if (endDate != null) params.put("to", DateUtils.translateToHistoryTokenFormat(endDate));
    if (startIndex != null) params.put("start", Integer.toString(Math.max(startIndex, 0))); // don't let index be negative
    if (pageSize != null) params.put("page_size", Integer.toString(pageSize)); 
    return params.isEmpty() ? "responses" : "responses?" + MapUtils.translateToParameters(params);
  }
  
}
