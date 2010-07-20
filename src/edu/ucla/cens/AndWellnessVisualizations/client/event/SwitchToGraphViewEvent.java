package edu.ucla.cens.AndWellnessVisualizations.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class SwitchToGraphViewEvent extends
        GwtEvent<SwitchToGraphViewEventHandler> {
    public static Type<SwitchToGraphViewEventHandler> TYPE = new Type<SwitchToGraphViewEventHandler>();
    
    @Override
    public Type<SwitchToGraphViewEventHandler> getAssociatedType() {
        return TYPE;
    }
    
    @Override
    protected void dispatch(SwitchToGraphViewEventHandler handler) {
        handler.onSwitch(this);
    }
}