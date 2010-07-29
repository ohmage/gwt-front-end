package edu.ucla.cens.AndWellnessVisualizations.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.ucla.cens.AndWellnessVisualizations.client.model.MainViewState;

/**
 * Sent to the event bus whenever the user desires to switch to the
 * graph view.
 * 
 * @author jhicks
 *
 */
public class SwitchViewEvent extends
        GwtEvent<SwitchViewEventHandler> {
    public static Type<SwitchViewEventHandler> TYPE = new Type<SwitchViewEventHandler>();
    private final MainViewState mainViewState;
    
    public SwitchViewEvent(MainViewState appState) {
        this.mainViewState = appState;
    }
    
    public MainViewState getAppState() { return mainViewState; }
    
    @Override
    public Type<SwitchViewEventHandler> getAssociatedType() {
        return TYPE;
    }
    
    @Override
    protected void dispatch(SwitchViewEventHandler handler) {
        handler.onSwitch(this);
    }
}