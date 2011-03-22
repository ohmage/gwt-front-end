package edu.ucla.cens.AndWellnessVisualizations.client.presenter;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.ucla.cens.AndWellnessVisualizations.client.AndWellnessConstants;
import edu.ucla.cens.AndWellnessVisualizations.client.event.VisualizationSelectionEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.AndWellnessRpcService;
import edu.ucla.cens.AndWellnessVisualizations.client.view.VisualizationSelectionView;

public class VisualizationSelectionPresenter implements Presenter,
        VisualizationSelectionView.Presenter {

    private EventBus eventBus;
    private VisualizationSelectionView view;
    
   
    public VisualizationSelectionPresenter(AndWellnessRpcService rpcService, 
            EventBus eventBus, VisualizationSelectionView view) {
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
        
    }

    /**
     * Called from the view whenever a visualization type is selected.
     * Sends out an appropriate VisualizationSelectionEvent.
     * 
     */
    public void onSelection(VizType type) {
		switch (type) {
		case CALENDAR:
			eventBus.fireEvent(new VisualizationSelectionEvent(AndWellnessConstants.VizType.CALENDAR));
			break;
		case MAP:
			eventBus.fireEvent(new VisualizationSelectionEvent(AndWellnessConstants.VizType.MAP));
			break;
		case CHART:
			eventBus.fireEvent(new VisualizationSelectionEvent(AndWellnessConstants.VizType.CHART));
			break;
		}
	}
    
}
