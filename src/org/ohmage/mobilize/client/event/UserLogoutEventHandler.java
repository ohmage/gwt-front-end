package org.ohmage.mobilize.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface UserLogoutEventHandler extends EventHandler {
    void onUserLogout(UserLogoutEvent event);
}
