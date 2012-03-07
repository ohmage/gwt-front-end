package edu.ucla.cens.mobilize.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.ucla.cens.mobilize.client.model.UserInfo;

/**
 * Fired when info about the currently logged in user changes. 
 * 
 * Note this event applies only to the currently logged in user. In this way, it differs from 
 *   UserDataChangedEvent which is fired when any user changes/
 */
public class UserInfoUpdatedEvent extends GwtEvent<UserInfoUpdatedEventHandler> {
  public static Type<UserInfoUpdatedEventHandler> TYPE = new Type<UserInfoUpdatedEventHandler>();

  private UserInfo userInfo;
  
  public UserInfoUpdatedEvent(UserInfo userInfo) {
    this.userInfo = userInfo;
  }
  
  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<UserInfoUpdatedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(UserInfoUpdatedEventHandler handler) {
    handler.onUserInfoChanged(this);
  }

  public UserInfo getUserInfo() {
    return this.userInfo;
  }
}
