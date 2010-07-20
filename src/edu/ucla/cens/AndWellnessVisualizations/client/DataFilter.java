package edu.ucla.cens.AndWellnessVisualizations.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;

public class DataFilter implements EntryPoint {
    // Static (global) so it can be easily accessed by javascript
    public static HandlerManager eventBus;
    
    @Override
    public void onModuleLoad() {
        DataFilterService rpcService = new DataFilterService();
        eventBus = new HandlerManager(null);
        AppController appViewer = new AppController(rpcService, eventBus);
        appViewer.go(RootPanel.get("gwt_test"));
    }
}
