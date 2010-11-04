package edu.ucla.cens.AndWellnessVisualizations.client.model;

public class ConfigurationsAwData extends QueryAwData {
    protected ConfigurationsAwData() {};
    
    // Returns the version of this campaign (all campaign name/campaign version pairs are separate)
    public final native String getCampaignVersion() /*-{ return this.version; }-*/;
    // Returns the XML configuration that created this campaign name/version
    public final native String getCampaignConfiguration() /*-{ return this.configuration; }-*/;
    
    // Create an ConfigurationsAwData from a JSON string
    public static native ConfigurationsAwData fromJsonString(String jsonString) /*-{
        return eval('(' + jsonString + ')'); 
    }-*/;
}
