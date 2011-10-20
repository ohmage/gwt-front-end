package edu.ucla.cens.mobilize.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface UserInfoUpdatedEventHandler extends EventHandler {
  public void onUserInfoChanged(UserInfoUpdatedEvent event);
}
