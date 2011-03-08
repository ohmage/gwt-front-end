package edu.ucla.cens.AndWellnessVisualizations.client.event;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

import edu.ucla.cens.AndWellnessVisualizations.client.model.MobilityDataPointAwData;

/**
 * Event that is triggered when new DataPointAwData comes in from the server.  Any loaded
 * modules that want to access incoming server data should subscribe to the event bus.
 * 
 * @author jhicks
 *
 */
public class NewMobilityDataPointAwDataEvent extends
        GwtEvent<NewMobilityDataPointAwDataEventHandler> {
    public static Type<NewMobilityDataPointAwDataEventHandler> TYPE = new Type<NewMobilityDataPointAwDataEventHandler>();
    
    private final List<MobilityDataPointAwData> mobilityDataPointAwData;
    
    public NewMobilityDataPointAwDataEvent(List<MobilityDataPointAwData> _mobilityDataPointAwData) {
        mobilityDataPointAwData = _mobilityDataPointAwData;
    }
    
    public List<MobilityDataPointAwData> getData() {
        return mobilityDataPointAwData;
    }
    
    @Override
    protected void dispatch(NewMobilityDataPointAwDataEventHandler handler) {
        handler.onNewData(this);
    }

    @Override
    public Type<NewMobilityDataPointAwDataEventHandler> getAssociatedType() {
        return TYPE;
    }

}
