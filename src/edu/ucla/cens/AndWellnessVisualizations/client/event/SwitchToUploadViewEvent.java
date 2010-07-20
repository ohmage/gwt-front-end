package edu.ucla.cens.AndWellnessVisualizations.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class SwitchToUploadViewEvent extends
        GwtEvent<SwitchToUploadViewEventHandler> {
    public static Type<SwitchToUploadViewEventHandler> TYPE = new Type<SwitchToUploadViewEventHandler>();
    
    @Override
    public Type<SwitchToUploadViewEventHandler> getAssociatedType() {
        return TYPE;
    }
    
    @Override
    protected void dispatch(SwitchToUploadViewEventHandler handler) {
        handler.onSwitch(this);
    }
}
