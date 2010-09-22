package edu.ucla.cens.AndWellnessVisualizations.client.event;

import java.util.Date;

import com.google.gwt.event.shared.GwtEvent;

public class MonthSelectionEvent extends GwtEvent<MonthSelectionEventHandler> {
    public static Type<MonthSelectionEventHandler> TYPE = new Type<MonthSelectionEventHandler>();
    
    // Fields
    private final Date monthSelection;
    
    public MonthSelectionEvent(Date _monthSelection) {
        monthSelection = _monthSelection;
    }
    
    /**
     * Returns the month selection that triggered the event.
     * 
     * @return The month selection.
     */
    public Date getMonthSelection() {
        return monthSelection;
    }

    protected void dispatch(MonthSelectionEventHandler handler) {
        handler.onSelection(this);
    }

    public Type<MonthSelectionEventHandler> getAssociatedType() {
        return TYPE;
    }

}
