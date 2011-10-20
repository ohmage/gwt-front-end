package edu.ucla.cens.mobilize.client.event;

import com.google.gwt.event.shared.GwtEvent;


public class UserLoginEvent extends GwtEvent<UserLoginEventHandler> {

    public static Type<UserLoginEventHandler> TYPE = new Type<UserLoginEventHandler>();
    
    // Fields
    private final String userName;
    
    public UserLoginEvent(String userName) {
        this.userName = userName;
    }
    
    /**
     * Returns the user name.
     * 
     * @return The user name.
     */
    public String getUserName() {
        return userName;
    }

    protected void dispatch(UserLoginEventHandler handler) {
        handler.onUserLogin(this);
    }

    public Type<UserLoginEventHandler> getAssociatedType() {
        return TYPE;
    }

}
