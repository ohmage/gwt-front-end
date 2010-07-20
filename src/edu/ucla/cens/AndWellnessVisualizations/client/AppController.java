package edu.ucla.cens.AndWellnessVisualizations.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.ucla.cens.AndWellnessVisualizations.client.event.SwitchToGraphViewEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.SwitchToUploadViewEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.Presenter;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.DataFilterPresenter;
import edu.ucla.cens.AndWellnessVisualizations.client.view.DataFilterView;

public class AppController implements Presenter, ValueChangeHandler<String> {
    private final HandlerManager eventBus;
    private final DataFilterService rpcService; 
    private HasWidgets container;
  
    
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
    
    @Override
    public void go(HasWidgets container) {
        this.container = container;
        
        if ("".equals(History.getToken())) {
            History.newItem("datafilter");
        }
        else {
            History.fireCurrentHistoryState();
        }
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        String token = event.getValue();
        
        if (token != null) {
            Presenter presenter = null;

            if (token.equals("datafilter")) {
                presenter = new DataFilterPresenter(rpcService, eventBus, new DataFilterView());
            }
          
            if (presenter != null) {
                presenter.go(container);
            }
        }
    }
    
    public static void triggerSwitchToUploadViewEventJS() {
        DataFilter.eventBus.fireEvent(new SwitchToUploadViewEvent());
    }
    
    public static void triggerSwitchToGraphViewEventJS() {
        DataFilter.eventBus.fireEvent(new SwitchToGraphViewEvent());
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
