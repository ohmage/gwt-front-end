package edu.ucla.cens.AndWellnessVisualizations.client.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public class AuthorizationTokenAwData extends JavaScriptObject {
    protected AuthorizationTokenAwData() {};
    
    /**
     * Returns the authorization token in the JSON.
     * 
     * @return The authorization token.
     */
    public final native String getAuthorizationToken() /*-{ return this.token; }-*/;
    
    /**
     * Returns the list of campaign names as a JsArray.
     * 
     * @return The list of campaign names.
     */
    public final native JsArrayString getCampaignNameList() /*-{ 
        return this.campaigns; 
    }-*/;
    
    /**
     * Returns a List of String campaign names instead of a JsArray.
     * 
     * @return The List\<String\> of campaign names.
     */
    public final List<String> getStringCampaignNameList() {
        JsArrayString toTranslate = getCampaignNameList();
        List<String> toReturn = new ArrayList<String>();
        
        // Translate the array one by one
        for (int i = 0; i < toTranslate.length(); ++i) {
            String campaignName = toTranslate.get(i);
            
            toReturn.add(campaignName);
        }
        
        return toReturn;
    }
}
