package edu.ucla.cens.AndWellnessVisualizations.client;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

import edu.ucla.cens.AndWellnessVisualizations.client.common.AuthTokenLoginManager;
import edu.ucla.cens.AndWellnessVisualizations.client.event.UserLoginEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.UserLoginEventHandler;
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
    private final HandlerManager eventBus;
    private final AndWellnessRpcService rpcService;
    private final AuthTokenLoginManager loginManager;
    
    private NavigationBarView navBarView = null;
    private LoginBoxView loginBoxView = null;
  
    public LoginAppController(AndWellnessRpcService rpcService, HandlerManager eventBus, 
            AuthTokenLoginManager loginManager) {
        this.eventBus = eventBus;
        this.rpcService = rpcService;
        this.loginManager = loginManager;
        
        bind();
    }
    
    // Listen for events, take action
    private void bind() {
        // Upon successful login redirect to calendar
        eventBus.addHandler(UserLoginEvent.TYPE, new UserLoginEventHandler() {
            public void onUserLogin(UserLoginEvent event) {
                Window.Location.assign("/calendar");
            }
        });
    }
    
    public void go() {
        // Initialize and run the navigation bar view
        if (navBarView == null) {
            navBarView = new NavigationBarViewImpl();
        }
        NavigationBarPresenter navBarPres= new NavigationBarPresenter(eventBus, navBarView, loginManager);
        navBarView.setPresenter(navBarPres);
        navBarPres.go(RootPanel.get("navigationBarView"));
        
        if (loginBoxView == null) {
            loginBoxView = new LoginBoxViewImpl();
        }
        LoginBoxPresenter loginBoxPres = new LoginBoxPresenter(rpcService, eventBus, loginBoxView, loginManager);
        loginBoxPres.go(RootPanel.get("loginBoxView"));
    }
}
