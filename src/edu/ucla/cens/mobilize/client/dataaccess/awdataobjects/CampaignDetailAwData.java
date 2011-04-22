package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import com.google.gwt.core.client.JsArrayString;

// {"classes":["urn:sys:andwellness"],"user_role_campaign":{"analyst":[],"author":[],"supervisor":["temp.user"],"participant":[]},"user_roles":["supervisor"],"name":"NIH","privacy_state":"private","xml":"","creation_timestamp":"2011-04-12 15:33:34.0","running_state":"active"}
public class CampaignDetailAwData extends QueryAwData {
  protected CampaignDetailAwData() {};
  
  public final native String getCampaignName() /*-{ return this.name; }-*/;
  public final native JsArrayString getUserRoles() /*-{ return this.user_roles; }-*/;
  public final native JsArrayString getParticipants() /*-{ return this.user_role_campaign.participant; }-*/;
  public final native JsArrayString getAnalysts() /*-{ return this.user_role_campaign.analyst; }-*/;
  public final native JsArrayString getAuthors() /*-{ return this.user_role_campaign.author; }-*/;
  public final native JsArrayString getSupervisors() /*-{ return this.user_role_campaign.supervisor; }-*/;
  public final native String getXmlConfig() /*-{ return this.xml; }-*/;
  public final native JsArrayString getClasses() /*-{ return this.classes; }-*/;
  public final native String getCreationTime() /*-{ return this.creation_timestamp; }-*/;
  public final native String getRunningState() /*-{ return this.running_state; }-*/;
  public final native String getPrivacyState() /*-{ return this.privacy_state; }-*/;
  public final native String getDescription() /*-{ return this.description; }-*/;
  
  // Create an CampaignDetailAwData from a JSON string
  public static native CampaignDetailAwData fromJsonString(String jsonString) /*-{
      return eval('(' + jsonString + ')'); 
  }-*/;
}
