package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;


public class CampaignsAwData extends QueryAwData {
    protected CampaignsAwData() {};
    
    // Returns the name of this campaign
    public final native String getCampaignName() /*-{ return this.id; }-*/;
    // Returns the user role in this campaign
    public final native String getUserRole() /*-{ return this.user_role; }-*/;
    // Returns the list of participants the logged in user can retrieve information about
    public final native JsArrayString getUserList() /*-{ return this.user_list; }-*/;
    // Returns the configurations attached to this campaign
    public final native JsArray<ConfigurationsAwData> getConfigurations() /*-{ return this.configurations; }-*/;
    
    // Create an CampaignsAwData from a JSON string
    public static native CampaignsAwData fromJsonString(String jsonString) /*-{
        return eval('(' + jsonString + ')'); 
    }-*/;
}
