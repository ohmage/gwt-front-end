package edu.ucla.cens.mobilize.client.common;

/**
 * User type defines global type for a user. In contrast,
 * CampaignInfo.UserRole defines the role a user plays for
 * one specific campaign. 
 *  
 * @author shlurbee
 */
public enum UserRole {
  PARTICIPANT,
  AUTHOR,
  ANALYST,
  SUPERVISOR,
  ADMIN, 
  RESEARCHER; // legacy
  
  public String toString() {
    String str = null;
    switch (this) {
      case PARTICIPANT: str = "Participant"; break;
      case AUTHOR: str = "Author"; break;
      case ANALYST: str = "Analyst"; break;
      case SUPERVISOR: str = "Supervisor"; break;
      case ADMIN: str = "Admin"; break;
      case RESEARCHER: str = "Researcher"; break;
      default: str = "Other"; break;
    }
    return str;
  }
}
