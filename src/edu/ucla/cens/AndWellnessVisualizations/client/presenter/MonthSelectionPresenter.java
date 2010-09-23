package edu.ucla.cens.AndWellnessVisualizations.client.presenter;

import java.util.Date;

import com.google.code.p.gwtchismes.client.GWTCDatePicker;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.ucla.cens.AndWellnessVisualizations.client.event.MonthSelectionEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.AndWellnessRpcService;
import edu.ucla.cens.AndWellnessVisualizations.client.view.MonthSelectionView;

public class MonthSelectionPresenter implements Presenter,
        MonthSelectionView.Presenter {

    private Date currentMonth;
    private HandlerManager eventBus;
    private MonthSelectionView view;
    
   
    public MonthSelectionPresenter(AndWellnessRpcService rpcService, 
            HandlerManager eventBus, MonthSelectionView view) {
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
        currentMonth = new Date();
        view.setCurrentMonth(currentMonth);
    }
    
    /**
     * Binds handlers to the eventBus to listen for incoming events.
     */
    private void bind() {
        
    }
    
    /**
     * Called when the user clicks previous month.  Update the displayed month and
     * send out a month change event.
     */
    public void onPreviousMonthSelected() {
        currentMonth = GWTCDatePicker.increaseMonth(currentMonth, -1);
        
        // Update the view with the new month
        view.setCurrentMonth(currentMonth);
        
        // Send out the new month selection event
        eventBus.fireEvent(new MonthSelectionEvent(currentMonth));
    }

    public void onNextMonthSelected() {
        currentMonth = GWTCDatePicker.increaseMonth(currentMonth, 1);
        
        // Update the view with the new month
        view.setCurrentMonth(currentMonth);
        
        // Send out the new month selection event
        eventBus.fireEvent(new MonthSelectionEvent(currentMonth));
    }
}
