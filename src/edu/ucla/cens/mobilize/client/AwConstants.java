package edu.ucla.cens.mobilize.client;

import com.google.gwt.core.client.GWT;

/**
 * Holds program wide constants.  Use DeployStatus to return different
 * constants based on the deployment status. 
 * 
 * @note DeployStatus is set in the gwt module file
 * 
 * @author jhicks
 * @author shlurbee
 *
 */
public class AwConstants {
  
    // DeployStatus is set in the gwt module file
    public final static DeployStatus status = GWT.create(DeployStatus.class);

    // login cookie management
    public final static String cookieAuthToken = "authToken";
    public final static String cookieUserName = "userName";
    
    public final static String apiClientString = "ohmage-gwt";
    
    // special token that tells api to return all values, useful for api params
    // that don't default to all when omitted
    public final static String specialAllValuesToken = "urn:ohmage:special:all";

    // Google maps thumbnail size in pixels
    public final static int MAPS_THUMBNAIL_WIDTH = 50;
    public final static int MAPS_THUMBNAIL_HEIGHT = 50;
    
    //private final static String debugServerLocation = "https://dev1.andwellness.org";
    private final static String debugServerLocation = "http://localhost:8080";
    //private final static String debugServerLocation = "https://dev.mobilizingcs.org";
    private final static String releaseServerLocation = ".."; // same as web server

    // API Endpoints
    // http://lecs.cs.ucla.edu/wikis/andwellness/index.php/Main_Page#2.9.2B_Server_APIs
    public enum AwUri {
     AUTHORIZATION("/app/user/auth_token"),
     APP_CONFIG_READ("/app/config/read"),
     USER_ACTIVATE("/app/user/activate"),
     USER_AUTH("/app/user/auth"),
     USER_CHANGE_PASSWORD("/app/user/change_password"),
     USER_CREATE("/app/user/create"),
     USER_DELETE("/app/user/delete"),
     USER_INFO_READ ("/app/user_info/read"),
     USER_READ("/app/user/read"),
     USER_REGISTER("/app/user/register"),
     USER_SEARCH("/app/user/search"),
     USER_UPDATE("/app/user/update"),
     REGISTRATION_READ("/app/registration/read"),
     CAMPAIGN_READ("/app/campaign/read"),
     CAMPAIGN_CREATE("/app/campaign/create"),
     CAMPAIGN_UPDATE("/app/campaign/update"),
     CAMPAIGN_DELETE("/app/campaign/delete"),
     CAMPAIGN_SEARCH("/app/campaign/search"),
     SURVEY_RESPONSE_READ("/app/survey_response/read"),
     SURVEY_RESPONSE_UPDATE("/app/survey_response/update"),
     SURVEY_RESPONSE_DELETE("/app/survey_response/delete"),
     CLASS_READ("/app/class/read"),
     CLASS_CREATE("/app/class/create"),
     CLASS_UPDATE("/app/class/update"),
     CLASS_DELETE("/app/class/delete"),
     CLASS_SEARCH("/app/class/search"),
     CLASS_ROSTER_READ("/app/class/roster/read"),
     CLASS_ROSTER_UPDATE("/app/class/roster/update"),
     IMAGE_READ("/app/image/read"),
     DOCUMENT_READ("/app/document/read"),
     DOCUMENT_READ_CONTENTS("/app/document/read/contents"),
     DOCUMENT_CREATE("/app/document/create"),
     DOCUMENT_UPDATE("/app/document/update"),
     DOCUMENT_DELETE("/app/document/delete"),
     DOCUMENT_DOWNLOAD("/app/document/read/contents"),
     VISUALIZATION_URL("/app/viz"),
     AUDIT_READ("/app/audit/read"),
     MOBILITY_READ("/app/mobility/read"),
     MOBILITY_READ_CHUNKED("/app/mobility/read/chunked"),
     MOBILITY_DATES_READ("/app/mobility/dates/read");
     
     private AwUri(String uri) {
       this.uriString = uri;
     }
     
     private final String uriString;
     
     public String toString() {
       return uriString;
     }
     
     public static AwUri fromString(String uriString) {
       for (AwUri uri : AwUri.values()) {
         if (uri.uriString.equals(uriString)) {
           return uri;
         }
       }
       return null;
     }
    }
    
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
    
    public static String getAppConfigReadUrl() {
      return getServerLocation() + AwUri.APP_CONFIG_READ;
    }
    
    public static String getUserInfoReadUrl() {
      return getServerLocation() + AwUri.USER_INFO_READ;
    }
    
    public static String getUserReadUrl() {
      return getServerLocation() + AwUri.USER_READ;
    }
    
    public static String getUserCreateUrl() {
      return getServerLocation() + AwUri.USER_CREATE;
    }
    
    public static String getUserDeleteUrl() {
      return getServerLocation() + AwUri.USER_DELETE;
    }
    
    public static String getUserSearchUrl() {
      return getServerLocation() + AwUri.USER_SEARCH;
    }
    
    public static String getUserUpdateUrl() {
      return getServerLocation() + AwUri.USER_UPDATE;
    }
    
    public static String getUserChangePasswordUrl() {
      return getServerLocation() + AwUri.USER_CHANGE_PASSWORD;
    }
    
