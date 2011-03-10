package edu.ucla.cens.AndWellnessVisualizations.client.event;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Triggered whenever a new datapoint is selected from any UI that allows data point selection.
 * Contains enough information to ask for data points from the server excluding date range.
 * 
 * @author jhicks
 *
 */
public class DataBrowserSelectionEvent extends
        GwtEvent<DataBrowserSelectionEventHandler> {
    public static Type<DataBrowserSelectionEventHandler> TYPE = new Type<DataBrowserSelectionEventHandler>();
    
    public static enum DataType {
        campaignName, campaignVersion, userName, promptIdList 
    }
    
    // Describes the type of data this event holds
    private final DataType selectionDataType;
    // The actual data
    private List<String> selectionData = new ArrayList<String>();

    public DataBrowserSelectionEvent(DataType type) {
        selectionDataType = type;
    }
        
    public void add(String data) {
    	selectionData.add(data);
    }
    
    public List<String> getData() {
    	return selectionData;
    }
    
    public DataType getType() {
    	return selectionDataType;
    }
    
    protected void dispatch(DataBrowserSelectionEventHandler handler) {
        handler.onSelect(this);
    }

    public Type<DataBrowserSelectionEventHandler> getAssociatedType() {
        return TYPE;
    }

}
