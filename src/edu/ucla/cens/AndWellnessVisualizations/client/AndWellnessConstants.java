package edu.ucla.cens.AndWellnessVisualizations.client;

import com.google.gwt.core.client.GWT;

/**
 * Holds program wide constants.  Use DeployStatus to return different
 * constants based on the deployment status
 * 
 * @author jhicks
 *
 */
public class AndWellnessConstants {
    // Use to determine the deployment status
    public final static DeployStatus status = GWT.create(DeployStatus.class);
    
    // DEBUGGING CONSTANTS
    private final static String authorizationLocationDebug = "http://127.0.0.1:8080/app/auth_token";
    private final static String dataPointLocationDebug = "http://127.0.0.1:8080/app/q/dp";
    private final static String configurationLocationDebug = "http://127.0.0.1:8080/app/q/config";
    private final static String mobilityLocationDebug = "http://127.0.0.1:8080/app/q/m";
    private final static String googleMapsApiKeyDebug = "ABQIAAAA5ZXjE5Rq-KGomi3qK8oshxRi_j0U6kJrkFvY4-OX2XYmEAa76BQ2ZkOydhEh44vXPVI_djTFw81U0w";
    
    // RELEASE CONSTANTS
    private final static String authorizationLocationRelease = "../app/auth_token";
    private final static String dataPointLocationRelease = "../app/q/dp";
    private final static String configurationLocationRelease = "../app/q/config";
    private final static String mobilityLocationRelease = "../app/q/m";
    private final static String googleMapsApiKeyRelease = "ABQIAAAA5ZXjE5Rq-KGomi3qK8oshxTSFov2pRIXTMXmJtOHSJVnImuOrRRbrQPGvvdbztyn6nYzKy4ZFDUXrw";
    
        
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

    /**
     * Returns the mobility api url based on the deployment status.
     * Returns null if the status cannot be found
     * 
     * @return The mobility API URL.
     */
	public static String getMobilityUrl() {
		if (status.getStatus().equals(DeployStatus.Status.DEBUG)) {
            return mobilityLocationDebug;
        }
        else if (status.getStatus().equals(DeployStatus.Status.RELEASE)) {
            return mobilityLocationRelease;
        }
		
		return null;
	}
	
	/**
	 * Return the gogole maps api key based on the deployment status
	 * 
	 * @return The google maps API key.
	 */
	public static String getMapsApiKeyl() {
		if (status.getStatus().equals(DeployStatus.Status.DEBUG)) {
            return googleMapsApiKeyDebug;
        }
        else if (status.getStatus().equals(DeployStatus.Status.RELEASE)) {
            return googleMapsApiKeyRelease;
        }
		
		return null;
	}
}
