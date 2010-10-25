package edu.ucla.cens.AndWellnessVisualizations.client.presenter;

import java.util.logging.Logger;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.ucla.cens.AndWellnessVisualizations.client.common.TokenLoginManager;
import edu.ucla.cens.AndWellnessVisualizations.client.event.RequestLogoutEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.UserLoginEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.UserLoginEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.event.UserLogoutEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.UserLogoutEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.view.NavigationBarView;


public class NavigationBarPresenter implements Presenter, NavigationBarView.Presenter {
    private final EventBus eventBus;  
    private final NavigationBarView view;
    private final TokenLoginManager loginManager;
    
    private static Logger _logger = Logger.getLogger(NavigationBarPresenter.class.getName());
    
    public NavigationBarPresenter(EventBus eventBus, NavigationBarView view, TokenLoginManager loginManager) {
        this.eventBus = eventBus;
        this.view = view;
        this.loginManager = loginManager;
        
        this.view.setPresenter(this);
    }
    
    /**
     * Attaches the view to the container.
     * 
     * @param The container to attach to.
     */
    public void go(HasWidgets container) {
        container.clear();
        container.add(view.asWidget());
        
        bind();
        
        // Rebuild the nav bar based on the current login status
        if (loginManager.isCurrentlyLoggedIn()) {
            view.updateUserName(loginManager.getUserInfo().getUserName());
        }
        view.setLoggedIn(loginManager.isCurrentlyLoggedIn());
        
        setActiveLink();
    }
    
    /**
     * Binds handlers to the eventBus.
     */
    private void bind() {
        // Listen for login and logout events, update the view accordingly
        eventBus.addHandler(UserLoginEvent.TYPE, new UserLoginEventHandler() {
            public void onUserLogin(UserLoginEvent event) {
                String userName = event.getUserInfo().getUserName();
                
                _logger.fine("Received user login for " + userName);
                
                view.updateUserName(userName);
                view.setLoggedIn(true);
                
                setActiveLink();
            }
        });
        
        eventBus.addHandler(UserLogoutEvent.TYPE, new UserLogoutEventHandler() {
            public void onUserLogout(UserLogoutEvent event) {
                view.setLoggedIn(false);
                
                setActiveLink();
            }
        });
    }

    /**
     * Requests a logout when the logout button is clicked.
     */
    public void logoutClicked() {
        eventBus.fireEvent(new RequestLogoutEvent());        
    }
    
    /**
     * Sets the active link based on the current URL, very hacky but not sure
     * the best way to do this.
     */
    private void setActiveLink() {
        String currentUrl = Window.Location.getPath();
        RegExp activeRegexp;
        
        activeRegexp = RegExp.compile("^/about");
        if (activeRegexp.test(currentUrl)) {
            view.setActiveStyle(1);
            return;
        }
        activeRegexp = RegExp.compile("^/help");
        if (activeRegexp.test(currentUrl)) {
            view.setActiveStyle(2);
            return;
        }
        activeRegexp = RegExp.compile("^/login");
        if (activeRegexp.test(currentUrl)) {
            view.setActiveStyle(3);
            return;
        }
        activeRegexp = RegExp.compile("^/");
        if (activeRegexp.test(currentUrl)) {
            view.setActiveStyle(0);
            return;
        }
    }
}
