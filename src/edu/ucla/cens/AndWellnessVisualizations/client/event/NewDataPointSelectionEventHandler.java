package edu.ucla.cens.AndWellnessVisualizations.client.event;

import com.google.gwt.event.shared.EventHandler;


public interface NewDataPointSelectionEventHandler extends EventHandler {
    void onSelect(NewDataPointSelectionEvent event);
}
