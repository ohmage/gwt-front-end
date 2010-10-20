package edu.ucla.cens.AndWellnessVisualizations.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;

import edu.ucla.cens.AndWellnessVisualizations.client.common.AuthTokenLoginManager;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.AndWellnessRpcService;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.LocalAndWellnessRpcService;

public class CalendarAppControllerEntryPoint implements EntryPoint {

    /**
     * Called when the webpage finished loading.  Create any necessary objects and
     * starts the AppController.
     */
    public void onModuleLoad() {
        // Initialize the rpc service and event bus for the app
        AndWellnessRpcService rpcService = new LocalAndWellnessRpcService();
        HandlerManager eventBus = new HandlerManager(null);
        AuthTokenLoginManager loginManager = new AuthTokenLoginManager(eventBus);
        
        // Check if we are logged in, if not redirect to home
        if (loginManager.isCurrentlyLoggedIn() == false) {
            Window.Location.assign("/");
        }
        
        // Create and run the controller
        CalendarAppController appController = new CalendarAppController(rpcService, eventBus, loginManager);
        appController.go();
    }
}
