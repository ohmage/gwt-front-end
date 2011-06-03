package edu.ucla.cens.mobilize.client.model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ucla.cens.mobilize.client.common.RoleCampaign;

public class UserInfo {
  private String userName; // login id
  boolean isPrivileged = false; // true if user is privileged member of any class
  boolean canCreateCampaigns = false; // true if user is allowed to author campaigns
  private Map<String, String> classIdToNameMap;
  private Set<RoleCampaign> campaignRoles = new HashSet<RoleCampaign>();
  private Map<String, String> campaignIdToNameMap;
  
  private String msgInfo;
  private boolean hasUnreadInfoMsg = false;
  private String msgError;
  private boolean hasUnreadErrorMsg = false;
  
  /******** MESSAGING ********/
  
  public boolean hasInfoMessage() { return this.hasUnreadInfoMsg; }
  public boolean hasErrorMessage() { return this.hasUnreadErrorMsg; }
  
  public String getInfoMessage() { return this.msgInfo; }
  public String getErrorMessage() { return this.msgError; }
  
  public void setInfoMessage(String message) {
    this.msgInfo = message;
    this.hasUnreadInfoMsg = true;
  }
  
  public void setErrorMessage(String error) {
    this.msgError = error;
    this.hasUnreadErrorMsg = true;
  }
  
  public void clearMessages() {
    this.msgInfo = this.msgError = null;
    this.hasUnreadInfoMsg = this.hasUnreadErrorMsg = false;
  }
  
  /******** GETTERS AND SETTERS ********/
 
  public String getUserName() {
    return this.userName != null ? this.userName : "invalid username";
  }
  
  public void setUserName(String userName) {
    this.userName = userName;
  }
  
  public Map<String, String> getClasses() {
    return this.classIdToNameMap;
  }
  
  public void setClasses(Map<String, String> classIdToNameMap) {
    this.classIdToNameMap = classIdToNameMap;
  }

  // convenience method
  public Set<String> getClassIds() {
    return this.classIdToNameMap.keySet();
  }
  
  public Map<String, String> getCampaigns() {
    return this.campaignIdToNameMap;
  }
  
  public void setCampaigns(Map<String, String> campaignIdToNameMap) {
    this.campaignIdToNameMap = campaignIdToNameMap;
  }
  
  // convenience method
  public Set<String> getCampaignIds() {
    return this.campaignIdToNameMap.keySet();
  }

  public void setPrivilegeFlag(boolean isPrivileged) {
    this.isPrivileged = isPrivileged;
  }
  
  public void setCanCreateFlag(boolean canCreateCampaigns) {
    this.canCreateCampaigns = canCreateCampaigns;
  }
  
  public void setCampaignRoles(List<RoleCampaign> campaignRoles) {
    this.campaignRoles.clear();
    this.campaignRoles.addAll(campaignRoles);
  }
  
  /******* USER ROLES ******/
  
  public boolean isAdmin(String campaignId) {
    return this.campaignRoles.contains(RoleCampaign.ADMIN);
  }
  
  public boolean isSupervisor(String campaignId) {
    return this.campaignRoles.contains(RoleCampaign.SUPERVISOR);
  }
  
  public boolean isAuthor(String campaignId) {
    return this.campaignRoles.contains(RoleCampaign.AUTHOR);
  }
  
  public boolean isParticipant(String campaignId) {
    return this.campaignRoles.contains(RoleCampaign.PARTICIPANT);
  }
  
  public boolean isAnalyst(String campaignId) {
    return this.campaignRoles.contains(RoleCampaign.ANALYST);
  }
  
  public boolean isPrivileged() {
    return this.isPrivileged;
  }

  /********** PERMISSIONS ***********/
  
  public boolean canCreate() {
    return this.canCreateCampaigns;
  }

  public boolean canUpload() {
    return this.campaignRoles.contains(RoleCampaign.ADMIN) ||
           this.campaignRoles.contains(RoleCampaign.SUPERVISOR) || 
           this.campaignRoles.contains(RoleCampaign.ANALYST);
  }
  
}
