package edu.ucla.cens.mobilize.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface UserDataChangedEventHandler extends EventHandler {
  void onUserDataChanged(UserDataChangedEvent event);
}
