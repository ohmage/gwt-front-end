package edu.ucla.cens.AndWellnessVisualizations.client.model;


/**
 * Overlays the response from /app/auth_token.  The response should contain
 * both an authorization token to use to authorize all calls to the server plus
 * a list of campaigns to which the user belongs.
 * 
 * @author jhicks
 *
 */
public class AuthorizationTokenQueryAwData extends QueryAwData {

    protected AuthorizationTokenQueryAwData() {};
    
    // Grab the authorization token from the JSON
    public final native AuthorizationTokenAwData getData() /*-{
        return this.data;
    }-*/;
    
    // Create an AuthorizationTokenQueryAwData from a JSON string
    public static native AuthorizationTokenQueryAwData fromJsonString(String jsonString) /*-{
        return eval('(' + jsonString + ')'); 
    }-*/;
}
