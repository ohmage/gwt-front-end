package edu.ucla.cens.mobilize.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface NotLoggedInEventHandler extends EventHandler {
    void onLogout(NotLoggedInEvent event);
}
