package edu.ucla.cens.mobilize.client.model;

import java.util.Date;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.RoleCampaign;
import edu.ucla.cens.mobilize.client.common.RunningState;
import edu.ucla.cens.mobilize.client.common.UserRoles;

// NOTE: this is a read-only class
public class CampaignShortInfo {
  private String campaignId;
  private String campaignName;
  private RunningState runningState;
  private Privacy privacy;
  private UserRoles userRoles; // TODO: use list of roles instead of special object
  private Date creationTime;
  
  public CampaignShortInfo(String campaignId,
                             String campaignName,
                             RunningState runningState,
                             Privacy privacy,
                             UserRoles roles,
                             Date creationTime) {
    this.campaignId = campaignId;
    this.campaignName = campaignName;
    this.runningState = runningState;
    this.privacy = privacy;
    this.userRoles = roles;
    this.creationTime = creationTime;
  }
  
  // ******** GETTERS ********
  
  public String getCampaignId() {
    return this.campaignId;
  }
  
  public String getCampaignName() {
    return this.campaignName;
  }
  
  public Date getCreationTime() {
    return this.creationTime;
  }
  
  public RunningState getRunningState() {
    return this.runningState;
  }
  
  public Privacy getPrivacy() {
    return this.privacy;
  }
  
  public UserRoles getUserRoles() {
    return this.userRoles;
  }
  
  //******** PERMISSIONS ********
  // Make sure these stay in sync with the permissions in CampaignDetailedInfo
  // TODO: abstract them?
  
  public boolean userCanViewDetails() {
    return true; // everyone
  }
  
  public boolean userCanEdit() {
    return this.userRoles.author || this.userRoles.supervisor || this.userRoles.admin;
  }
  
  public boolean userCanDelete() {
    // User can delete campaign if he has edit permission and the campaign has
    // no responses. Front end only checks edit permission. Server will reject the
    // request if campaign has responses.
    return userCanEdit(); 
  }
  
  public boolean userCanAnalyze() {
    return this.userRoles.analyst && this.privacy.equals(Privacy.SHARED) ||
           this.userRoles.supervisor || this.userRoles.admin ||
           this.userRoles.author;
  }
  
  // used for response tab. make sure this matches permissions in detailed info
  public boolean userCanEditResponses() {
    return this.userRoles.supervisor || this.userRoles.admin;
  }
  
  // used for response tab. make sure this matches permissions in detailed info
  public boolean userCanSeeSharedResponses() {
    // user can see other users' shared responses if:
    // 1. he is admin or supervisor
    // 2. he is author of the campaign
    // 3. he is an analyst and the campaign is shared
    return this.userRoles.supervisor ||
           this.userRoles.admin ||
           this.userRoles.author ||
           (this.userRoles.analyst && this.isShared());
  }

  //******** CONVENIENCE METHODS ********
  
  public boolean userIsAuthor() {
    return this.userRoles.author;
  }
  
  public boolean userIsParticipant() {
    return this.userRoles.participant;
  }
  
  public boolean userIsSupervisor() {
    return this.userRoles.supervisor;
  }
  
  public boolean isRunning() {
    return this.runningState.equals(RunningState.RUNNING);
  }
  
  public boolean isShared() {
    return this.privacy.equals(Privacy.SHARED);
  }

  public boolean userIsSupervisorOrAdmin() {
    return this.userRoles.supervisor || this.userRoles.admin;
  }

  public boolean userIsAnalyst() {
    return this.userRoles.analyst;
  }

}

