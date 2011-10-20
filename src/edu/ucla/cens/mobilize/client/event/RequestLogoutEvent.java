package edu.ucla.cens.mobilize.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class RequestLogoutEvent extends GwtEvent<RequestLogoutEventHandler> {
    public static Type<RequestLogoutEventHandler> TYPE = new Type<RequestLogoutEventHandler>();

    public RequestLogoutEvent() {};
    
    protected void dispatch(RequestLogoutEventHandler handler) {
        handler.requestLogout(this);
    }

    public Type<RequestLogoutEventHandler> getAssociatedType() {
        return TYPE;
    }
}
