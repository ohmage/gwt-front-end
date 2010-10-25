package edu.ucla.cens.AndWellnessVisualizations.client.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Cookies;

import edu.ucla.cens.AndWellnessVisualizations.client.event.RequestLogoutEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.RequestLogoutEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.event.UserLoginEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.UserLogoutEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.model.UserInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.utils.CollectionUtils;

/**
 * TokenLoginManager acts as an abstraction of the login information stored
 * in a local cookie.  Upon login, the server returns an authorization token that
 * authenticates all future calls to the server API until expiration.  Since we want
 * to keep this token around if the user were to refresh or reload the webpage, the token
 * is stores in a local cookie.  The logged in user, the list of campaigns of which the user
 * is a member, and the currently selected campaign is also stored.
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
    private final static String AUTH_TOKEN_COOKIE = "authToken";
    private final static String USER_NAME_COOKIE = "userName";
    private final static String CAMPAIGN_LIST_COOKIE = "campaignList";
    private final static String SELECTED_CAMPAIGN_COOKIE = "selectedCampaign";
    private final static String DELIMITER = ",";
    
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
            
            // Fire the logged in event
            // maybe don't do this, make modules ask login manager directly if the user is already logged in
            //eventBus.fireEvent(new UserLoginEvent(getUserInfo()));
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
    public void loginWithAuthToken(String authToken, String userName, List<String> campaignList) {
        Cookies.setCookie(AUTH_TOKEN_COOKIE, authToken, null, null, "/", false);
        Cookies.setCookie(USER_NAME_COOKIE, userName, null, null, "/", false);
        Cookies.setCookie(CAMPAIGN_LIST_COOKIE, CollectionUtils.join(campaignList, DELIMITER), null, null, "/", false);
        
        _logger.fine("Loggin in as user: " + authToken + " with campaignList: " + CollectionUtils.join(campaignList, DELIMITER));
        
        currentlyLoggedIn = true;
        
        eventBus.fireEvent(new UserLoginEvent(getUserInfo()));
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
     * @return The authorization token.  NULL if not set.
     */
    public String getAuthorizationToken() {
        return Cookies.getCookie(AUTH_TOKEN_COOKIE);
    }
    
    /**
     * Returns whether the user is logged in or not.
     * @return true if the user is logged in, false if not.
     */
    public boolean isCurrentlyLoggedIn() {
        return currentlyLoggedIn;
    }
    
    /**
     * Generates a UserInfo object from the current cookies.  Returns NULL if the
     * userName cookie is not set.
     * 
     * @return A UserInfo object.
     */
    public UserInfo getUserInfo() {
        UserInfo userInfo = null;
        
        Collection<String> currentCookieNames = Cookies.getCookieNames();
        if (currentCookieNames.contains(AUTH_TOKEN_COOKIE) &&
                currentCookieNames.contains(USER_NAME_COOKIE)) {
            String userName = Cookies.getCookie(USER_NAME_COOKIE);
            String campaignListString = Cookies.getCookie(CAMPAIGN_LIST_COOKIE);
            List<String> campaignList = new ArrayList<String>();
            String selectedCampaign = Cookies.getCookie(SELECTED_CAMPAIGN_COOKIE);
            
            // Create the new UserInfo
            userInfo = new UserInfo(userName);
            
            _logger.fine("Reading in user " + userName);
            
            // Check to see if we have a stored campaign list (we should)
            if (campaignListString != null) {
                campaignList.addAll(Arrays.asList(campaignListString.split(DELIMITER)));
                userInfo.setCampaignMembershipList(campaignList);
                
                _logger.fine("Reading in campaignList: " + campaignListString);
            }
            
            // Check to see if the user has selected a campaign (might not have happened)
            if (selectedCampaign != null) {
                userInfo.setSelectedCampaign(Integer.parseInt(selectedCampaign));
            }
            // Unselected campaign is represented by -1
            else {
                userInfo.setSelectedCampaign(-1);
            }
        }
        
        return userInfo;
    }
    
    /**
     * Clears all currently stored user info from the cookies.
     */
    private void clearUserInformation() {
        // removeCookie call doesn't seem to work, instead settings cookies to expire now
        Date removeExpire = new Date();
        
        Cookies.setCookie(AUTH_TOKEN_COOKIE, null, removeExpire, null, "/", false);
        Cookies.setCookie(USER_NAME_COOKIE, null, removeExpire, null, "/", false);
        Cookies.setCookie(CAMPAIGN_LIST_COOKIE, null, removeExpire, null, "/", false);
        Cookies.setCookie(SELECTED_CAMPAIGN_COOKIE, null, removeExpire, null, "/", false);
    }
}
