package edu.ucla.cens.AndWellnessVisualizations.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.RootPanel;

import edu.ucla.cens.AndWellnessVisualizations.client.common.TokenLoginManager;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.LoginBoxPresenter;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.NavigationBarPresenter;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.AndWellnessRpcService;
import edu.ucla.cens.AndWellnessVisualizations.client.view.LoginBoxView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.LoginBoxViewImpl;
import edu.ucla.cens.AndWellnessVisualizations.client.view.NavigationBarView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.NavigationBarViewImpl;

/**
 * 
 * @author jhicks
 *
 */
public class LoginAppController {
    private final EventBus eventBus;
    private final AndWellnessRpcService rpcService;
    private final TokenLoginManager loginManager;
    
    private NavigationBarView navBarView = null;
    private LoginBoxView loginBoxView = null;
  
    public LoginAppController(AndWellnessRpcService rpcService, EventBus eventBus, 
            TokenLoginManager loginManager) {
        this.eventBus = eventBus;
        this.rpcService = rpcService;
        this.loginManager = loginManager;
        
        bind();
    }
    
    // Listen for events, take action
    private void bind() {

    }
    
    public void go() {
        // Initialize and run the navigation bar view
        if (navBarView == null) {
            navBarView = new NavigationBarViewImpl();
        }
        NavigationBarPresenter navBarPres= new NavigationBarPresenter(eventBus, navBarView, loginManager);
        navBarPres.go(RootPanel.get("navigationBarView"));
        
        if (loginBoxView == null) {
            loginBoxView = new LoginBoxViewImpl();
        }
        LoginBoxPresenter loginBoxPres = new LoginBoxPresenter(rpcService, eventBus, loginBoxView, loginManager);
        loginBoxPres.go(RootPanel.get("loginBoxView"));
    }
}
