package edu.ucla.cens.AndWellnessVisualizations.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Interface to implement to handle the switch to upload view event.
 * 
 * @author jhicks
 *
 */
public interface SwitchToUploadViewEventHandler extends EventHandler {
    void onSwitch(SwitchToUploadViewEvent event);
}
