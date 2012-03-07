package edu.ucla.cens.mobilize.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired when a user is created or deleted. Mainly useful in the admin tab, where info pertaining
 *   to all users (e.g., list of all usernames) will need to be refreshed. 
 * 
 * Note this event differs from UserInfoUpdatedEvent:
 * - UserDataChangedEvent is fired for any user. UserInfoUpdatedEvent is fired only for the currently
 *   logged in user.
 * - UserDataChangedEvent is fired for events that affect whether a username should show up in 
 *   a list of all users - i.e., if a user is created or deleted. UserInfoUpdatedEvent is fired
 *   when any info about the currently logged in user changes.
 */
public class UserDataChangedEvent extends GwtEvent<UserDataChangedEventHandler> {

  public static Type<UserDataChangedEventHandler> TYPE = new Type<UserDataChangedEventHandler>();
  
  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<UserDataChangedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(UserDataChangedEventHandler handler) {
    handler.onUserDataChanged(this);
  }
  
}
