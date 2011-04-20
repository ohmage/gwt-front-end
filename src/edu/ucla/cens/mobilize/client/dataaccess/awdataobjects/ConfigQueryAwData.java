package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;


public class ConfigQueryAwData extends QueryAwData {
    protected ConfigQueryAwData() {};
    
    // Returns the list of special IDs associated with this user
    public final native JsArrayString getSpecialId() /*-{ return this.special_ids; }-*/;
    // Returns a list of Campaign info containing all necessary information about the campaign
    public final native JsArray<CampaignsAwData> getCampaignList() /*-{ return this.campaigns }-*/;
    
    // Create an ConfigQueryAwData from a JSON string
    public static native ConfigQueryAwData fromJsonString(String jsonString) /*-{
        return eval('(' + jsonString + ')'); 
    }-*/;
}
