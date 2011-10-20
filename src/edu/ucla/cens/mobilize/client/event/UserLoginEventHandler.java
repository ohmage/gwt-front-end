package edu.ucla.cens.mobilize.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface UserLoginEventHandler extends EventHandler {
    void onUserLogin(UserLoginEvent event);
}