    public static String getCampaignReadUrl() {
      return getServerLocation() + AwUri.CAMPAIGN_READ;
    }
    
    public static String getCampaignCreateUrl() {
      return getServerLocation() + AwUri.CAMPAIGN_CREATE;
    }

    public static String getCampaignUpdateUrl() {
      return getServerLocation() + AwUri.CAMPAIGN_UPDATE;
    }
    
    public static String getCampaignDeleteUrl() {
      return getServerLocation() + AwUri.CAMPAIGN_DELETE;
    }
    
    public static String getAuthorizationUrl() {
      return getServerLocation() + AwUri.AUTHORIZATION;
    }
    
    public static String getSurveyResponseReadUrl() {
      return getServerLocation() + AwUri.SURVEY_RESPONSE_READ;
    }
    
    public static String getSurveyResponseUpdateUrl() {
      return getServerLocation() + AwUri.SURVEY_RESPONSE_UPDATE;
    }
    
    public static String getSurveyResponseDeleteUrl() {
      return getServerLocation() + AwUri.SURVEY_RESPONSE_DELETE;
    }
    
    public static String getClassReadUrl() {
      return getServerLocation() + AwUri.CLASS_READ;
    }
    
    public static String getClassCreateUrl() {
      return getServerLocation() + AwUri.CLASS_CREATE;
    }
    
    public static String getClassUpdateUrl() {
      return getServerLocation() + AwUri.CLASS_UPDATE;
    }
    
    public static String getClassDeleteUrl() {
      return getServerLocation() + AwUri.CLASS_DELETE;
    }
    
    public static String getClassSearchUrl() {
      return getServerLocation() + AwUri.CLASS_SEARCH;
    }
    
    public static String getClassRosterReadUrl() {
      return getServerLocation() + AwUri.CLASS_ROSTER_READ;
    }
    
    public static String getClassRosterUpdateUrl() {
      return getServerLocation() + AwUri.CLASS_ROSTER_UPDATE;
    } 
    
    public static String getImageReadUrl() {
      return getServerLocation() + AwUri.IMAGE_READ;
    }
    
    public static String getDocumentReadUrl() {
      return getServerLocation() + AwUri.DOCUMENT_READ;
    }
    
    public static String getDocumentReadContentsUrl() {
      return getServerLocation() + AwUri.DOCUMENT_READ_CONTENTS;
    }
    
    public static String getDocumentCreateUrl() {
      return getServerLocation() + AwUri.DOCUMENT_CREATE;
    }
    
    public static String getDocumentUpdateUrl() {
      return getServerLocation() + AwUri.DOCUMENT_UPDATE;
    }
    
    public static String getDocumentDeleteUrl() {
      return getServerLocation() + AwUri.DOCUMENT_DELETE;
    }

    public static String getDocumentDownloadUrl() {
      return getServerLocation() + AwUri.DOCUMENT_DOWNLOAD;
    }
    
    public static String getVisualizationUrl(String plotType) {
      return getServerLocation() + AwUri.VISUALIZATION_URL + "/" + plotType + "/read";
    }
    
    public static String getAuditReadUrl() {
      return getServerLocation() + AwUri.AUDIT_READ;
    }

    public static String getMobilityReadUrl() {
      return getServerLocation() + AwUri.MOBILITY_READ;
    }
    
    public static String getMobilityReadChunkedUrl() {
      return getServerLocation() + AwUri.MOBILITY_READ_CHUNKED;
    }
    
    public static String getMobilityDatesReadUrl() {
      return getServerLocation() + AwUri.MOBILITY_DATES_READ;
    }
    
    /**
     * Contains all the possible error codes returned by the AndWellness server.
     */
    public static enum ErrorCode {
        E0101("0101", "JSON syntax error"),
        E0102("0102", "no data in message"),
        E0103("0103", "server error"),
        E0104("0104", "session expired"),
        E0200("0200", "authentication failed"),
        E0201("0201", "disabled user"),
        E0202("0202", "new account attempting to access a service without changing default password first"),
        E0300("0300", "missing JSON data"),
        E0301("0301", "unknown request type"),
        E0302("0302", "unknown phone version"),
        E0304("0304", "invalid campaign id"),
        E0701("0701", "invalid user in query"),
        E0716("0716", "participant cannot query stopped campaign"),
        E0717("0717", "authors or analysts cannot query private campaigns");
        
        private final String errorCode;
        private final String errorDescription;
        
        ErrorCode(String code, String description) {
            errorCode = code;
            errorDescription = description;
        }
        
        public String getErrorCode() { return errorCode; }
        public String getErrorDesc() { return errorDescription; }
        
        /**
         * Returns the ErrorCode that has the passed in error code from the server.
         * 
         * @param err The error code from the server
         * @return The correct ErrorCode, NULL if not found.
         */
        public static ErrorCode translateServerError(String err) {
            // Loop over all ErrorCodes to find the right one.
            for (ErrorCode errCode : ErrorCode.values()) {
                if (err.equals(errCode.getErrorCode())) {
                    return errCode;
                }
            }
            
            return null;
        }
    }    
    
}
