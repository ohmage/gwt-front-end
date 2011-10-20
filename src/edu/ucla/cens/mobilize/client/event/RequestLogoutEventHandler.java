package edu.ucla.cens.mobilize.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface RequestLogoutEventHandler extends EventHandler {
    void requestLogout(RequestLogoutEvent event);
}
