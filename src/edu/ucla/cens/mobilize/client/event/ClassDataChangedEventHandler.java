package edu.ucla.cens.mobilize.client.event;
import com.google.gwt.event.shared.EventHandler;

public interface ClassDataChangedEventHandler extends EventHandler {
  public void onClassDataChanged(ClassDataChangedEvent event);
}
