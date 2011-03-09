package edu.ucla.cens.AndWellnessVisualizations.client.presenter;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.ucla.cens.AndWellnessVisualizations.client.event.NewMobilityDataPointAwDataEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.NewMobilityDataPointAwDataEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.AndWellnessRpcService;
import edu.ucla.cens.AndWellnessVisualizations.client.view.MobilityMapVisualizationView;

public class MobilityMapPresenter implements Presenter,
	MobilityMapVisualizationView.Presenter {
	
	// Save a handle to the event bus to listen to and pass events
    private EventBus eventBus;
    
    // The view this presenter is controlling
    private MobilityMapVisualizationView view;
    
   
    public MobilityMapPresenter(AndWellnessRpcService rpcService, 
            EventBus eventBus, MobilityMapVisualizationView view) {
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
    }
    
    /**
     * Binds handlers to the eventBus to listen for incoming events.
     */
    private void bind() {
        // Listen for incoming mobility data, pass to the view
    	eventBus.addHandler(NewMobilityDataPointAwDataEvent.TYPE, new NewMobilityDataPointAwDataEventHandler() {
			public void onNewData(NewMobilityDataPointAwDataEvent event) {
				view.setDataList(event.getData());
			}
    	});
    }
}
