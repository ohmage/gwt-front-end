package edu.ucla.cens.mobilize.client.model;

import java.util.ArrayList;
import java.util.List;

import edu.ucla.cens.mobilize.client.common.UserRole;
import edu.ucla.cens.mobilize.client.common.UserRoles;
import edu.ucla.cens.mobilize.client.common.UserStats;

public class UserInfo {
  private String userName; // login id
  private boolean canCreate = false; // if true, user can create a new campaign
  private UserRoles roles = new UserRoles(); // flags showing what roles are held by user
  private List<String> classes = new ArrayList<String>(); // user groups
  private List<String> visibleUsers = new ArrayList<String>(); // whose info this user can see
  
  private UserStats stats = new UserStats();
  
  public UserInfo(String username, 
                  boolean canCreate, 
                  List<String> classes,
                  List<UserRole> roles) {
    this.userName = username;
    this.canCreate = canCreate;

    this.visibleUsers.add(this.userName); // most users can only see themselves
    
    // FIXME: user info service should also return list of users visible to this one
    
    if (classes != null) this.classes.addAll(classes);

    for (UserRole role : roles) {
      this.roles.addRole(role);
    }    
  }
  
  /******** GETTERS ********/
 
  public String getUserName() {
    return this.userName != null ? this.userName : "invalid username";
  }
  
  public UserStats getStats() {
    return this.stats;
  }
  
  public List<String> getClasses() {
    return this.classes;
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
  
  public boolean canEdit(String campaignId) {
    return this.roles.admin || this.roles.supervisor || this.roles.author;
  }
  
  public boolean canDelete(String campaignId) {
    return this.roles.admin || this.roles.supervisor || this.roles.author;
  }
  
}
