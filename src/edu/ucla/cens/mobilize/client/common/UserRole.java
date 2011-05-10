package edu.ucla.cens.mobilize.client.common;

/**
 * User type defines global type for a user. In contrast,
 * CampaignInfo.UserRole defines the role a user plays for
 * one specific campaign. 
 *  
 * @author vhajdik
 */
public enum UserRole {
  PARTICIPANT,
  AUTHOR,
  ANALYST,
  SUPERVISOR,
  ADMIN, 
  PRIVILEGED, // FIXME: this should be a different enum 
  RESTRICTED, // FIXME: this should be a different enum
  RESEARCHER; // legacy
  
  public String toUserFriendlyString() {
    return this.toString().substring(0, 1).concat(this.toString().substring(1).toLowerCase());
  }

  public String toServerString() {
    return this.toString().toLowerCase();
  }
}
