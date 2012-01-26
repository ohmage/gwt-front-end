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
    
    //private final static String debugServerLocation = "https://dev1.andwellness.org/app/";
    private final static String debugServerLocation = "http://localhost:8080/app/";
    //private final static String debugServerLocation = "https://dev.mobilizingcs.org/app/";
    private final static String releaseServerLocation = "../app/"; // same as web server
    
    // API Endpoints
    // http://lecs.cs.ucla.edu/wikis/andwellness/index.php/AndWellness_Read_API_2.2
    private final static String AUTHORIZATION   = "user/auth_token";
    private final static String APP_CONFIG_READ = "config/read";
    private final static String USER_INFO_READ  = "user_info/read";
    private final static String USER_READ       = "user/read";
    private final static String USER_CHANGE_PASSWORD = "user/change_password";
    private final static String USER_CREATE = "user/create";
    private final static String USER_UPDATE = "user/update";
    private final static String USER_DELETE = "user/delete";
    private final static String USER_SEARCH = "user/search";
    private final static String CAMPAIGN_READ   = "campaign/read";
    private final static String CAMPAIGN_CREATE = "campaign/create";
    private final static String CAMPAIGN_UPDATE = "campaign/update";
    private final static String CAMPAIGN_DELETE = "campaign/delete";
    private final static String SURVEY_RESPONSE_READ   = "survey_response/read";
    private final static String SURVEY_RESPONSE_UPDATE = "survey_response/update";
    private final static String SURVEY_RESPONSE_DELETE = "survey_response/delete";
    private final static String CLASS_READ      = "class/read";
    private final static String CLASS_CREATE    = "class/create";
    private final static String CLASS_UPDATE    = "class/update";
    private final static String CLASS_DELETE    = "class/delete";
    private final static String CLASS_SEARCH    = "class/search";
    private final static String CLASS_ROSTER_READ   = "class/roster/read";
    private final static String CLASS_ROSTER_UPDATE = "class/roster/update";
    private final static String IMAGE_READ      = "image/read";
    private final static String DOCUMENT_READ   = "document/read";
    private final static String DOCUMENT_READ_CONTENTS = "document/read/contents";
    private final static String DOCUMENT_CREATE = "document/create";
    private final static String DOCUMENT_UPDATE = "document/update";
    private final static String DOCUMENT_DELETE = "document/delete";
    private final static String DOCUMENT_DOWNLOAD = "document/read/contents";
    private final static String VISUALIZATION_URL = "viz";
    private final static String MOBILITY_READ	= "mobility/read";
    private final static String MOBILITY_READ_CHUNKED = "mobility/read/chunked";
    
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
      return getServerLocation() + APP_CONFIG_READ;
    }
    
    public static String getUserInfoReadUrl() {
      return getServerLocation() + USER_INFO_READ;
    }
    
    public static String getUserReadUrl() {
      return getServerLocation() + USER_READ;
    }
    
    public static String getUserCreateUrl() {
      return getServerLocation() + USER_CREATE;
    }
    
    public static String getUserDeleteUrl() {
      return getServerLocation() + USER_DELETE;
    }
    
    public static String getUserSearchUrl() {
      return getServerLocation() + USER_SEARCH;
    }
    
    public static String getUserUpdateUrl() {
      return getServerLocation() + USER_UPDATE;
    }
    
    public static String getUserChangePasswordUrl() {
      return getServerLocation() + USER_CHANGE_PASSWORD;
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
    
    public static String getCampaignDeleteUrl() {
      return getServerLocation() + CAMPAIGN_DELETE;
    }
    
    public static String getAuthorizationUrl() {
      return getServerLocation() + AUTHORIZATION;
    }
    
    public static String getSurveyResponseReadUrl() {
      return getServerLocation() + SURVEY_RESPONSE_READ;
    }
    
    public static String getSurveyResponseUpdateUrl() {
      return getServerLocation() + SURVEY_RESPONSE_UPDATE;
    }
    
    public static String getSurveyResponseDeleteUrl() {
      return getServerLocation() + SURVEY_RESPONSE_DELETE;
    }
    
    public static String getClassReadUrl() {
      return getServerLocation() + CLASS_READ;
    }
    
    public static String getClassCreateUrl() {
      return getServerLocation() + CLASS_CREATE;
    }
    
    public static String getClassUpdateUrl() {
      return getServerLocation() + CLASS_UPDATE;
    }
    
    public static String getClassDeleteUrl() {
      return getServerLocation() + CLASS_DELETE;
    }
    
    public static String getClassSearchUrl() {
      return getServerLocation() + CLASS_SEARCH;
    }
    
    public static String getClassRosterReadUrl() {
      return getServerLocation() + CLASS_ROSTER_READ;
    }
    
    public static String getClassRosterUpdateUrl() {
      return getServerLocation() + CLASS_ROSTER_UPDATE;
    } 
    
    public static String getImageReadUrl() {
      return getServerLocation() + IMAGE_READ;
    }
    
    public static String getDocumentReadUrl() {
      return getServerLocation() + DOCUMENT_READ;
    }
    
    public static String getDocumentReadContentsUrl() {
      return getServerLocation() + DOCUMENT_READ_CONTENTS;
    }
    
    public static String getDocumentCreateUrl() {
      return getServerLocation() + DOCUMENT_CREATE;
    }
    
    public static String getDocumentUpdateUrl() {
      return getServerLocation() + DOCUMENT_UPDATE;
    }
    
    public static String getDocumentDeleteUrl() {
      return getServerLocation() + DOCUMENT_DELETE;
    }

    public static String getDocumentDownloadUrl() {
      return getServerLocation() + DOCUMENT_DOWNLOAD;
    }
    
    public static String getVisualizationUrl(String plotType) {
      return getServerLocation() + VISUALIZATION_URL + "/" + plotType + "/read";
    }

    public static String getMobilityReadUrl() {
      return getServerLocation() + MOBILITY_READ;
    }
    
    public static String getMobilityReadChunkedUrl() {
      return getServerLocation() + MOBILITY_READ_CHUNKED;
    }
}
