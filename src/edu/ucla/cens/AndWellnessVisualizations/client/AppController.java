package edu.ucla.cens.AndWellnessVisualizations.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.ucla.cens.AndWellnessVisualizations.client.common.DataFilterDropDownDefinitionsFactory;
import edu.ucla.cens.AndWellnessVisualizations.client.event.SwitchViewEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.model.MainViewState;
import edu.ucla.cens.AndWellnessVisualizations.client.model.UserInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.Presenter;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.DataFilterPresenter;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.DataFilterService;
import edu.ucla.cens.AndWellnessVisualizations.client.view.DataFilterView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.DataFilterViewImpl;

/**
 * The main controller for the GWT application.  Holds the event bus,
 * the rpc service, and the main container.  Also initializes and launches
 * presenters and their associated views.
 * 
 * @author jhicks
 *
 */
public class AppController implements Presenter, ValueChangeHandler<String> {
    private final HandlerManager eventBus;
    private final DataFilterService rpcService; 
    private HasWidgets container;
    private DataFilterView<UserInfo> dataFilterView = null;
  
    
    public AppController(DataFilterService rpcService, HandlerManager eventBus) {
        this.eventBus = eventBus;
        this.rpcService = rpcService;
        bind();
    }
    
    // Listen for events, take action
    private void bind() {
        History.addValueChangeHandler(this);  
        
        // Declare two javascript functions that can be called from javscript to
        // push events
        initEventsJS();
    }
    
    public void go(HasWidgets container) {
        this.container = container;
        
        if ("".equals(History.getToken())) {
            History.newItem("datafilter");
        }
        else {
            History.fireCurrentHistoryState();
        }
    }

    public void onValueChange(ValueChangeEvent<String> event) {
        String token = event.getValue();
        
        if (token != null) {
            if (token.equals("datafilter")) {
                if (dataFilterView == null) {
                    dataFilterView = new DataFilterViewImpl<UserInfo>();
                }
                new DataFilterPresenter(rpcService, eventBus, dataFilterView, 
                        DataFilterDropDownDefinitionsFactory
                        .getDataFilterDropDownDefinitions())
                    .go(container);
            }
        }
    }
    
    public static void triggerSwitchToUploadViewEventJS() {
        DataFilter.eventBus.fireEvent(new SwitchViewEvent(MainViewState.UPLOADVIEW));
    }
    
    public static void triggerSwitchToGraphViewEventJS() {
        DataFilter.eventBus.fireEvent(new SwitchViewEvent(MainViewState.GRAPHVIEW));
    }
    
    private native void initEventsJS () /*-{
        $wnd.switchToUploadViewEvent = function () {
            @edu.ucla.cens.AndWellnessVisualizations.client.AppController::triggerSwitchToUploadViewEventJS()();
        };
        $wnd.switchToGraphViewEvent = function () {
            @edu.ucla.cens.AndWellnessVisualizations.client.AppController::triggerSwitchToGraphViewEventJS()();
        };
    }-*/;
}
