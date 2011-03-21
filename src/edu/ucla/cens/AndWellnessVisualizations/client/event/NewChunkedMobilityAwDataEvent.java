package edu.ucla.cens.AndWellnessVisualizations.client.event;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

import edu.ucla.cens.AndWellnessVisualizations.client.model.ChunkedMobilityAwData;

/**
 * Event that is triggered when new DataPointAwData comes in from the server.  Any loaded
 * modules that want to access incoming server data should subscribe to the event bus.
 * 
 * @author jhicks
 *
 */
public class NewChunkedMobilityAwDataEvent extends
        GwtEvent<NewChunkedMobilityAwDataEventHandler> {
    public static Type<NewChunkedMobilityAwDataEventHandler> TYPE = new Type<NewChunkedMobilityAwDataEventHandler>();
    
    private final List<ChunkedMobilityAwData> mobilityDataPointAwData;
    
    public NewChunkedMobilityAwDataEvent(List<ChunkedMobilityAwData> _mobilityDataPointAwData) {
        mobilityDataPointAwData = _mobilityDataPointAwData;
    }
    
    public List<ChunkedMobilityAwData> getData() {
        return mobilityDataPointAwData;
    }
    
    @Override
    protected void dispatch(NewChunkedMobilityAwDataEventHandler handler) {
        handler.onNewData(this);
    }

    @Override
    public Type<NewChunkedMobilityAwDataEventHandler> getAssociatedType() {
        return TYPE;
    }

}
