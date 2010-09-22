package edu.ucla.cens.AndWellnessVisualizations.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class DataPointLabelSelectionEvent extends
        GwtEvent<DataPointLabelSelectionEventHandler> {
    public static Type<DataPointLabelSelectionEventHandler> TYPE = new Type<DataPointLabelSelectionEventHandler>();
    
    // Fields
    private final String dataPointLabel;
    
    public DataPointLabelSelectionEvent(String _dataPointLabel) {
        dataPointLabel = _dataPointLabel;
    }
    
    /**
     * Returns the selected data point labels.
     * 
     * @return The data point labels.
     */
    public String getDataPointLabelSelection() {
        return dataPointLabel;
    }

    protected void dispatch(DataPointLabelSelectionEventHandler handler) {
        handler.onSelection(this);
    }

    public Type<DataPointLabelSelectionEventHandler> getAssociatedType() {
        return TYPE;
    }
}
