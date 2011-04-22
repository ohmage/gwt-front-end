package edu.ucla.cens.mobilize.client;

import com.google.gwt.core.client.GWT;

/**
 * Holds program wide constants.  Use DeployStatus to return different
 * constants based on the deployment status. 
 * 
 * @note DeployStatus is set in the gwt module file
 * 
 * @author jhicks
 *
 */
public class AndWellnessConstants {
  
    // DeployStatus is set in the gwt module file
    public final static DeployStatus status = GWT.create(DeployStatus.class);

    private final static String debugServerLocation = "http://dev1.andwellness.org/app/";
    private final static String releaseServerLocation = "../app/"; // same as web server
    
    // API Endpoints
    // http://lecs.cs.ucla.edu/wikis/andwellness/index.php/AndWellness_Read_API_2.2
    private final static String AUTHORIZATION   = "user/auth_token";
    private final static String USER_READ       = "user/read";
    private final static String CAMPAIGN_READ   = "campaign/read";
    private final static String CAMPAIGN_CREATE = "campaign/create";
    private final static String CAMPAIGN_UPDATE = "campaign/update";
    private final static String DATA_POINT      = "q/dp";
    
    /**
     * Returns data server url based on value of deployment status variable. 
     * This setup is useful because you can set your build script to use
     * different module files for debug vs release instead of trying to 
     * remember to change the server location every time you deploy.
     * 
     * @return String server location or null if the deployment status is not found
     */
    public static String getServerLocation() {
      String serverLocation = null;
      if (status.getStatus().equals(DeployStatus.Status.DEBUG)) {
        serverLocation = debugServerLocation;
      } else if (status.getStatus().equals(DeployStatus.Status.RELEASE)) {
        serverLocation = releaseServerLocation;
      } 
      return serverLocation;
    }
    
    public static String getUserReadUrl() {
      return getServerLocation() + USER_READ;
    }
    
    public static String getCampaignReadUrl() {
      return getServerLocation() + CAMPAIGN_READ;
    }
    
    public static String getCampaignCreateUrl() {
      return getServerLocation() + CAMPAIGN_CREATE;
    }

    public static String getCampaignUpdateUrl() {
      return getServerLocation() + CAMPAIGN_UPDATE;
    }
    
    public static String getAuthorizationUrl() {
      return getServerLocation() + AUTHORIZATION;
    }
    
    public static String getDataPointUrl() {
      return getServerLocation() + DATA_POINT;
    }
}
