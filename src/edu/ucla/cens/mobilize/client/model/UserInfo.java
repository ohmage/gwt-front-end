package edu.ucla.cens.mobilize.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ucla.cens.mobilize.client.common.RoleCampaign;
import edu.ucla.cens.mobilize.client.common.UserRoles;
import edu.ucla.cens.mobilize.client.common.UserStats;

public class UserInfo {
  private String userName; // login id
  private boolean canCreate = false; // if true, user can create a new campaign
  private UserRoles roles = new UserRoles(); // flags showing what roles are held by user
  private List<String> visibleUsers = new ArrayList<String>(); // whose info this user can see
  private Map<String, String> classIdToNameMap;
  
  private String msgInfo;
  private boolean hasUnreadInfoMsg = false;
  private String msgError;
  private boolean hasUnreadErrorMsg = false;
  
  private UserStats stats = new UserStats();
  
  public UserInfo(String username, 
                  boolean canCreate, 
                  Map<String, String> classIdToNameMap,
                  List<RoleCampaign> roles) {
    this.userName = username;
    this.canCreate = canCreate;

    this.visibleUsers.add(this.userName); // most users can only see themselves
    
    // FIXME: user info service should also return list of users visible to this one
    
    this.classIdToNameMap = classIdToNameMap;

    for (RoleCampaign role : roles) {
      this.roles.addRole(role);
    }    
  }
  
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
  
  /******** GETTERS ********/
 
  public String getUserName() {
    return this.userName != null ? this.userName : "invalid username";
  }
  
  public UserStats getStats() {
    return this.stats;
  }
  
  public Map<String, String> getClasses() {
    return this.classIdToNameMap;
  }
  
  public Set<String> getClassIds() {
    return this.classIdToNameMap.keySet();
  }
  
  // gets list of users whose data this user is allowed to see
  public List<String> getVisibleUsers() {
    return this.visibleUsers;
  }
  
  /******* USER ROLES ******/
  
  public boolean isAdmin(String campaignId) {
    return this.roles.admin;
  }
  
  public boolean isSupervisor(String campaignId) {
    return this.roles.supervisor;
  }
  
  public boolean isAuthor(String campaignId) {
    return this.roles.author;
  }
  
  public boolean isParticipant(String campaignId) {
    return this.roles.participant;
  }
  
  public boolean isAnalyst(String campaignId) {
    return this.roles.analyst;
  }

  /********** PERMISSIONS ***********/
  
  public boolean canCreate() {
    return this.canCreate;
  }

  public boolean canUpload() {
    return this.roles.admin || this.roles.supervisor || this.roles.analyst;
  }
  
}
