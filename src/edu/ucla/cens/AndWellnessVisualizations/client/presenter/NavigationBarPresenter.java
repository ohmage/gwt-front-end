package edu.ucla.cens.AndWellnessVisualizations.client.presenter;

import java.util.logging.Logger;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.ucla.cens.AndWellnessVisualizations.client.common.AuthTokenLoginManager;
import edu.ucla.cens.AndWellnessVisualizations.client.event.RequestLogoutEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.UserLoginEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.UserLoginEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.event.UserLogoutEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.UserLogoutEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.view.NavigationBarView;


public class NavigationBarPresenter implements Presenter, NavigationBarView.Presenter {
    private final HandlerManager eventBus;  
    private final NavigationBarView view;
    private final AuthTokenLoginManager loginManager;
    
    private static Logger _logger = Logger.getLogger(NavigationBarPresenter.class.getName());
    
    public NavigationBarPresenter(HandlerManager eventBus, NavigationBarView view, AuthTokenLoginManager loginManager) {
        this.eventBus = eventBus;
        this.view = view;
        this.loginManager = loginManager;
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
        
        // Set the active link based on the current URL, very hacky but not sure
        // the best way to do this
        // TODO fix this
        String currentUrl = Window.Location.getPath();
        
        if ("/".equals(currentUrl) || "/index.jsp".equals(currentUrl)) {
            view.setActiveStyle(0);
        }
        if ("/about".equals(currentUrl) || "/about/index.jsp".equals(currentUrl)) {
            view.setActiveStyle(1);
        }
        if ("/help".equals(currentUrl) || "/help/index.jsp".equals(currentUrl)) {
            view.setActiveStyle(2);
        }
        if ("/login".equals(currentUrl) || "/login/index.jsp".equals(currentUrl)) {
            view.setActiveStyle(3);
        }
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
            }
        });
        
        eventBus.addHandler(UserLogoutEvent.TYPE, new UserLogoutEventHandler() {
            public void onUserLogout(UserLogoutEvent event) {
                view.setLoggedIn(false);
            }
        });
    }

    /**
     * Requests a logout when the logout button is clicked.
     */
    public void logoutClicked() {
        eventBus.fireEvent(new RequestLogoutEvent());        
    }
}
