package edu.ucla.cens.AndWellnessVisualizations.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.ucla.cens.AndWellnessVisualizations.client.AndWellnessConstants;

public class VisualizationSelectionEvent extends GwtEvent<VisualizationSelectionEventHandler> {
    public static Type<VisualizationSelectionEventHandler> TYPE = new Type<VisualizationSelectionEventHandler>();
  
    // Fields
    private final AndWellnessConstants.VizType vizType;
    
    public VisualizationSelectionEvent(AndWellnessConstants.VizType vizType) {
        this.vizType = vizType;
    }
    
    /**
     * Returns the viz type that triggered the event.
     * 
     * @return The visualization type.
     */
    public AndWellnessConstants.VizType getSelection() {
        return vizType;
    }

    protected void dispatch(VisualizationSelectionEventHandler handler) {
        handler.onSelect(this);
    }

    public Type<VisualizationSelectionEventHandler> getAssociatedType() {
        return TYPE;
    }

}
