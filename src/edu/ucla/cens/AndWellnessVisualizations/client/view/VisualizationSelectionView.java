package edu.ucla.cens.AndWellnessVisualizations.client.view;

import com.google.gwt.user.client.ui.Widget;

public interface VisualizationSelectionView {
    // Any Presenter that uses this View must implement these functions to
    // handle events from the View
    public interface Presenter {
    	enum VizType {
            CALENDAR, MAP, CHART;
        }
    	
        void onSelection(VizType type);
    }
  
    // Sets the presenter so the view can call the presenter in response to events
    void setPresenter(Presenter presenter);

    Widget asWidget();
}

