package edu.ucla.cens.AndWellnessVisualizations.client.presenter;

import java.util.Date;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.ucla.cens.AndWellnessVisualizations.client.common.SetModel;
import edu.ucla.cens.AndWellnessVisualizations.client.event.DateSelectionEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.MonthSelectionEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.MonthSelectionEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.AndWellnessRpcService;
import edu.ucla.cens.AndWellnessVisualizations.client.view.DateSelectionView;

public class DateSelectionPresenter implements Presenter,
        DateSelectionView.Presenter {

    private EventBus eventBus;
    private DateSelectionView view;
    
    // The currently selected date
    SetModel<Date> setDate = new SetModel<Date>();
   
    // The currently displayed month
    private Date month;
    
    public DateSelectionPresenter(AndWellnessRpcService rpcService, 
            EventBus eventBus, DateSelectionView view) {
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
        setDate.updateSetItem(new Date());
        month = new Date();
        view.setCurrentMonth(month);
    }
    
    /**
     * Binds handlers to the eventBus to listen for incoming events.
     */
    private void bind() {
        // Listen for month change events
    	eventBus.addHandler(MonthSelectionEvent.TYPE,
                new MonthSelectionEventHandler() {
                    public void onSelection(MonthSelectionEvent event) {
                        
                        month = event.getMonthSelection();
                        
                        if (view != null) {
                            view.setCurrentMonth(month);
                        }
                    }
            });
    }
   
	public void onDaySelection(Date selection) {
		setDate.updateSetItem(selection);
		
		// Send out a date selection event
		eventBus.fireEvent(new DateSelectionEvent(setDate.getSetItem()));
	}
}
