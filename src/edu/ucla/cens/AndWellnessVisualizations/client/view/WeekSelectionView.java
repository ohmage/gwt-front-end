package edu.ucla.cens.AndWellnessVisualizations.client.view;

import com.google.gwt.user.client.ui.Widget;

import java.util.Date;

public interface WeekSelectionView {
    // Any Presenter that uses this View must implement these functions to
    // handle events from the View
    public interface Presenter {
        void onPreviousWeekSelected();
        void onNextWeekSelected();
    }
  
    // Sets the presenter so the view can call the presenter in response to events
    void setPresenter(Presenter presenter);
    // Sets the current month for display
    void setCurrentWeek(Date week);
    Widget asWidget();
}

