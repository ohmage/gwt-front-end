package edu.ucla.cens.AndWellnessVisualizations.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.ucla.cens.AndWellnessVisualizations.client.model.UserInfo;

public class CampaignConfigurationEvent extends
        GwtEvent<CampaignConfigurationEventHandler> {
    public static Type<CampaignConfigurationEventHandler> TYPE = new Type<CampaignConfigurationEventHandler>();
    
    // Fields
    private final UserInfo userInfo;
    
    public CampaignConfigurationEvent(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
    
    /**
     * Returns the new campaign configuration information.
     * 
     * @return The logged in user info.
     */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    protected void dispatch(CampaignConfigurationEventHandler handler) {
        handler.onReceive(this);
    }

    public Type<CampaignConfigurationEventHandler> getAssociatedType() {
        return TYPE;
    }
}
