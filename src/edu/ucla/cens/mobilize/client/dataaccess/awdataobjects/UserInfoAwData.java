package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

// {"classes":{"urn:class:ca:lausd:BoyleHeights_HS:CS102:Spring:2011":"BH_HS_CS102_Spring_2011","urn:class:ca:lausd:Addams_HS:CS101:Fall:2011":"Addams_HS_CS101_Fall_2011"},"permissions":{"cancreate":true},"campaign_roles":["participant","author","supervisor"],"class_roles":["privileged"]}

/**
 * Detailed info about user's relationship to other data objects, usually used 
 * by the app when building the gui for the currently logged in user.
 */
public class UserInfoAwData extends JavaScriptObject {
  protected UserInfoAwData() {};
  
  public static native UserInfoAwData fromJsonString(String jsonString) /*-{
      return eval('(' + jsonString + ')'); 
  }-*/;
    
  // restricted or privileged
  private final native JsArrayString getClassRolesAsJsArray() /*-{ return this.class_roles; }-*/;
  
  // author, participant, supervisor, analyst
  private final native JsArrayString getCampaignRolesAsJsArray() /*-{ return this.campaign_roles }-*/;

  // email address
  public final native String getEmail() /*-{ 
    return (this.email_address != undefined) ? this.email_address : ""; 
  }-*/;
  
  public final native boolean getCanCreateFlag() /*-{ 
    return (this.permissions != undefined) ? this.permissions.can_create_campaigns : false; 
  }-*/;
  
  public final native boolean getIsAdminFlag() /*-{
    return (this.permissions != undefined) ? this.permissions.is_admin : false;
  }-*/;
  
  public final Map<String, String> getClasses() {
    Map<String, String> classNameToIdMap = new HashMap<String, String>();
    JsArrayString classKeys = getClassKeys();
    for (int i = 0; i < classKeys.length(); i++) {
      String classKey = classKeys.get(i);
      classNameToIdMap.put(classKey, getClassByKey(classKey));
    }
    return classNameToIdMap;
  }
  
  public final native JsArrayString getClassKeys() /*-{
    var keys = [];
    for (var key in this.classes) keys.push(key);
    return keys;
  }-*/;
  
  public final native String getClassByKey(String classKey) /*-{
    return this.classes[classKey];
  }-*/;

  public final Map<String, String> getCampaigns() {
    Map<String, String> campaignNameToIdMap = new HashMap<String, String>();
    JsArrayString campaignKeys = getCampaignKeys();
    for (int i = 0; i < campaignKeys.length(); i++) {
      String campaignKey = campaignKeys.get(i);
      campaignNameToIdMap.put(campaignKey, getCampaignByKey(campaignKey));
    }
    return campaignNameToIdMap;
  }
  
  public final native JsArrayString getCampaignKeys() /*-{
    var keys = [];
    for (var key in this.campaigns) keys.push(key);
    return keys;
  }-*/;
  
  public final native String getCampaignByKey(String campaignKey) /*-{
    return this.campaigns[campaignKey];
  }-*/;
  
  public final List<String> getCampaignRoles() {
    JsArrayString toTranslate = getCampaignRolesAsJsArray();
    List<String> toReturn = new ArrayList<String>();
    
    // Translate the array one by one
    for (int i = 0; i < toTranslate.length(); ++i) {
        String role = toTranslate.get(i);
        toReturn.add(role);
    }
    
    return toReturn;
  }
  
  public final List<String> getClassRoles() {
    JsArrayString toTranslate = getClassRolesAsJsArray();
    List<String> toReturn = new ArrayList<String>();
    
    // Translate the array one by one
    for (int i = 0; i < toTranslate.length(); ++i) {
      String role = toTranslate.get(i);
      toReturn.add(role);
    }
    return toReturn;
  }
  
}