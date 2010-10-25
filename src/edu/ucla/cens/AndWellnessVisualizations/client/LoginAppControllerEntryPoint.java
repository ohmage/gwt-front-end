package edu.ucla.cens.AndWellnessVisualizations.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;

import edu.ucla.cens.AndWellnessVisualizations.client.common.AuthTokenLoginManager;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.AndWellnessRpcService;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.LocalAndWellnessRpcService;

public class LoginAppControllerEntryPoint implements EntryPoint {

    /**
     * Called when the webpage finished loading.  Create any necessary objects and
     * starts the AppController.
     */
    public void onModuleLoad() {
        // Initialize the rpc service and event bus for the app
        AndWellnessRpcService rpcService = new LocalAndWellnessRpcService();
        EventBus eventBus = new SimpleEventBus();
        AuthTokenLoginManager loginManager = new AuthTokenLoginManager(eventBus);
        
        // If we are already logged in redirect to the visualizations
        if (loginManager.isCurrentlyLoggedIn()) {
            Window.Location.assign("/calendar");
        }
        
        // Create and run the controller
        LoginAppController appController = new LoginAppController(rpcService, eventBus, loginManager);
        appController.go();
    }
}
