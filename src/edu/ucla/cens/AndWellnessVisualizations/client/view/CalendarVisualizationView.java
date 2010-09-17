package edu.ucla.cens.AndWellnessVisualizations.client.view;

import java.util.Date;
import java.util.Map;

import com.google.gwt.user.client.ui.Widget;

public interface CalendarVisualizationView {
    // Any Presenter that uses this View must implement these functions to
    // handle events from the View
    public interface Presenter {
        void onDayClicked(Date selectedDate);
    }
  
    // Sets the presenter so the view can call the presenter in response to events
    void setPresenter(Presenter presenter);
    
    // Changes the displayed month
    void updateMonth(Date month);
    
    // Updates the view's data
    void updateDayData(Map<Date, Double> dayData);
    
    Widget asWidget();
}
