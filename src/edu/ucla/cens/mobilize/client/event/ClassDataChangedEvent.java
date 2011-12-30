package edu.ucla.cens.mobilize.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ClassDataChangedEvent extends GwtEvent<ClassDataChangedEventHandler> {
  
  public static Type<ClassDataChangedEventHandler> TYPE = new Type<ClassDataChangedEventHandler>();
  
  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<ClassDataChangedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ClassDataChangedEventHandler handler) {
    handler.onClassDataChanged(this);
  }

}
