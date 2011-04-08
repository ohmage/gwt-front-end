package edu.ucla.cens.mobilize.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class UserLogoutEvent extends GwtEvent<UserLogoutEventHandler> {

    public static Type<UserLogoutEventHandler> TYPE = new Type<UserLogoutEventHandler>();

    
    public UserLogoutEvent() {}
    

    protected void dispatch(UserLogoutEventHandler handler) {
        handler.onUserLogout(this);
    }

    public Type<UserLogoutEventHandler> getAssociatedType() {
        return TYPE;
    }

}
