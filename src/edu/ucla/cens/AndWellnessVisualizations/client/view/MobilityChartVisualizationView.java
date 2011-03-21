package edu.ucla.cens.AndWellnessVisualizations.client.view;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.AndWellnessVisualizations.client.model.ChunkedMobilityAwData;

public interface MobilityChartVisualizationView {
    // Any Presenter that uses this View must implement these functions to
    // handle events from the View
    public interface Presenter {
        
    }
  
    // Sets the presenter so the view can call the presenter in response to events
    void setPresenter(Presenter presenter);
    
    // Updates the view's data
    void setDataList(List<ChunkedMobilityAwData> data);
    
    Widget asWidget();
}
