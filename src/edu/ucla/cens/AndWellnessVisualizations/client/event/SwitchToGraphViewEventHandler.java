package edu.ucla.cens.AndWellnessVisualizations.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Interface to implement to handle the switch to graph view event.
 * 
 * @author jhicks
 *
 */
public interface SwitchToGraphViewEventHandler extends EventHandler {
    void onSwitch(SwitchToGraphViewEvent event);
}
