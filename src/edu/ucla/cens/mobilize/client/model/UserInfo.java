package edu.ucla.cens.mobilize.client.model;

import java.util.ArrayList;
import java.util.List;

import edu.ucla.cens.mobilize.client.common.UserRole;

public class UserInfo {
  private String userName;
  private boolean canCreate = false;
  
  private List<String> adminCampaigns = new ArrayList<String>();
  private List<String> supervisorCampaigns = new ArrayList<String>();
  private List<String> authorCampaigns = new ArrayList<String>();
  private List<String> participantCampaigns = new ArrayList<String>();
  private List<String> analystCampaigns = new ArrayList<String>();
  
  private List<String> classes = new ArrayList<String>(); // user groups
  private List<String> visibleUsers = new ArrayList<String>(); // whose info this user can see
  
  private UserStats stats = new UserStats();
  
  public UserInfo(String username, 
                  boolean canCreate, 
                  List<CampaignDetailedInfo> campaigns,
                  List<String> classes) {
    this.userName = username;
    this.canCreate = canCreate;
    
    this.visibleUsers.add(this.userName); // most users can only see themselves
    
    if (classes != null) this.classes.addAll(classes);
    
    if (campaigns != null) {
      // generate user counts and list of user roles from campaigns
      for (CampaignDetailedInfo ci : campaigns) {
        List<UserRole> roles = ci.getUserRoles();
        if (ci.isActive()) {
          for (UserRole role : roles) {
            this.stats.incrementActiveCount(role);
          }
        }
        if (roles.contains(UserRole.ADMIN)) this.adminCampaigns.add(ci.getCampaignId());
        if (roles.contains(UserRole.ANALYST)) this.analystCampaigns.add(ci.getCampaignId());
        if (roles.contains(UserRole.AUTHOR)) this.authorCampaigns.add(ci.getCampaignId());
        if (roles.contains(UserRole.PARTICIPANT)) this.participantCampaigns.add(ci.getCampaignId());
        if (roles.contains(UserRole.SUPERVISOR)) this.supervisorCampaigns.add(ci.getCampaignId());
      }
    }
    // NOTE: in the future when a user might have 100+ campaigns, we may want
    // to generate these counts on the server and pass them on login
    
  }
  
  /******** GETTERS ********/
 
  public String getUserName() {
    return this.userName != null ? this.userName : "invalid username";
  }
  
  public UserStats getStats() {
    return this.stats;
  }
  
  public List<String> getClasses() {
    List<String> retval = new ArrayList<String>();
    retval.addAll(this.classes);
    return retval;  // read only list
  }
  
  // gets list of users whose data this user is allowed to see
  public List<String> getVisibleUsers() {
    List<String> retval = new ArrayList<String>();
    retval.addAll(this.visibleUsers);
    return retval;
  }
  
  public List<String> getParticipantCampaigns() {
    List<String> retval = new ArrayList<String>();
    retval.addAll(this.participantCampaigns);
    return retval;
  }
  
  /******* USER ROLES ******/
  
  public boolean isAdmin(String campaignId) {
    return this.adminCampaigns.contains(campaignId);
  }
  
  public boolean isSupervisor(String campaignId) {
    return this.supervisorCampaigns.contains(campaignId);
  }
  
  public boolean isAuthor(String campaignId) {
    return this.authorCampaigns.contains(campaignId);
  }
  
  public boolean isParticipant(String campaignId) {
    return this.participantCampaigns.contains(campaignId);
  }
  
  public boolean isAnalyst(String campaignId) {
    return this.analystCampaigns.contains(campaignId);
  }

  /********** PERMISSIONS ***********/
  
  public boolean canCreate() {
    return this.canCreate;
  }

  public boolean canUpload() {
    return !this.adminCampaigns.isEmpty() ||
           !this.supervisorCampaigns.isEmpty() || 
           !this.analystCampaigns.isEmpty();
  }
  
  public boolean canEdit(String campaignId) {
    return this.adminCampaigns.contains(campaignId) || 
           this.supervisorCampaigns.contains(campaignId) || 
           this.authorCampaigns.contains(campaignId);
  }
  
  public boolean canDelete(String campaignId) {
    return this.adminCampaigns.contains(campaignId) ||
           this.supervisorCampaigns.contains(campaignId) ||
           this.authorCampaigns.contains(campaignId);
  }
}
