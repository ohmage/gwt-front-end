package edu.ucla.cens.AndWellnessVisualizations.client.presenter;

import java.util.logging.Logger;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.ucla.cens.AndWellnessVisualizations.client.event.NewChunkedMobilityAwDataEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.NewChunkedMobilityAwDataEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.AndWellnessRpcService;
import edu.ucla.cens.AndWellnessVisualizations.client.view.MobilityChartVisualizationView;

public class MobilityChartPresenter implements Presenter,
	MobilityChartVisualizationView.Presenter {
	private static Logger _logger = Logger.getLogger(MobilityChartPresenter.class.getName());
	
	// Save a handle to the event bus to listen to and pass events
    private EventBus eventBus;
    
    // The view this presenter is controlling
    private MobilityChartVisualizationView view;
    
   
    public MobilityChartPresenter(AndWellnessRpcService rpcService, 
            EventBus eventBus, MobilityChartVisualizationView view) {
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
    	eventBus.addHandler(NewChunkedMobilityAwDataEvent.TYPE, new NewChunkedMobilityAwDataEventHandler() {
			public void onNewData(NewChunkedMobilityAwDataEvent event) {
				_logger.fine("Received an incoming data event");
				
				view.setDataList(event.getData());
			}
    	});
    }
}
