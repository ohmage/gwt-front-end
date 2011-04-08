package edu.ucla.cens.mobilize.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class NotLoggedInEvent extends GwtEvent<NotLoggedInEventHandler> {
    public static Type<NotLoggedInEventHandler> TYPE = new Type<NotLoggedInEventHandler>();

    public NotLoggedInEvent() {};
    
    protected void dispatch(NotLoggedInEventHandler handler) {
        handler.onLogout(this);
    }

    public Type<NotLoggedInEventHandler> getAssociatedType() {
        return TYPE;
    }
}
