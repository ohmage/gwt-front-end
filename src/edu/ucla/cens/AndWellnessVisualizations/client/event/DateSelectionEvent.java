package edu.ucla.cens.AndWellnessVisualizations.client.event;

import java.util.Date;

import com.google.gwt.event.shared.GwtEvent;

public class DateSelectionEvent extends GwtEvent<DateSelectionEventHandler> {
    public static Type<DateSelectionEventHandler> TYPE = new Type<DateSelectionEventHandler>();
    
    // Types of date selection
    public static enum DateType {Day, Week, Month};
    
    // Fields
    private final Date dateSelection;
    private final DateType dateType;
    
    public DateSelectionEvent(Date _dateSelection, DateType _dateType) {
        dateSelection = _dateSelection;
        dateType = _dateType;
    }
    
    /**
     * Returns the date that triggered the event.
     * 
     * @return The date selection.
     */
    public Date getSelection() {
        return dateSelection;
    }
    
    /**
     * Returns the type of date selection.
     * 
     * @return The dateType.
     */
    public DateType getType() {
    	return dateType;
    }

    protected void dispatch(DateSelectionEventHandler handler) {
        handler.onSelection(this);
    }

    public Type<DateSelectionEventHandler> getAssociatedType() {
        return TYPE;
    }

}
