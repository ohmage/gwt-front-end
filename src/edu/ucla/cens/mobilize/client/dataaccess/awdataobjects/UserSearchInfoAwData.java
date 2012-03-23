package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * Parses json object returned by the user/search api. 
 * Note this is similar to the user_info/read api but includes user personal info as
 * well as roles for each class and campaign.
 * @author shlurbee
 */
public class UserSearchInfoAwData extends JavaScriptObject {
  protected UserSearchInfoAwData() {}
  
  public final native boolean getCanCreateFlag() /*-{ 
    return (this.permissions != undefined) ? this.permissions.can_create_campaigns : false; 
  }-*/;
  
  public final native boolean getNewAccountFlag() /*-{
    return (this.permissions != undefined) ? this.permissions.new_account : false;
  }-*/;
  
  public final native boolean getEnabledFlag() /*-{
    return (this.permissions != undefined) ? this.permissions.enabled : false;
  }-*/;
  
  public final native boolean getAdminFlag() /*-{
    return (this.permissions != undefined) ? this.permissions.admin : false;
  }-*/;

  /**
   * @return String or null
   */
  public final native String getFirstName() /*-{
    return (this.personal != undefined) ? this.personal.first_name : null;
  }-*/;

  /**
   * @return String or null
   */
  public final native String getLastName() /*-{
    return (this.personal != undefined) ? this.personal.last_name : null;
  }-*/;
  
  /**
   * @return String or null
   */
  public final native String getOrganization() /*-{
    return (this.personal != undefined) ? this.personal.organization : null;
  }-*/;
  
  /**
   * @return String or null
   */
  public final native String getPersonalId() /*-{
    return (this.personal != undefined) ? this.personal.personal_id : null;
  }-*/;
  
  /**
   * @return String or null
   */
  public final native String getEmailAddress() /*-{
    return this.email_address;
  }-*/;
  
  /**
   * @return Map of class urns to user's role in that class ("restricted" or "privileged") 
   */
  public final Map<String, String> getClasses() {
    Map<String, String> classNameToIdMap = new HashMap<String, String>();
    JsArrayString classUrns = getClassUrns();
    for (int i = 0; i < classUrns.length(); i++) {
      String classKey = classUrns.get(i);
      classNameToIdMap.put(classKey, getClassByUrn(classKey));
    }
    return classNameToIdMap;
  }

  public final native JsArrayString getClassUrns() /*-{
    var keys = [];
    for (var key in this.classes) keys.push(key);
    return keys;
  }-*/;
  
  private final native String getClassByUrn(String classKey) /*-{
    return this.classes[classKey];
  }-*/;

  /**
   * @return Map of class urn to a list of the user's roles in the campaigns ("author", "participant", "analyst")
   */
  public final Map<String, List<String>> getCampaigns() {
    Map<String, List<String>> campaignUrnToRoleListMap = new HashMap<String, List<String>>();
    JsArrayString campaignUrns = getCampaignUrns();
    for (int i = 0; i < campaignUrns.length(); i++) {
      String campaignUrn = campaignUrns.get(i);
      JsArrayString rolesArray = getCampaignRoles(campaignUrn);
      List<String> roles = new ArrayList<String>();
      for (int j = 0; j < rolesArray.length(); j++) {
        roles.add(rolesArray.get(j));
      }
      campaignUrnToRoleListMap.put(campaignUrn, roles);
    }
    return campaignUrnToRoleListMap;
  }
  
  public final native JsArrayString getCampaignUrns() /*-{
    var keys = [];
    for (var key in this.campaigns) keys.push(key);
    return keys;
  }-*/;
  
  private final native JsArrayString getCampaignRoles(String campaignUrn) /*-{
    var roles = [];
    if (campaignUrn in this.campaigns) {
      roles = this.campaigns[campaignUrn];
    }
    return roles;
  }-*/;
  
}
