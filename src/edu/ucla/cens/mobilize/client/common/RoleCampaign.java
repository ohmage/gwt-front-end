package edu.ucla.cens.mobilize.client.common;

/**
 * User type defines global type for a user. In contrast,
 * CampaignInfo.UserRole defines the role a user plays for
 * one specific campaign. 
 *  
 * @author vhajdik
 */
public enum RoleCampaign {
  PARTICIPANT,
  AUTHOR,
  ANALYST,
  SUPERVISOR,
  ADMIN, 
  RESEARCHER, 
  UNRECOGNIZED; // legacy
  
  public String toUserFriendlyString() {
    return this.toString().substring(0, 1).concat(this.toString().substring(1).toLowerCase());
  }

  public String toServerString() {
    return this.toString().toLowerCase();
  }
  
  public static RoleCampaign fromServerString(String roleStringInServerFormat) {
    RoleCampaign retval = null;
    try {
      retval = RoleCampaign.valueOf(roleStringInServerFormat.toUpperCase());
    } catch (Exception e) { 
      retval = UNRECOGNIZED;
    }
    return retval;
  }
}
