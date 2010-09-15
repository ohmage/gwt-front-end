package edu.ucla.cens.AndWellnessVisualizations.client.view;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

public interface CalendarVisualizationView<T> {
    // Any Presenter that uses this View must implement these functions to
    // handle events from the View
    public interface Presenter<T> {
        void onDayClicked(Date selectedDate);
    }
  
    // Sets the presenter so the view can call the presenter in response to events
    void setPresenter(Presenter<T> presenter);
    //void setDropDownDefinitions(List<DropDownDefinition<T>> dropDownDefinitions);
    // Update the days with opacity values
    void setDayData(List<T> dayData);
    Widget asWidget();
}
