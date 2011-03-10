package edu.ucla.cens.AndWellnessVisualizations.client.event;

import java.util.Date;

import com.google.gwt.event.shared.GwtEvent;

public class DateSelectionEvent extends GwtEvent<DateSelectionEventHandler> {
    public static Type<DateSelectionEventHandler> TYPE = new Type<DateSelectionEventHandler>();
    
    // Fields
    private final Date dateSelection;
    
    public DateSelectionEvent(Date _dateSelection) {
        dateSelection = _dateSelection;
    }
    
    /**
     * Returns the date that triggered the event.
     * 
     * @return The date selection.
     */
    public Date getSelection() {
        return dateSelection;
    }

    protected void dispatch(DateSelectionEventHandler handler) {
        handler.onSelection(this);
    }

    public Type<DateSelectionEventHandler> getAssociatedType() {
        return TYPE;
    }

}
