package edu.ucla.cens.mobilize.client.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ucla.cens.mobilize.client.common.RoleCampaign;

/**
 * Info about the logged in user. 
 * @author shlurbee
 */
public class UserInfo {
  private String userName; // login id
  private String email;
  boolean isPrivileged = false; // true if user is privileged member of any class
  boolean isAdmin = false; // true if user is admin (can view/edit all classes and users)
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
  
  public String getEmail() {
	  return (this.email != null) ? this.email : ""; 
  }
  
  public void setEmail(String email) {
	  this.email = email;
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

  // returns copy to prevent accidental mutate
  public Map<String, String> getCampaigns() {
    Map<String, String> copy = new HashMap<String, String>();
    for (String campaignId : this.campaignIdToNameMap.keySet()) {
      copy.put(campaignId, this.campaignIdToNameMap.get(campaignId));
    }
    return copy;
  }
  
  /**
   * Convenience method to get campaign name from user campaign info. Only works for 
   * campaigns to which the user belongs.
   * TODO: cache this data in dataservice instead
   * @param campaignUrn
   * @return Name of campaign with id campaignUrn or empty string if not found in user data
   */
  public String getCampaignName(String campaignUrn) {
    String retval = "";
    for (String campaignId : this.campaignIdToNameMap.keySet()) {
      if (campaignId.equals(campaignUrn)) {
        retval = this.campaignIdToNameMap.get(campaignId);
        break;
      }
    }
    return retval;
  }
  
  public void setCampaigns(Map<String, String> campaignIdToNameMap) {
    this.campaignIdToNameMap = campaignIdToNameMap;
  }
  
  // convenience method
  public Set<String> getCampaignIds() {
    return this.campaignIdToNameMap.keySet();
  }

  public void setAdminFlag(boolean isAdmin) {
    this.isAdmin = isAdmin;
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
  
  public boolean isAdmin() {
    return this.isAdmin;
  }
  
  public boolean isSupervisor() {
    return this.campaignRoles.contains(RoleCampaign.SUPERVISOR);
  }
  
  public boolean isAuthor() {
    return this.campaignRoles.contains(RoleCampaign.AUTHOR);
  }
  
  public boolean isParticipant() {
    return this.campaignRoles.contains(RoleCampaign.PARTICIPANT);
  }
  
  public boolean isAnalyst() {
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
