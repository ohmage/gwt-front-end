package edu.ucla.cens.AndWellnessVisualizations.client.common;

import java.util.logging.Logger;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;

import edu.ucla.cens.AndWellnessVisualizations.client.AndWellnessConstants;
import edu.ucla.cens.AndWellnessVisualizations.client.DeployStatus;
import edu.ucla.cens.AndWellnessVisualizations.client.event.UserLoginEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.UserLoginEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.event.UserLogoutEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.UserLogoutEventHandler;

/**
 * AuthorizationManager authorizes access to each webpage.  Each page should
 * load a single AuthorizationManager instance.  The AuthorizationManager will
 * automatically grab the current URL, and authorize access based on the current
 * logged in/out status.  The manager will listen for login and logout events and
 * re-quthorize accordingly.
 * 
 * Authorization is based on the PageAuthorize enum.  The manager runs through the 
 * enum in order until it finds a URL that matches the current URL.  The URL is then
 * authorized and the manager takes the appropriate action.  Only the first match is considered.
 * 
 * If the URL authorizes, no action is taken.  If the URL does not authorize, the manager
 * will redirect to the given URL in redirectIfAuthFail.  
 * 
 * If the URL is NOT found, the manager will automatically redirect to the root URL.
 * 
 * @author jhicks
 *
 */
public class AuthorizationManager {
    /**
     * ProtectionLevels defines the three possible protection levels for URLs.
     * The URL can either have NO protection, require the user to be logged out,
     * or require the user to be logged in.
     */
    private enum ProtectionLevel {
        NONE, 
        LOGGED_OUT, 
        LOGGED_IN
    }
    
    /**
     * The page authorization enum.  Every URL must be here, or the manager will
     * redirect back to the root URL.
     */
    private enum PageAuthorize {
        ABOUT ("^/about", ProtectionLevel.NONE, null), // About page is unrestricted
        HELP ("^/help", ProtectionLevel.NONE, null), // Help page is unrestricted
        LOGIN ("^/login", ProtectionLevel.LOGGED_OUT, "/map"), // Only show login page if not logged in
        CALENDAR ("^/calendar", ProtectionLevel.LOGGED_IN, "/"), // Must be logged in to see visualizations
        MAP ("^/map", ProtectionLevel.LOGGED_IN, "/"), // Must be logged in to see visualizations
        CHART ("^/chart", ProtectionLevel.LOGGED_IN, "/"), // Must be logged in to see visualizations
        ROOT ("^/", ProtectionLevel.LOGGED_OUT, "/map"); // If logged in, show visualizations instead of home
        
        
        public final RegExp urlRegexp;
        public final ProtectionLevel protectionLevel;
        public final String redirectIfAuthFail;
        
        PageAuthorize(String regexpPattern, ProtectionLevel protectionLevel, String redirectIfAuthFail) {
            // Compile the regexp String into a GWT RegExp
            this.urlRegexp = RegExp.compile(regexpPattern);
            
            this.protectionLevel = protectionLevel;
            this.redirectIfAuthFail = redirectIfAuthFail; 
        }
    }
    
    // AuthorizationManager fields
    private static AuthorizationManager authorizationManager = null;
    private final EventBus eventBus;
    private final TokenLoginManager loginManager;
    
    // GWT logging utility
    private static Logger _logger = Logger.getLogger(AuthorizationManager.class.getName());
    
    // Make this private, only instantiate through static functions
    private AuthorizationManager(EventBus eventBus, TokenLoginManager loginManager) {
        this.eventBus = eventBus;
        this.loginManager = loginManager;

        // Listen for incoming logout or login events
        bind();
    }
    
    /**
     * Instantiates the authorization manager and authorizes the current url.
     * 
     * @param eventBus The current system event bus.
     * @param loginManager The instantiated login manager.
     */
    public static void instantiate(EventBus eventBus, TokenLoginManager loginManager) {
        if (authorizationManager == null) {
            authorizationManager = new AuthorizationManager(eventBus, loginManager);
            // Now authorize our current URL
            authorizationManager.authorizeUrl(Window.Location.getPath());
        }
    }
    
    /**
     * Binds handlers to the user login and user logout events, reauthorize on receive.
     */
    private void bind() {
        eventBus.addHandler(UserLoginEvent.TYPE, new UserLoginEventHandler() {
            public void onUserLogin(UserLoginEvent event) {
                // Re authorize now that the login status has changed
                authorizeUrl(Window.Location.getPath());
            }           
        });
        
        eventBus.addHandler(UserLogoutEvent.TYPE, new UserLogoutEventHandler() {
            public void onUserLogout(UserLogoutEvent event) {
                // Re authorize now that the login status has changed
                authorizeUrl(Window.Location.getPath());  
            }
        });
    }
    
    /**
     * Runs over every URL in the PageAuthorize enum until it finds one
     * that matches the passed in URL.  On success the function returns,
     * on fail the function calls a redirect which ends execution of this
     * code.
     */
    private void authorizeUrl(String urlToAuthorize) {
        for (PageAuthorize authRecord: PageAuthorize.values()) {
            // Check to see if the regexp matches the url
            if (authRecord.urlRegexp.test(urlToAuthorize)) {
                // use the PageAuthorize record to authorize access
                switch (authRecord.protectionLevel) {
                case NONE:
                    // There is no protection on this URL, simply return
                    _logger.fine("URL authorize succeeded as no protection: " + urlToAuthorize);
                    break;
                case LOGGED_OUT:
                    // Make sure we are logged out
                    if (loginManager.isCurrentlyLoggedIn()) {
                        // Auth fails, redirect
                        _logger.info("URL authorize failed as LOGGED_OUT: " + urlToAuthorize);
                        
                        // Turn off redirects when debugging
                        if (AndWellnessConstants.status.getStatus().equals(DeployStatus.Status.RELEASE)) {
                            Window.Location.assign(authRecord.redirectIfAuthFail);
                        }
                    }
                    else {
                        // Auth succeeded, return
                        _logger.fine("URL authorize succeeded as LOGGED_OUT: " + urlToAuthorize);
                    }
                    break;
                case LOGGED_IN:
                    // Make sure we are logged in
                    if (!loginManager.isCurrentlyLoggedIn()) {
                        // Auth fails, redirect
                        _logger.info("URL authorize failed as LOGGED_IN: " + urlToAuthorize);
                        
                        // Turn off redirects when debugging
                        if (AndWellnessConstants.status.getStatus().equals(DeployStatus.Status.RELEASE)) {
                            Window.Location.assign(authRecord.redirectIfAuthFail);
                        }
                    }
                    else {
                        // Auth succeeded, return
                        _logger.fine("URL authorize succeeded as LOGGED_IN: " + urlToAuthorize);
                    }
                    break;
                default:
                    // If we get this far, assume the URL was NOT found
                    _logger.warning("Did not find URL in the PageAuthorize enum: " + urlToAuthorize);
                    
                    // Turn off redirects when debugging
                    if (AndWellnessConstants.status.getStatus().equals(DeployStatus.Status.RELEASE)) {
                        Window.Location.assign("/");
                    }
                    break;
                }
                
                // Return once we find the first matching authRecord
                return;
            }
        }
    }
}
