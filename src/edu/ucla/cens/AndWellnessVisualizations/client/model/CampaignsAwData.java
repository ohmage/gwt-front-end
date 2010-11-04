package edu.ucla.cens.AndWellnessVisualizations.client.model;

import com.google.gwt.core.client.JsArrayString;

public class CampaignsAwData extends QueryAwData {
    protected CampaignsAwData() {};
    
    // Returns the name of this campaign
    public final native String getCampaignName() /*-{ return this.name; }-*/;
    // Returns the version of this campaign (all campaign name/campaign version pairs are separate)
    public final native String getCampaignVersion() /*-{ return this.version; }-*/;
    // Returns the user role in this campaign
    public final native String getUserRole() /*-{ return this.user_role; }-*/;
    // Returns the list of participants the logged in user can retrieve information about
    public final native JsArrayString getUserList() /*-{ return this.user_list; }-*/;
    // Returns the XML configuration that created this campaign name/version
    public final native String getCampaignConfiguration() /*-{ return this.configuration; }-*/;
    
    // Create an CampaignsAwData from a JSON string
    public static native CampaignsAwData fromJsonString(String jsonString) /*-{
        return eval('(' + jsonString + ')'); 
    }-*/;
}
