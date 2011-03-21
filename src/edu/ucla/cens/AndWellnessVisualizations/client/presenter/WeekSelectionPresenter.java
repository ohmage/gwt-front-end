package edu.ucla.cens.AndWellnessVisualizations.client.presenter;

import java.util.Date;

import com.google.code.p.gwtchismes.client.GWTCDatePicker;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.ucla.cens.AndWellnessVisualizations.client.event.DateSelectionEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.AndWellnessRpcService;
import edu.ucla.cens.AndWellnessVisualizations.client.view.WeekSelectionView;

public class WeekSelectionPresenter implements Presenter,
        WeekSelectionView.Presenter {

    private Date currentWeek;
    private EventBus eventBus;
    private WeekSelectionView view;
    
   
    public WeekSelectionPresenter(AndWellnessRpcService rpcService, 
            EventBus eventBus, WeekSelectionView view) {
        this.eventBus = eventBus;
        this.view = view;
        this.view.setPresenter(this);
    }  
    
    /**
     * Initialized the presenter in the passed container.
     * 
     * @param container The container in which to initialize the presenter.
     */
    public void go(HasWidgets container) {
        bind();
        container.clear();
        container.add(view.asWidget());
        
        // Setup some default data
        currentWeek = new Date();
        view.setCurrentWeek(currentWeek);
    }
    
    /**
     * Binds handlers to the eventBus to listen for incoming events.
     */
    private void bind() {
        
    }
    
    public void setCurrentWeek(Date week) {
    	currentWeek = week;
    	view.setCurrentWeek(week);
    }
    
    /**
     * Called when the user clicks previous month.  Update the displayed month and
     * send out a month change event.
     */
    public void onPreviousWeekSelected() {
        currentWeek = GWTCDatePicker.increaseWeek(currentWeek, -1);
        
        // Update the view with the new month
        view.setCurrentWeek(currentWeek);
        
        // Send out the new month selection event
        eventBus.fireEvent(new DateSelectionEvent(currentWeek, DateSelectionEvent.DateType.Week));
    }

    public void onNextWeekSelected() {
        currentWeek = GWTCDatePicker.increaseWeek(currentWeek, 1);
        
        // Update the view with the new month
        view.setCurrentWeek(currentWeek);
        
        // Send out the new month selection event
        eventBus.fireEvent(new DateSelectionEvent(currentWeek, DateSelectionEvent.DateType.Week));
    }
}
