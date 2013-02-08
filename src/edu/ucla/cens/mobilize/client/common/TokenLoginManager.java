package edu.ucla.cens.mobilize.client.common;

import java.util.logging.Logger;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Cookies;

import edu.ucla.cens.mobilize.client.AwConstants;
import edu.ucla.cens.mobilize.client.event.UserLoginEvent;
import edu.ucla.cens.mobilize.client.exceptions.NotLoggedInException;

/**
 * TokenLoginManager acts as an abstraction of the login information stored
 * in a local cookie.  Upon login, the server returns an authorization token that
 * authenticates all future calls to the server API until expiration.  Since we want
 * to keep this token around if the user were to refresh or reload the webpage, the token
 * is stored in a local cookie.  The logged in user is also stored.
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
    
    private final EventBus eventBus;   
    
    // Logging utility
    private static Logger _logger = Logger.getLogger(TokenLoginManager.class.getName());
    
    public TokenLoginManager(EventBus eventBus) {
        this.eventBus = eventBus;
    };
        
    /**
     * Returns the stored authorization token.
     * 
     * @return The authorization token.  Throws NotLoggedInException if not logged in.
     */
    public String getAuthorizationToken() {
        String authToken = Cookies.getCookie(AwConstants.cookieAuthToken);
        
        if (authToken == null) {
        	_logger.fine("could not find auth_token cookie");
            throw new NotLoggedInException("Cannot find the authorization token.");
        }
        else {
            return authToken;
        }
    }
    
    /**
     * Call when a user successfully logs in and obtains an authorization token
     * from the server. Fires a UserLoginEvent into the eventBus.
     *  
     * @param userName The logged-in user's username. 
     */
    public void login(String userName) {
        
    	eventBus.fireEvent(new UserLoginEvent(userName));
    }
}
