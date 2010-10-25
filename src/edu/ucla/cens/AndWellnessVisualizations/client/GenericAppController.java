package edu.ucla.cens.AndWellnessVisualizations.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.RootPanel;

import edu.ucla.cens.AndWellnessVisualizations.client.common.TokenLoginManager;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.NavigationBarPresenter;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.AndWellnessRpcService;
import edu.ucla.cens.AndWellnessVisualizations.client.view.NavigationBarView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.NavigationBarViewImpl;

/**
 * Generic controller for the AndWellness web site.  Use when there are no specific Views
 * to display (such as the home page, about page, or whatever else).  The page should be
 * rendered in the html.
 * 
 * This generic controller still uses the NavigationView which should be on every page, and
 * the LoginManager, which is a singleton that should be instantiated on every page to handle
 * logins and logouts.
 * 
 * @author jhicks
 *
 */
public class GenericAppController {
    private final EventBus eventBus;
    private final AndWellnessRpcService rpcService;
    private final TokenLoginManager loginManager;
    
    private NavigationBarView navBarView = null;
  
    public GenericAppController(AndWellnessRpcService rpcService, EventBus eventBus, 
            TokenLoginManager loginManager) {
        this.eventBus = eventBus;
        this.rpcService = rpcService;
        this.loginManager = loginManager;
        
        bind();
    }
    
    // Listen for events, take action
    private void bind() {
        // TODO: Listen for necessary events here
    }
    
    public void go() {
        // Initialize and run the navigation bar view
        if (navBarView == null) {
            navBarView = new NavigationBarViewImpl();
        }
        NavigationBarPresenter navBarPres = new NavigationBarPresenter(eventBus, navBarView, loginManager);
        navBarPres.go(RootPanel.get("navigationBarView"));
    }
}
