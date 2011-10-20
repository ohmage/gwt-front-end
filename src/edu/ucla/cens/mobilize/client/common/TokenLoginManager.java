package edu.ucla.cens.mobilize.client.common;

import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Cookies;

import edu.ucla.cens.mobilize.client.AwConstants;
import edu.ucla.cens.mobilize.client.event.RequestLogoutEvent;
import edu.ucla.cens.mobilize.client.event.RequestLogoutEventHandler;
import edu.ucla.cens.mobilize.client.event.UserLoginEvent;
import edu.ucla.cens.mobilize.client.event.UserLogoutEvent;
import edu.ucla.cens.mobilize.client.exceptions.NotLoggedInException;

/**
 * TokenLoginManager acts as an abstraction of the login information stored
 * in a local cookie.  Upon login, the server returns an authorization token that
 * authenticates all future calls to the server API until expiration.  Since we want
 * to keep this token around if the user were to refresh or reload the webpage, the token
 * is stores in a local cookie.  The logged in user is also stored.
 * 
 * The purpose of this class to is to know whether or not the system is in a logged in state.
 * Each module should load the login manager, then listen for UserLogoutEvents and UserLoginEvents
 * to take action when a user logs off
 * or back in.  If the user is already logged in, the module can ask the login manager for the
 * stored user information.
 * 
 * @author jhicks
 *
 */
public class TokenLoginManager {
    private boolean currentlyLoggedIn = false;
    
    private final EventBus eventBus;
    
    // Constant cookie names
    private final static String AUTH_TOKEN_COOKIE = AwConstants.cookieAuthToken;
    private final static String USER_NAME_COOKIE = AwConstants.cookieUserName;
    
    // Logging utility
    private static Logger _logger = Logger.getLogger(TokenLoginManager.class.getName());
    
    public TokenLoginManager(EventBus eventBus) {
        this.eventBus = eventBus;
        
        // Initialize the manager by checking the currently set cookies
        init();
    };
    
    /**
     * Checks to see if any of our cookies are already set.  If the username
     * and authtoken are set, switch to logged in and pass a UserLoginEvent
     */
    public void init() {
        Collection<String> currentCookieNames = Cookies.getCookieNames();
        
        // Listen for RequestLogoutEvents to handle a module requesting a logout
        eventBus.addHandler(RequestLogoutEvent.TYPE, new RequestLogoutEventHandler() {
            public void requestLogout(RequestLogoutEvent event) {
                _logger.info("Logout requested");
                
                logOut();
            }
        });
        
        if (currentCookieNames.contains(AUTH_TOKEN_COOKIE) &&
                currentCookieNames.contains(USER_NAME_COOKIE)) {           
            // Switch us to currently logged in
            currentlyLoggedIn = true;
            
            _logger.info("Initialzing login manager with user name: " + Cookies.getCookie(USER_NAME_COOKIE));
        }
        else {
            _logger.info("No login information found in cookies");
            // Clear the cookies in case they are in a bad state
            clearUserInformation();
        }
        
    }
    
    /**
     * Call when a user successfully logs in and obtains an authorization token
     * from the server.  Saves the user information into a cookie and
     * fires a UserLoginEvent into the eventBus.
     * 
     * @param authToken 
     * @param userName 
     * @param campaignList 
     */
    public void loginWithAuthToken(String authToken, String userName) {
        Cookies.setCookie(AUTH_TOKEN_COOKIE, authToken, null, null, "/", false);
        Cookies.setCookie(USER_NAME_COOKIE, userName, null, null, "/", false);
        
        _logger.fine("Logging in as user: " + authToken);
        
        currentlyLoggedIn = true;
        
        eventBus.fireEvent(new UserLoginEvent(userName));
    }
    
    /**
     * Clears all user information and fires a UserLogoutEvent.
     */
    public void logOut() {
        currentlyLoggedIn = false;
        
        _logger.fine("Logging out");
        
        clearUserInformation();
        
        eventBus.fireEvent(new UserLogoutEvent());
    }
    
    /**
     * Returns the stored authorization token.
     * 
     * @return The authorization token.  Throws NotLoggedInException if not logged in.
     */
    public String getAuthorizationToken() {
        String authToken = Cookies.getCookie(AUTH_TOKEN_COOKIE);
        
        if (authToken == null) {
            throw new NotLoggedInException("Cannot find the authorization token.");
        }
        else {
            return authToken;
        }
    }
    
    /**
     * Returns whether the user is logged in or not.
     * @return true if the user is logged in, false if not.
     */
    public boolean isCurrentlyLoggedIn() {
        return currentlyLoggedIn;
    }
    
      
    /**
     * Returns the current user name if logged in, null otherwise.
     * 
     * @return The currently logged in user name.
     */
    public String getLoggedInUserName() {
        // First check if we are logged in
        if (!isCurrentlyLoggedIn()) {
            _logger.warning("Cannot get user name if not logged in.");
            return null;
        }
        
        // Next check if the user name cookie is set (it should be if we are logged in
        // but check anyway for debugging)
        Collection<String> currentCookieNames = Cookies.getCookieNames();
        if (!currentCookieNames.contains(USER_NAME_COOKIE)) {
            _logger.severe("Logged in but no user name found.");
            return null;
        }
        
        // Now return the user name
        return Cookies.getCookie(USER_NAME_COOKIE);
    }
    
    /**
     * Clears all currently stored user info from the cookies.
     */
    private void clearUserInformation() {
        // removeCookie call doesn't seem to work, instead settings cookies to expire now
        Date removeExpire = new Date();
        
        Cookies.setCookie(AUTH_TOKEN_COOKIE, null, removeExpire, null, "/", false);
        Cookies.setCookie(USER_NAME_COOKIE, null, removeExpire, null, "/", false);
    }
}
