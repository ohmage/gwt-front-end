package edu.ucla.cens.AndWellnessVisualizations.client.model;

import com.google.gwt.core.client.JsArray;

/**
 * Overlays the response from /app/auth_token.  The response should contain
 * both an authorization token to use to authorize all calls to the server plus
 * a list of campaigns to which the user belongs.
 * 
 * @author jhicks
 *
 */
public class AuthorizationInformationAwData extends AwData {

    protected AuthorizationInformationAwData() {};
    
    // Grab the authorization token from the JSON
    public final native String getAuthorizationToken() /*-{ return this.token; }-*/;
    
    // Grab the List of campaign names
    public final native JsArray<CampaignNameAwData> getCampaignNameList() /*-{ 
        return this.campaigns; 
    }-*/;
    
    // Create an AuthorizationInformationAwData from a JSON string
    public static native AuthorizationInformationAwData fromJsonString(String jsonString) /*-{
        return eval('(' + jsonString + ')'); 
    }-*/;
}
