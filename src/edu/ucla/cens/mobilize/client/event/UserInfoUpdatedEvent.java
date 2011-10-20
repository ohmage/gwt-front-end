package edu.ucla.cens.mobilize.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.ucla.cens.mobilize.client.model.UserInfo;

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
