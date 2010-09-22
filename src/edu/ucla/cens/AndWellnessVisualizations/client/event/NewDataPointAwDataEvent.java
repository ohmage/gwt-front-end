package edu.ucla.cens.AndWellnessVisualizations.client.event;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

import edu.ucla.cens.AndWellnessVisualizations.client.model.DataPointAwData;

/**
 * Event that is triggered when new DataPointAwData comes in from the server.  Any loaded
 * modules that want to access incoming server data should subscribe to the event bus.
 * 
 * @author jhicks
 *
 */
public class NewDataPointAwDataEvent extends
        GwtEvent<NewDataPointAwDataEventHandler> {
    public static Type<NewDataPointAwDataEventHandler> TYPE = new Type<NewDataPointAwDataEventHandler>();
    
    private final List<DataPointAwData> dataPointAwData;
    
    public NewDataPointAwDataEvent(List<DataPointAwData> _dataPointAwData) {
        dataPointAwData = _dataPointAwData;
    }
    
    public List<DataPointAwData> getData() {
        return dataPointAwData;
    }
    
    @Override
    protected void dispatch(NewDataPointAwDataEventHandler handler) {
        handler.onNewData(this);
    }

    @Override
    public Type<NewDataPointAwDataEventHandler> getAssociatedType() {
        return TYPE;
    }

}
