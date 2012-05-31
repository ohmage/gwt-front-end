package org.ohmage.mobilize.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface ResponseDataChangedEventHandler extends EventHandler {
  void onSurveyResponseDataChanged(ResponseDataChangedEvent event);
}
