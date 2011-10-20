package edu.ucla.cens.mobilize.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ResponseDataChangedEvent extends GwtEvent<ResponseDataChangedEventHandler> {

  public static Type<ResponseDataChangedEventHandler> TYPE = new Type<ResponseDataChangedEventHandler>();

  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<ResponseDataChangedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ResponseDataChangedEventHandler handler) {
    handler.onSurveyResponseDataChanged(this);
  }
  
}
