package edu.ucla.cens.mobilize.client.common;

import java.util.List;

public class UserRoles {
  public boolean admin;
  public boolean author;
  public boolean participant;
  public boolean analyst;
  public boolean supervisor;
  
  public UserRoles() {}
  
  public UserRoles(List<RoleCampaign> listOfRoles) {
    admin = listOfRoles.contains(RoleCampaign.ADMIN);
    author = listOfRoles.contains(RoleCampaign.AUTHOR);
    participant = listOfRoles.contains(RoleCampaign.PARTICIPANT);
    analyst = listOfRoles.contains(RoleCampaign.ANALYST);
    supervisor = listOfRoles.contains(RoleCampaign.SUPERVISOR);
  }
  
  public void addRole(RoleCampaign role) {
    switch (role) {
      case PARTICIPANT: this.participant = true; break;
      case ANALYST: this.analyst = true; break;
      case AUTHOR: this.author = true; break;
      case SUPERVISOR: this.supervisor = true; break;
      case ADMIN: this.admin = true; break;
      default: break;
    }
  }
  
}
