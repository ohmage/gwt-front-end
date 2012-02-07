package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArrayString;

public class DocumentAwData extends QueryAwData {
  // Every overlay type must have precisely one constructor, 
  // and it must be protected, empty, and no-argument.
  protected DocumentAwData() {};

  public final native String getDocumentName() /*-{ return this.name; }-*/;
  public final native String getCreator() /*-{ return this.creator; }-*/;
  public final native String getDescription() /*-{ return this.description; }-*/;
  public final native String getUserRole() /*-{ return this.user_role; }-*/;
  public final native String getUserMaxRole() /*-{ return this.user_max_role; }-*/;
  public final native String getPrivacyState() /*-{ return this.privacy_state; }-*/;
  public final native float getSize() /*-{ return this.size; }-*/; // FIXME: int? 
  public final native String getLastModified() /*-{ return this.last_modified; }-*/;
  
  private final native JsArrayString getCampaignUrnsJsArray() /*-{
    var campaign_urns = [];
    for (var key in this.campaign_role) {
      campaign_urns.push(key);
    } 
    return campaign_urns;
  }-*/;
  
  public final List<String> getCampaignUrns() {
    List<String> campaignUrns = new ArrayList<String>();
    JsArrayString urns = getCampaignUrnsJsArray();
    for (int i = 0; i < urns.length(); i++) {
      campaignUrns.add(urns.get(i));
    }
    return campaignUrns;
  }
  
  private final native JsArrayString getClassUrnsJsArray() /*-{
    var class_urns = [];
    for (var key in this.class_role) { // get hash keys
      class_urns.push(key);
    } 
    return class_urns;
  }-*/;
  
  public final List<String> getClassUrns() {
    List<String> classUrns = new ArrayList<String>();
    JsArrayString urns = getClassUrnsJsArray();
    for (int i = 0; i < urns.length(); i++) {
      classUrns.add(urns.get(i));
    }
    return classUrns;
  }
  
  public final native String getClassRole(String classUrn) /*-{
    return this.class_role[classUrn];
  }-*/;
  
  public final native String getCampaignRole(String campaignUrn) /*-{
    return this.campaign_role[campaignUrn];
  }-*/;
  
}

