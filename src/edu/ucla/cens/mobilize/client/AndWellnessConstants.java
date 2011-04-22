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
    // Use to determine the deployment status
    public final static DeployStatus status = GWT.create(DeployStatus.class);
    
    // DEBUGGING CONSTANTS
    //private final static String authorizationLocationDebug = "http://127.0.0.1:8080/app/auth_token";
    //private final static String dataPointLocationDebug = "http://127.0.0.1:8080/app/q/dp";
    //private final static String configurationLocationDebug = "http://127.0.0.1:8080/app/q/config";
    private final static String authorizationLocationDebug = "http://dev1.andwellness.org/app/user/auth_token"; 
    private final static String dataPointLocationDebug = "http://dev1.andwellness.org/app/q/dp";
    private final static String configurationLocationDebug = "http://dev1.andwellness.org/app/q/config";
    private final static String userReadLocationDebug = "http://dev1.andwellness.org/app/user/read";
    private final static String campaignReadLocationDebug = "http://dev1.andwellness.org/app/campaign/read";
    
    // RELEASE CONSTANTS
    private final static String authorizationLocationRelease = "../app/user/auth_token";
    private final static String dataPointLocationRelease = "../app/q/dp";
    private final static String configurationLocationRelease = "../app/q/config";
    private final static String userReadLocationRelease = "../app/user/read";
    private final static String campaignReadLocationRelease = "../app/campaign/read";
    
    
    public static String getUserReadUrl() {
      if (status.getStatus().equals(DeployStatus.Status.DEBUG)) {
        return userReadLocationDebug;
      } else if (status.getStatus().equals(DeployStatus.Status.RELEASE)) {
        return userReadLocationRelease;
      }
      return null;
    }
    
    public static String getCampaignReadUrl() {
      if (status.getStatus().equals(DeployStatus.Status.DEBUG)) {
        return campaignReadLocationDebug;
      } else if (status.getStatus().equals(DeployStatus.Status.RELEASE)) {
        return campaignReadLocationRelease;
      }
      return null;
    }
        
    /**
     * Returns the authorization server api url based on the deployment status.
     * Returns null if the deploy status cannot be found.
     * 
     * @return The authorization API URL.
     */
    public static String getAuthorizationUrl() {
        if (status.getStatus().equals(DeployStatus.Status.DEBUG)) {
            return authorizationLocationDebug;
        }
        else if (status.getStatus().equals(DeployStatus.Status.RELEASE)) {
            return authorizationLocationRelease;
        }
        
        return null;
    }
    
    /**
     * Returns the data point server api url based on the deployment status.
     * Returns null if the deploy status cannot be found.
     * 
     * @return The data point API URL.
     */
    public static String getDataPointUrl() {
        if (status.getStatus().equals(DeployStatus.Status.DEBUG)) {
            return dataPointLocationDebug;
        }
        else if (status.getStatus().equals(DeployStatus.Status.RELEASE)) {
            return dataPointLocationRelease;
        }
        
        return null;
    }
    
    /**
     * Returns the configuration server api url based on the deployment status.
     * Returns null if the deploy status cannot be found.
     * 
     * @return The configuration API URL.
     */
    public static String getConfigurationUrl() {
        if (status.getStatus().equals(DeployStatus.Status.DEBUG)) {
            return configurationLocationDebug;
        }
        else if (status.getStatus().equals(DeployStatus.Status.RELEASE)) {
            return configurationLocationRelease;
        }
        
        return null;
    }
}
