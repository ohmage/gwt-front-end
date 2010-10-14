package edu.ucla.cens.AndWellnessVisualizations.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.ucla.cens.AndWellnessVisualizations.client.model.UserInfo;

public class UserLoginEvent extends GwtEvent<UserLoginEventHandler> {

    public static Type<UserLoginEventHandler> TYPE = new Type<UserLoginEventHandler>();
    
    // Fields
    private final UserInfo userInfo;
    
    public UserLoginEvent(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
    
    /**
     * Returns the user data.
     * 
     * @return The user data as a UserInfo object.
     */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    protected void dispatch(UserLoginEventHandler handler) {
        handler.onUserLogin(this);
    }

    public Type<UserLoginEventHandler> getAssociatedType() {
        return TYPE;
    }

}
