package edu.ucla.cens.mobilize.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface UserLogoutEventHandler extends EventHandler {
    void onUserLogout(UserLogoutEvent event);
}
