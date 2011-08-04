package edu.ucla.cens.mobilize.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface ResponseDataChangedEventHandler extends EventHandler {
  void onSurveyResponseDataChanged(ResponseDataChangedEvent event);
}
