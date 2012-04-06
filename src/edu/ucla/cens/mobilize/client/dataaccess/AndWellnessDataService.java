package edu.ucla.cens.mobilize.client.dataaccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.AwConstants;
import edu.ucla.cens.mobilize.client.AwConstants.ErrorCode;
import edu.ucla.cens.mobilize.client.common.PlotType;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.AuthorizationTokenQueryAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.ErrorAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.ErrorQueryAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.QueryAwData;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.AuditLogSearchParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.CampaignReadParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.ClassSearchParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.ClassUpdateParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.DocumentReadParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.SurveyResponseReadParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.SurveyResponseUpdateParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.UserCreateParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.UserSearchParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.UserUpdateParams;
import edu.ucla.cens.mobilize.client.exceptions.ApiException;
import edu.ucla.cens.mobilize.client.exceptions.AuthenticationException;
import edu.ucla.cens.mobilize.client.exceptions.NotLoggedInException;
import edu.ucla.cens.mobilize.client.exceptions.ServerException;
import edu.ucla.cens.mobilize.client.exceptions.ServerUnavailableException;
import edu.ucla.cens.mobilize.client.model.AppConfig;
import edu.ucla.cens.mobilize.client.model.AuditLogEntry;
import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.ClassInfo;
import edu.ucla.cens.mobilize.client.model.ClassSearchInfo;
import edu.ucla.cens.mobilize.client.model.DocumentInfo;
import edu.ucla.cens.mobilize.client.model.MobilityChunkedInfo;
import edu.ucla.cens.mobilize.client.model.MobilityInfo;
import edu.ucla.cens.mobilize.client.model.RegistrationInfo;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.model.SurveyResponseData;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.model.UserSearchData;
import edu.ucla.cens.mobilize.client.model.UserSearchInfo;
import edu.ucla.cens.mobilize.client.model.UserShortInfo;
import edu.ucla.cens.mobilize.client.utils.AwDataTranslators;
import edu.ucla.cens.mobilize.client.utils.CollectionUtils;
import edu.ucla.cens.mobilize.client.utils.DateUtils;
import edu.ucla.cens.mobilize.client.utils.MapUtils;
import edu.ucla.cens.mobilize.client.utils.StopWatch;

/**
 * Class for requesting data from the AndWellnessServer.
 * 
 * @note was previously "ServerAndWellnessRpcService"
 * 
 * @author jhicks 
 * @author vhajdik
 * @author ewang9
 * 
 */
public class AndWellnessDataService implements DataService {

  // NOTE: campaignCreate and campaignUpdate are not included in the DataService
  // because they require file upload and so much be done with a formPanel

  String username;
  String authToken;
  String client = AwConstants.apiClientString;
  boolean isInitialized = false;
  
  private static Logger _logger = Logger.getLogger(AndWellnessDataService.class.getName());
  
  public AndWellnessDataService() {
  }

  // convenience method for request builder with common options
  private RequestBuilder getAwRequestBuilder(String serviceUrl) {
    RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, URL.encode(serviceUrl));
    rb.setHeader("Content-Type", "application/x-www-form-urlencoded");
    return rb;
  }
  
  /**
   * Returns various RpcServiceExceptions based on the error codes.
   *
   * http://www.lecs.cs.ucla.edu/wikis/andwellness/index.php/AndWellness-Error-Handling
   * 
   * @param errorResponse The JSON error response from the server.
   * @return exception
   */
  private static Exception parseServerErrorResponse(String errorResponse) {
      if (errorResponse == null) return new ServerException("Invalid error response.");

      Exception returnError = null;
      ErrorQueryAwData errorQuery = ErrorQueryAwData.fromJsonString(errorResponse);
      JsArray<ErrorAwData> errorList = errorQuery.getErrors();
      
      _logger.fine("Received an error response from the server, parsing");
      
      int numErrors = errorList.length();
      
      // Lets just throw the first error for now
      if (numErrors > 0) {
          String errorCodeString = errorList.get(0).getCode();
          ErrorCode errorCode = ErrorCode.translateServerError(errorCodeString);
          if (errorCode == null) { // translateServerError didn't recognize the code
            // output the code and text given by the server
            returnError = new ServerException(errorCodeString + ": " + errorList.get(0).getText());
          } else {
            switch (errorCode) {
            case E0103:
                returnError = new ServerException(errorCode.getErrorDesc());
                break;
            case E0104:
                returnError = new NotLoggedInException(errorCode.getErrorDesc());
                break;
            case E0200: 
            case E0201:
            case E0202:
                returnError = new AuthenticationException(errorCode.getErrorCode(),
                                                          errorCode.getErrorDesc());
                break;
            case E0300:
            case E0301:
            case E0302:
            case E0304:
            case E0701:
            case E0716:
            case E0717:
                returnError = new ApiException(errorCode.getErrorCode(),
                                               errorCode.getErrorDesc());
                break;
            default:
                // if this happens, it probably means you added an error to the 
                // ErrorCode enum but forgot to add it to this switch statement
                returnError = new ServerException("Unknown server error.");
                break;
            }
          }
      }
      
      return returnError;
  }
  
  
  /**
   * Convenience method for usual error handling. (If your request needs
   *   anything special, just roll your own.) To use this method, wrap it in a 
   *   try/catch and pass any exception to you callback.onFailure(throwable)
   * 
   * @param requestBuilder The RequestBuilder used to generate the response,
   *   needed for logging request url
   * @param response
   * @return Response text if request was successful. (This is the text you'll
   *   probably pass to your someclass.fromJsonString(responseText) method)
   * @throws Exception 
   */
  protected String getResponseTextOrThrowException(RequestBuilder requestBuilder, 
                                 Response response) throws Exception {
    _logger.fine("Server response: " + response.getText());
    
    String responseText = null;
    int statusCode = response.getStatusCode();
    if (200 == statusCode) {
        // Eval the response into JSON
        // (Hope this doesn't contain malicious JavaScript!)
        responseText = response.getText();
        
        // could throw JavaScriptException if server returns invalid JSON
        QueryAwData serverResponse = QueryAwData.fromJsonString(responseText); 
        String result = serverResponse.getResult();
        
        // Request completed successfully but server returned error message
        if ("failure".equals(result)) {
          throw parseServerErrorResponse(responseText);
        }

        // The only two recognized responses are "success" and "failure"
        if (! "success".equals(result)) {
          throw new ServerException("Invalid server result: " + result + 
                                    ". Should have been one of 'success' or 'failure'");
          
        }
    } else if (0 == statusCode) {
      // Server down, incorrect request url, servlet exception (check catalina log), etc
      throw new ServerUnavailableException(requestBuilder.getUrl());
    }else {
      // NOTE(4/14/2011): josh says the server returns 404 for all server errors
      throw new ServerException("Request failed with status code: " + 
                                 statusCode + 
                                 ". Url was: " + requestBuilder.getUrl());
    }
    
    return responseText;

  }

  /**
   * Store username and auth token to be used in data requests.
   * Must be called before any fetch* methods
   */
  @Override
  public void init(String username, String authToken) {
    this.username = username;
    this.authToken = authToken;
    this.isInitialized = true;
  }


  @Override
  public String authToken() {
    return this.authToken;
  }
  
  @Override
  public String client() {
    return this.client;
  }
  
  /**
   * Sends the passed in username and password to the AW server.  Checks the server response
   * to determine whether the login succeeded or failed, and notifies the callback of such.
   * Saves auth info for use in future data fetches.
   * @param username The user name to authenticate.
   * @param password The password for the user name.
   * @param callback The interface to handle the server response.
   */
  public void fetchAuthorizationToken(final String username, String password,
          final AsyncCallback<AuthorizationTokenQueryAwData> callback) {
   
      // Setup the post parameters
      Map<String,String> parameters = new HashMap<String,String>();

      // params 
      parameters.put("user", username);
      parameters.put("password", password);
      parameters.put("client", this.client);  
      
      String postParams = MapUtils.translateToParameters(parameters);
      
      // is it ok that this logs the password? (security?)
      _logger.finest("Attempting authentication with parameters: " + postParams);
      
      // Send the username/password to the server.
      final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getAuthorizationUrl());
      try {
          requestBuilder.sendRequest(postParams, new RequestCallback() {
              // Error occurred, handle it here
              public void onError(Request request, Throwable exception) {
                  // Couldn't connect to server (could be timeout, SOP violation, etc.)   
                  callback.onFailure(new ServerException("Request to server timed out."));
              }
              
              // Eval the JSON into an overlay class and return
              public void onResponseReceived(Request request, Response response) {
                  String responseText = null;     
                  AuthorizationTokenQueryAwData result = null;
                  try {
                    responseText = getResponseTextOrThrowException(requestBuilder, response);
                    result = AuthorizationTokenQueryAwData.fromJsonString(responseText);
                    init(username, result.getAuthorizationToken()); // save for future fetches
                    callback.onSuccess(result);
                  } catch (Exception exception) {
                    callback.onFailure(exception);
                  }
              }       
          });
      // Big error occured, handle it here
      } catch (RequestException e) {
        _logger.severe(e.getMessage());
        throw new ServerException("Cannot contact server.");     
      }
      
  }


  @Override
  public void fetchAppConfig(final AsyncCallback<AppConfig> callback) {
    // NOTE: this api call doesn't need auth token so it's ok to call before initializing data service
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getAppConfigReadUrl());
    _logger.fine("Fetching app config.");
    String postParams = ""; // no params for this call
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {
          try {
            String result = getResponseTextOrThrowException(requestBuilder, response);
            
            // Configure server app name and privacy options
            AppConfig appConfig = AwDataTranslators.translateAppConfigReadQueryToAppConfig(result);
            String appName = AppConfig.getAppName().toLowerCase();
            // FIXME: get these from db instead of hard-coding them
            if ("mobilize".equals(appName)) {
              List<Privacy> privacyStates = Arrays.asList(Privacy.PRIVATE, Privacy.SHARED);
              AppConfig.setAppDisplayName("Mobilize");
              AppConfig.setPrivacyStates(privacyStates);
              AppConfig.setSharedResponsesOnly(true);
              AppConfig.setResponsePrivacyIsEditable(true);
            } else if ("ohmage".equals(appName)) { 
              List<Privacy> privacyStates = Arrays.asList(Privacy.PRIVATE, Privacy.SHARED);    
              AppConfig.setAppDisplayName("ohmage");
              AppConfig.setPrivacyStates(privacyStates);
              AppConfig.setSharedResponsesOnly(false); // show everything
              AppConfig.setResponsePrivacyIsEditable(true);
            } else if ("chipts".equals(appName)) {
              List<Privacy> privacyStates = Arrays.asList(Privacy.PRIVATE, Privacy.SHARED, Privacy.INVISIBLE);
              AppConfig.setAppDisplayName("AndWellness");
              AppConfig.setPrivacyStates(privacyStates);
              AppConfig.setSharedResponsesOnly(false);
              AppConfig.setResponsePrivacyIsEditable(false);
            } else if ("andwellness".equals(appName)) { 
              List<Privacy> privacyStates = Arrays.asList(Privacy.PRIVATE, Privacy.SHARED);    
              AppConfig.setAppDisplayName("AndWellness");
              AppConfig.setPrivacyStates(privacyStates);
              AppConfig.setSharedResponsesOnly(false); // show everything
              AppConfig.setResponsePrivacyIsEditable(true);
            } else { // default settings
              List<Privacy> privacyStates = Arrays.asList(Privacy.PRIVATE, Privacy.SHARED);    
              AppConfig.setAppDisplayName("ohmage");
              AppConfig.setPrivacyStates(privacyStates);
              AppConfig.setSharedResponsesOnly(false); // show everything
              AppConfig.setResponsePrivacyIsEditable(true);
            }
            
            callback.onSuccess(appConfig); // FIXME: all fields in obj are static - returning it is pointless
          } catch (Exception exception) {
            _logger.severe(exception.getMessage());
            callback.onFailure(exception);
          }
        }

        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }
  }
  
  @Override
  public void changePassword(String username, 
                             String oldPassword, 
                             String newPassword,
                             final AsyncCallback<String> callback) {
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getUserChangePasswordUrl());
    Map<String, String> params = new HashMap<String, String>();
    params.put("client", this.client);
    params.put("user", username);
    params.put("password", oldPassword);
    params.put("new_password", newPassword);
    String postParams = MapUtils.translateToParameters(params);
    _logger.fine("Attempting to change password with parameters: " + postParams);
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {
          try {
            getResponseTextOrThrowException(requestBuilder, response);
            // no exception? then it was successful
            callback.onSuccess("");
          } catch (Exception exception) {
            _logger.severe(exception.getMessage());
            callback.onFailure(exception);
          }
        }

        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }
    
  }
  

  @Override
  public void adminChangePassword(String usernameLoggedInUser,
                                  String passwordLoggedInUser, 
                                  String usernameThatOwnsPassword,
                                  String newPassword,
                                  final AsyncCallback<String> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before making any api calls";
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getUserChangePasswordUrl());
    Map<String, String> params = new HashMap<String, String>();
    params.put("auth_token", this.authToken);
    params.put("client", this.client);
    params.put("user", usernameLoggedInUser);
    params.put("password", passwordLoggedInUser);
    params.put("username", usernameThatOwnsPassword);
    params.put("new_password", newPassword);
    String postParams = MapUtils.translateToParameters(params);
    _logger.fine("Attempting to change password with parameters: " + postParams);
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {
          try {
            getResponseTextOrThrowException(requestBuilder, response);
            // no exception? then it was successful
            callback.onSuccess("");
          } catch (Exception exception) {
            _logger.severe(exception.getMessage());
            callback.onFailure(exception);
          }
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }    
  }
  
  @Override
  public void fetchUserInfo(final String username, final AsyncCallback<UserInfo> callback) {
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getUserInfoReadUrl());
    Map<String, String> params = new HashMap<String, String>();
    assert this.isInitialized : "You must call init(username, auth_token) before any fetches";
    params.put("auth_token", this.authToken);
    params.put("client", this.client);
    params.put("user_list", username); // this says user_list but we only allow one
    String postParams = MapUtils.translateToParameters(params);
    _logger.fine("Attempting to fetch user info with parameters: " + postParams);
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {
          List<UserInfo> userInfos = null;
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            userInfos = AwDataTranslators.translateUserReadQueryJSONToUserInfoList(responseText);
          } catch (Exception exception) {
            _logger.severe(exception.getMessage());
            callback.onFailure(exception);
          }
          if (userInfos != null && userInfos.size() > 0) {
            callback.onSuccess(userInfos.get(0)); // there's only one
          } else {
            callback.onFailure(new Exception("Failed to parse user data."));
          }
          
        }

        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }
  }

  @Override
  public void fetchUserShortInfo(final String username, final AsyncCallback<UserShortInfo> callback) {
    UserSearchParams params = new UserSearchParams();
    params.username_opt = username;
    fetchUserSearchResults(params, new AsyncCallback<List<UserSearchInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        callback.onFailure(caught);
      }

      @Override
      public void onSuccess(List<UserSearchInfo> result) {
        boolean found = false;
        for (UserSearchInfo info : result) {
          if (username.equals(info.getUsername())) {
            UserShortInfo shortInfo = new UserShortInfo();
            shortInfo.setUsername(info.getUsername());
            shortInfo.setFirstName(info.getFirstName());
            shortInfo.setLastName(info.getLastName());
            shortInfo.setOrganization(info.getOrganization());
            shortInfo.setPersonalId(info.getPersonalId());
            shortInfo.setEmail(info.getEmail());
            callback.onSuccess(shortInfo);
            found = true;
            break;
          }
        }
        // call succeeded but user was not found. return null.
        if (!found) callback.onSuccess(null);
      }
    });
  }

  @Override
  public void fetchUserSearchResults(UserSearchParams params, final AsyncCallback<List<UserSearchInfo>> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getUserSearchUrl());
    params.authToken = this.authToken();
    params.client = this.client();
    String postParams = params.toString();
    _logger.fine("Attempting to query user search api with parameters: " + postParams);
    StopWatch.start("user_search:fetch");
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {
          StopWatch.stop("user_search:fetch");
          List<UserSearchInfo> userInfos = null;
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            StopWatch.start("user_search:translate");
            userInfos = AwDataTranslators.translateUserSearchQueryJSONToUserSearchInfoList(responseText);
            StopWatch.stop("user_search:translate");
            _logger.finest(StopWatch.getTotalsString());
            StopWatch.resetAll();
          } catch (Exception exception) {
            _logger.severe(exception.getMessage());
            callback.onFailure(exception);
          }
          if (userInfos != null) {
            callback.onSuccess(userInfos); 
          } else {
            callback.onFailure(new Exception("Failed to parse user data."));
          }
          
        }

        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }
  }
  

  @Override
  public void fetchUserSearchData(UserSearchParams params, final AsyncCallback<UserSearchData> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getUserSearchUrl());
    params.authToken = this.authToken();
    params.client = this.client();
    String postParams = params.toString();
    final int startIndex = params.startIndex_opt;
    _logger.fine("Attempting to query user search api with parameters: " + postParams);
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            List<UserSearchInfo> userInfos = AwDataTranslators.translateUserSearchQueryJSONToUserSearchInfoList(responseText);
            int totalUserCount = AwDataTranslators.translateUserSearchQueryJSONToUserCount(responseText);
            UserSearchData data = new UserSearchData();
            data.setUserSearchInfo(startIndex, userInfos);
            data.setTotalUserCount(totalUserCount);
            callback.onSuccess(data);
          } catch (Exception exception) {
            _logger.severe(exception.getMessage());
            callback.onFailure(exception);
          }
          
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }
  }
  
  @Override
  public void fetchUserSearchInfo(final String username, final AsyncCallback<UserSearchInfo> callback) {
    UserSearchParams params = new UserSearchParams();
    params.username_opt = username;
    fetchUserSearchResults(params, new AsyncCallback<List<UserSearchInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        callback.onFailure(caught);
      }

      @Override
      public void onSuccess(List<UserSearchInfo> result) {
        boolean isFound = false;
        // return object with username that matches exactly
        for (UserSearchInfo userInfo : result) {
          if (userInfo.getUsername().equals(username)) {
            callback.onSuccess(userInfo);
            isFound = true;
          }
        }
        // if no exact match was found, return null
        if (!isFound) callback.onSuccess(null);
      }
    });
  }

  @Override
  public void fetchClassMembers(Collection<String> classUrns, final AsyncCallback<List<UserShortInfo>> callback) {
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getUserReadUrl());
    Map<String, String> params = new HashMap<String, String>();
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    params.put("auth_token", this.authToken);
    params.put("client", this.client);
    params.put("class_urn_list", CollectionUtils.join(classUrns, ",")); 
    String postParams = MapUtils.translateToParameters(params);
    _logger.fine("Attempting to fetch user info with parameters: " + postParams);
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {
          List<UserShortInfo> userInfos = null;
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            userInfos = AwDataTranslators.translateUserReadQueryJSONToUserShortInfoList(responseText);
          } catch (Exception exception) {
            _logger.severe(exception.getMessage());
            callback.onFailure(exception);
          }
          if (userInfos != null) {
            callback.onSuccess(userInfos); 
          } else {
            callback.onFailure(new Exception("Failed to parse user data."));
          }
          
        }

        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }    
  }

  @Override
  public void fetchCampaignIds(CampaignReadParams params, 
                               final AsyncCallback<List<String>> callback) {
    
    fetchCampaignListShort(params, new AsyncCallback<List<CampaignShortInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        callback.onFailure(caught);
      }

      @Override
      public void onSuccess(List<CampaignShortInfo> result) {
        List<String> campaignIds = new ArrayList<String>();
        for (CampaignShortInfo info : result) {
          campaignIds.add(info.getCampaignId());
        }
        // invoke original callback
        callback.onSuccess(campaignIds);
      }
    });    
  }
  

  @Override
  public void fetchCampaignIdToNameMap(CampaignReadParams params,
      final AsyncCallback<Map<String, String>> callback) {
    fetchCampaignListShort(params, new AsyncCallback<List<CampaignShortInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        _logger.severe(caught.getMessage());
        callback.onFailure(caught);
      }

      @Override
      public void onSuccess(List<CampaignShortInfo> result) {
        Map<String, String> campaignIdToNameMap = new HashMap<String, String>();
        for (CampaignShortInfo info : result) {
          campaignIdToNameMap.put(info.getCampaignId(), info.getCampaignName());
        }
        // invoke original callback
        callback.onSuccess(campaignIdToNameMap);
      }
    });    
  }

  @Override
  public void fetchCampaignListShort(CampaignReadParams params,
                                final AsyncCallback<List<CampaignShortInfo>> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any fetches";
    params.authToken = this.authToken;
    params.client = this.client;
    params.outputFormat = CampaignReadParams.OutputFormat.SHORT;
    params.endDate_opt = DateUtils.addOneDay(params.endDate_opt); // make date range inclusive
    String postParams = params.toString();
    _logger.fine("Attempting to fetch campaign list with parameters: " + postParams);
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getCampaignReadUrl());
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            List<CampaignShortInfo> campaigns = AwDataTranslators.translateCampaignReadQueryJSONtoCampaignShortInfoList(responseText);
            callback.onSuccess(campaigns);
          } catch (Exception exception) {
            callback.onFailure(exception);
          }
          
        }

        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }
  }
  
  @Override
  public void fetchCampaignDetail(final String campaignId,
      final AsyncCallback<CampaignDetailedInfo> callback) {
    
    CampaignReadParams params = new CampaignReadParams();
    assert this.isInitialized : "You must call init(username, auth_token) before any fetches";
    params.authToken = this.authToken;
    params.client = this.client;
    params.outputFormat = CampaignReadParams.OutputFormat.LONG;
    params.campaignUrns_opt.add(campaignId);
    
    String postParams = params.toString();
    _logger.fine("Attempting to fetch campaign detail list with parameters: " + postParams);
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getCampaignReadUrl());
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            List<CampaignDetailedInfo> campaigns = AwDataTranslators.translateCampaignReadQueryJSONtoCampaignDetailedInfoList(responseText);
            if (campaigns.size() > 0) {
              callback.onSuccess(campaigns.get(0)); // there's just one in the list
            } else {
              callback.onFailure(new Exception("Campaign with id not found. Id: " + campaignId));
            }
          } catch (Exception exception) {
            callback.onFailure(exception);
          }
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }
    
  }

  @Override
  public void fetchCampaignListDetail(List<String> campaignIds,
      AsyncCallback<List<CampaignDetailedInfo>> callback) {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void deleteCampaign(final String campaignId, 
                             final AsyncCallback<String> callback) {
    // set up request params
    Map<String, String> params = new HashMap<String, String>();
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    params.put("auth_token", this.authToken);
    params.put("client", this.client);
    params.put("campaign_urn", campaignId);
    String postParams = MapUtils.translateToParameters(params);
    _logger.fine("Attempting to delete campaign with parameters: " + postParams);
    // make the request
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getCampaignDeleteUrl());
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {          
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            // no exception thrown? then it was a success
            callback.onSuccess(responseText);
          } catch (Exception exception) {
            callback.onFailure(exception);
          }
          
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }    
  }

  @Override
  public void fetchSurveyResponses(String usernameOrNull,
                                   final String campaignId,
                                   String surveyName, // ignored if null or ""
                                   Privacy privacy,	// ignored if null
                                   Date startDate,
                                   Date endDate,
                                   final AsyncCallback<List<SurveyResponse>> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    SurveyResponseReadParams params = new SurveyResponseReadParams();
    params.authToken = this.authToken;
    params.client = this.client;
    params.campaignUrn = campaignId;
    params.outputFormat = SurveyResponseReadParams.OutputFormat.JSON_ROWS;
    String username = usernameOrNull != null ? usernameOrNull : AwConstants.specialAllValuesToken;
    params.userList.add(username != null ? username : AwConstants.specialAllValuesToken);
    // if surveyName is omitted, readparams object sends special token for all surveys
    if (surveyName != null && !surveyName.isEmpty())	params.surveyIdList_opt.add(surveyName);
    if (privacy != null)	params.privacyState_opt = privacy;
    params.startDate_opt = startDate;
    params.endDate_opt = DateUtils.addOneDay(endDate); // add one to make range inclusive
    
    // define which columns to fetch (comment out this line to fetch all columns)
    params.columnList_opt = Arrays.asList("urn:ohmage:user:id",
                                          "urn:ohmage:context:epoch_millis", 
                                          "urn:ohmage:context:timezone",
                                          "urn:ohmage:context:location:latitude",
                                          "urn:ohmage:context:location:longitude",
                                          "urn:ohmage:context:location:status",
                                          "urn:ohmage:survey:id",
                                          "urn:ohmage:survey:title",
                                          "urn:ohmage:survey:description",
                                          "urn:ohmage:survey:privacy_state",
                                          "urn:ohmage:prompt:response");
    
    fetchSurveyResponses(params, callback);   
  }

  @Override
  public void fetchSurveyResponseCount(String username, 
                                       String campaignId,
                                       String surveyName, 
                                       Privacy privacy, 
                                       Date startDate, 
                                       Date endDate,
                                       final AsyncCallback<Integer> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    SurveyResponseReadParams params = new SurveyResponseReadParams();
    params.authToken = this.authToken;
    params.client = this.client;
    params.campaignUrn = campaignId;
    params.outputFormat = SurveyResponseReadParams.OutputFormat.JSON_ROWS;
    params.userList.add(username != null ? username : AwConstants.specialAllValuesToken);
    // if surveyName is omitted, readparams object sends special token for all surveys
    if (surveyName != null && !surveyName.isEmpty())  params.surveyIdList_opt.add(surveyName);
    params.privacyState_opt = privacy;
    params.startDate_opt = startDate;
    params.endDate_opt = DateUtils.addOneDay(endDate); // add one to make range inclusive
    // only fetch timestamp to reduce the amount of data (we only care about # of records)
    params.columnList_opt.add("urn:ohmage:context:timestamp"); 
    String postParams = params.toString();
    _logger.fine("Fetching survey response count with params: " + postParams);
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getSurveyResponseReadUrl());
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {          
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            // no exception thrown? then it was a success
            Integer numResponses = AwDataTranslators.translateSurveyResponseReadQueryJSONToTotalResponseCount(responseText);
            callback.onSuccess(numResponses);
          } catch (Exception exception) {
            _logger.severe(exception.getMessage());
            callback.onFailure(exception);
          }
          
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }
  }
  
  @Override
  public void fetchParticipantsWithResponses(String campaignId,
                                             boolean onlySharedResponses,
                                             final AsyncCallback<List<String>> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    SurveyResponseReadParams params = new SurveyResponseReadParams();
    params.authToken = this.authToken;
    params.client = this.client;
    params.campaignUrn = campaignId;
    params.outputFormat = SurveyResponseReadParams.OutputFormat.JSON_ROWS;
    params.userList.add(AwConstants.specialAllValuesToken);
    params.columnList_opt.add("urn:ohmage:user:id");
    params.collapse = true;
    params.returnId = false;
    if (onlySharedResponses) params.privacyState_opt = Privacy.SHARED;
    String postParams = params.toString();
    _logger.fine("Fetching participant list with params: " + postParams);
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getSurveyResponseReadUrl());
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {          
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            // no exception thrown? then it was a success
            List<String> result = AwDataTranslators.translateSurveyResponseParticipantQuery(responseText);
            callback.onSuccess(result);
          } catch (Exception exception) {
            _logger.severe(exception.getMessage());
            callback.onFailure(exception);
          }
          
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    } 
  }
  

  @Override
  public void fetchSurveyResponseData(SurveyResponseReadParams params,
                                      final AsyncCallback<SurveyResponseData> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    params.authToken = this.authToken;
    params.client = this.client;
    String postParams = params.toString();
    _logger.fine("Fetching survey response data with params: " + postParams);
    final String campaignId = params.campaignUrn;
    final int startIndex = params.numToSkip_opt;
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getSurveyResponseReadUrl());
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {          
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            // no exception thrown? then it was a success
            List<SurveyResponse> responses =
              AwDataTranslators.translateSurveyResponseReadQueryJSONToSurveyResponseList(responseText, campaignId);
            int totalResponseCount = AwDataTranslators.translateSurveyResponseReadQueryJSONToTotalResponseCount(responseText);
            SurveyResponseData data = new SurveyResponseData();
            data.setResponses(startIndex, responses);
            data.setTotalResponseCount(totalResponseCount);
            callback.onSuccess(data);
          } catch (Exception exception) {
            _logger.severe(exception.getMessage());
            callback.onFailure(exception);
          }
          
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }
  }
  
  @Override
  public void fetchSurveyResponses(SurveyResponseReadParams params,
                                   final AsyncCallback<List<SurveyResponse>> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    params.authToken = this.authToken;
    params.client = this.client;
    String postParams = params.toString();
    _logger.fine("Fetching survey responses with params: " + postParams);
    final String campaignId = params.campaignUrn;
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getSurveyResponseReadUrl());
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {          
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            // no exception thrown? then it was a success
            List<SurveyResponse> result =
              AwDataTranslators.translateSurveyResponseReadQueryJSONToSurveyResponseList(responseText, campaignId);
              callback.onSuccess(result);
          } catch (Exception exception) {
            _logger.severe(exception.getMessage());
            callback.onFailure(exception);
          }
          
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }
  }
  
  @Override
  public void updateSurveyResponse(String campaignId, String surveyKey,
      Privacy newPrivacyState, final AsyncCallback<String> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    SurveyResponseUpdateParams params = new SurveyResponseUpdateParams();
    params.authToken = this.authToken;
    params.client = this.client;
    params.campaignUrn = campaignId;
    params.privacy = newPrivacyState;
    params.surveyKey = surveyKey;
    String postParams = params.toString();
    _logger.fine("Updating survey response with params: " + postParams);
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getSurveyResponseUpdateUrl());
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {          
          try {
            getResponseTextOrThrowException(requestBuilder, response);
            // no exception thrown? then it was a success
            callback.onSuccess(""); 
          } catch (Exception exception) {
            _logger.severe(exception.getMessage());
            callback.onFailure(exception);
          }
          
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }
    
  }

  @Override
  public void deleteSurveyResponse(String campaignId, 
                                    String surveyKey, 
                                    final AsyncCallback<String> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    Map<String, String> params = new HashMap<String, String>();
    params.put("auth_token", this.authToken);
    params.put("client", this.client);
    params.put("campaign_urn", campaignId);
    params.put("survey_key", surveyKey);
    String postParams = MapUtils.translateToParameters(params);
    _logger.fine("Deleting survey response with params: " + postParams);
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getSurveyResponseDeleteUrl());
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {          
          try {
            getResponseTextOrThrowException(requestBuilder, response);
            // no exception thrown? then it was a success
            callback.onSuccess(""); // TODO: message?
          } catch (Exception exception) {
            _logger.severe(exception.getMessage());
            callback.onFailure(exception);
          }
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }    
  }
  
  @Override 
  public Map<String, String> getSurveyResponseExportParams(String campaignId) {
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    Map<String, String> params = new HashMap<String, String>();
    params.put("auth_token", this.authToken);
    params.put("client", this.client);
    params.put("campaign_urn", campaignId);
    params.put("user_list", AwConstants.specialAllValuesToken);
    params.put("prompt_id_list", AwConstants.specialAllValuesToken);
    params.put("output_format", "csv");
    params.put("column_list", "urn:ohmage:user:id,urn:ohmage:context:timestamp,urn:ohmage:prompt:response,urn:ohmage:context:location:latitude,urn:ohmage:context:location:longitude");
    params.put("sort_order", "survey,user,timestamp");
    if (AppConfig.exportAndVisualizeSharedResponsesOnly()) params.put("privacy_state", "shared");
    params.put("suppress_metadata", "true");
    return params;
  }
  
  @Override
  public void fetchClassList(List<String> classIds, final AsyncCallback<List<ClassInfo>> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    Map<String, String> params = new HashMap<String, String>();
    params.put("auth_token", this.authToken);
    params.put("client", this.client);
    params.put("class_urn_list", CollectionUtils.join(classIds, ","));
    String postParams = MapUtils.translateToParameters(params);
    _logger.fine("Fetching class list with params: " + postParams);
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getClassReadUrl());
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {          
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            // no exception thrown? then it was a success
            List<ClassInfo> result = AwDataTranslators.translateClassReadQueryJSONToClassInfoList(responseText);
            callback.onSuccess(result);
          } catch (Exception exception) {
            _logger.severe(exception.getMessage());
            callback.onFailure(exception);
          }
          
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }        
  }
  
  @Override
  public void fetchClassSearchResults(ClassSearchParams params, final AsyncCallback<List<ClassSearchInfo>> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    params.authToken = this.authToken();
    params.client = this.client();
    String postParams = params.toString();
    _logger.fine("Fetching class search results with params: " + postParams);
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getClassSearchUrl());
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {          
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            // no exception thrown? then it was a success
            List<ClassSearchInfo> result = AwDataTranslators.translateClassSearchQueryJSONToClassSearchInfoList(responseText);
            callback.onSuccess(result);
          } catch (Exception exception) {
            _logger.severe(exception.getMessage());
            callback.onFailure(exception);
          }
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }
  }
  
  @Override
  public void fetchClassSearchInfo(final String classUrn, final AsyncCallback<ClassSearchInfo> callback) {
    ClassSearchParams params = new ClassSearchParams();
    params.classUrn_opt = classUrn;
    fetchClassSearchResults(params, new AsyncCallback<List<ClassSearchInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        callback.onFailure(caught);
      }

      @Override
      public void onSuccess(List<ClassSearchInfo> result) {
        boolean isFound = false;
        for (ClassSearchInfo classInfo : result) {
          if (classInfo.getClassUrn().equals(classUrn)) {
            callback.onSuccess(classInfo);
            isFound = true;
          }
        }
        if (!isFound) callback.onSuccess(null);
      }
    });
  }
  
  @Override
  public void fetchClassNamesAndUrns(final AsyncCallback<Map<String, String>> callback) {
    // TODO: cache results
    fetchClassSearchResults(new ClassSearchParams(), new AsyncCallback<List<ClassSearchInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        callback.onFailure(caught);
      }

      @Override
      public void onSuccess(List<ClassSearchInfo> result) {
        Map<String, String> classUrnToNameMap = new HashMap<String, String>();
        for (ClassSearchInfo classInfo : result) {
          classUrnToNameMap.put(classInfo.getClassUrn(), classInfo.getClassName());
        }
        callback.onSuccess(classUrnToNameMap);
      }
    });
  }

  // FIXME: get cached data instead of refetching every time
  @Override
  public void fetchClassDetail(final String classId, final AsyncCallback<ClassInfo> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    List<String> classIdList = new ArrayList<String>();
    classIdList.add(classId);
    fetchClassList(classIdList, new AsyncCallback<List<ClassInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        callback.onFailure(caught);
      }

      @Override
      public void onSuccess(List<ClassInfo> result) {
        if (result != null && result.size() > 0 && result.get(0).getClassId().equals(classId)) {
          callback.onSuccess(result.get(0));
        } else {
          callback.onFailure(new Exception("There was a problem fetching the class info."));
        }
      }
    });
  }


  @Override
  public void createClass(final ClassUpdateParams params, final AsyncCallback<String> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    params.authToken = this.authToken;
    params.client = this.client;
    String postParams = params.toString();
    _logger.fine("Creating class with params: " + postParams);
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getClassCreateUrl());
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {          
          try {
            getResponseTextOrThrowException(requestBuilder, response);
            // no exception thrown? then it was a success
            callback.onSuccess("Class " + params.classId + " created successfully.");
          } catch (Exception exception) {
            _logger.severe(exception.getMessage());
            callback.onFailure(exception);
          }
          
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }
  }
  
  @Override
  public void updateClass(final ClassUpdateParams params, 
                          final AsyncCallback<String> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    params.authToken = this.authToken;
    params.client = this.client;
    String postParams = params.toString();
    _logger.fine("Updating class with params: " + postParams);
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getClassUpdateUrl());
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {          
          try {
            getResponseTextOrThrowException(requestBuilder, response);
            // no exception thrown? then it was a success
            callback.onSuccess("Class " + params.classId + " updated successfully.");
          } catch (Exception exception) {
            _logger.severe(exception.getMessage());
            callback.onFailure(exception);
          }
          
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }
  }
  
  @Override
  public void deleteClass(final String classUrn, final AsyncCallback<String> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    Map<String, String> params = new HashMap<String, String>();
    params.put("auth_token", this.authToken);
    params.put("client", this.client);
    params.put("class_urn", classUrn);
    String postParams = MapUtils.translateToParameters(params);
    _logger.fine("Deleting class with params: " + postParams);
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getClassDeleteUrl());
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {          
          try {
            getResponseTextOrThrowException(requestBuilder, response);
            // if no exception was thrown then it's successful
            callback.onSuccess(classUrn + " deleted");
          } catch (Exception exception) {
            _logger.severe(exception.getMessage());
            callback.onFailure(exception);
          }
          
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }
  }

  @Override
  public void fetchDocumentList(DocumentReadParams params,
      final AsyncCallback<List<DocumentInfo>> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    params.authToken = this.authToken;
    params.client = this.client;
    String postParams = params.toString();
    _logger.fine("Fetching document list with params: " + postParams);
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getDocumentReadUrl());
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {          
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            List<DocumentInfo> result = AwDataTranslators.translateDocumentReadQueryJSONToDocumentInfoList(responseText);
            callback.onSuccess(result);            
            // no exception thrown? then it was a success
            callback.onSuccess(result);
          } catch (Exception exception) {
            _logger.severe(exception.getMessage());
            callback.onFailure(exception);
          }
          
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }
  }
  
  @Override
  public void deleteDocument(String documentId, final AsyncCallback<String> callback) {
    // set up request params
    Map<String, String> params = new HashMap<String, String>();
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    params.put("auth_token", this.authToken);
    params.put("client", this.client);
    params.put("document_id", documentId);
    String postParams = MapUtils.translateToParameters(params);
    _logger.fine("Attempting to delete campaign with parameters: " + postParams);
    // make the request
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getDocumentDeleteUrl());
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {          
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            // no exception thrown? then it was a success
            callback.onSuccess(responseText);
          } catch (Exception exception) {
            callback.onFailure(exception);
          }
          
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }        
  }

  @Override
  public Map<String, String> getCampaignXmlDownloadParams(String campaignId) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("auth_token", this.authToken);
    params.put("client", this.client);
    params.put("output_format", "xml");
    params.put("campaign_urn_list", campaignId);
    return params;
  }
  
  @Override
  public Map<String, String> getClassRosterCsvDownloadParams(List<String> classUrns) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("auth_token", this.authToken);
    params.put("client", this.client);
    params.put("class_urn_list", CollectionUtils.join(classUrns, ","));
    return params;
  }

  @Override
  public Map<String, String> getDocumentDownloadParams(String documentId) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("auth_token", this.authToken);
    params.put("client", this.client);
    params.put("document_id", documentId);
    return params;
  }

  @Override
  public String getVisualizationUrl(PlotType plotType, 
                           int width,
                           int height,
                           String campaignId, 
                           String participantId, 
                           String promptX, 
                           String promptY, 
                           Date startDate, 
                           Date endDate, 
                           boolean includePrivateResponses) {
    assert plotType != null : "plotType is required";
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    // make sure any changes to params here are reflected in the fetchVizError function below
    Map<String, String> params = new HashMap<String, String>();
    params.put("auth_token", this.authToken());
    params.put("client", this.client());
    params.put("width", Integer.toString(width));
    params.put("height", Integer.toString(height));
    params.put("campaign_urn", campaignId);
    if (participantId != null && !participantId.isEmpty()) {
      params.put("user", participantId);
    }
    if (promptX != null && !promptX.isEmpty()) {
      params.put("prompt_id", promptX);
    }
    if (promptY != null && !promptY.isEmpty()) {
      params.put("prompt2_id", promptY);
    }
    if (startDate != null && endDate != null) {
      params.put("start_date", DateUtils.translateToApiRequestFormat(startDate));
      params.put("end_date", DateUtils.translateToApiRequestFormat(DateUtils.addOneDay(endDate)));
    }
    if (!includePrivateResponses) params.put("privacy_state", "shared");
    String baseUrl = AwConstants.getVisualizationUrl(plotType.toServerString()); 
    return baseUrl + "?" + MapUtils.translateToParameters(params);
  }

  // Call this function in image onError handler to find out the reason
  // for a broken image. Note that this means this function should almost
  // always end up calling callback.onFailure().
  // Make sure the params stay in sync with fetchVisualizationUrl above.
  @Override
  public void fetchVisualizationError(final PlotType plotType, 
                                      final int width, 
                                      final int height,
                                      final String campaignId, 
                                      final String participantId, 
                                      final String promptX, 
                                      final String promptY,
                                      final Date startDate,
                                      final Date endDate,
                                      final boolean includePrivateResponses,
                                      final AsyncCallback<String> callback) {
    assert plotType != null : "plotType is required";
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    Map<String, String> params = new HashMap<String, String>();
    params.put("auth_token", this.authToken());
    params.put("client", this.client());
    params.put("width", Integer.toString(width));
    params.put("height", Integer.toString(height));
    params.put("campaign_urn", campaignId);
    if (!includePrivateResponses) params.put("privacy_state", "shared");
    if (participantId != null && !participantId.isEmpty()) {
      params.put("user", participantId);
    }
    if (promptX != null && !promptX.isEmpty()) {
      params.put("prompt_id", promptX);
    }
    if (promptY != null && !promptY.isEmpty()) {
      params.put("prompt2_id", promptY);
    }
    if (startDate != null && endDate != null) {
      params.put("start_date", DateUtils.translateToApiRequestFormat(startDate));
      params.put("end_date", DateUtils.translateToApiRequestFormat(DateUtils.addOneDay(endDate)));
    }

    String postParams = MapUtils.translateToParameters(params);
    _logger.fine("Querying viz api to find cause of image load error: " + postParams);
    // make the request
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getVisualizationUrl(plotType.toServerString()));
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {          
          try {
            if (response.getStatusCode() == 200 && response.getHeader("Content-Type").contains("image")) {
              // Request works fine, which is unexpected. Maybe it was a transient error? 
              // Pass back image url so caller can make another attempt to display it.
              _logger.finest("fetchVisualization invoking onSuccess. Did you mean to call getVisualizationUrl instead?");
              callback.onSuccess(getVisualizationUrl(plotType, 
                                 width, 
                                 height,
                                 campaignId, 
                                 participantId, 
                                 promptX, 
                                 promptY,
                                 startDate,
                                 endDate,
                                 includePrivateResponses));
            } else { 
              getResponseTextOrThrowException(requestBuilder, response);
              throw new RuntimeException("Mysterious visualization error."); // should never happen
            }
          } catch (Exception exception) {
            callback.onFailure(exception);
          }
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }
    
  }

  @Override
  public void deleteUsers(Collection<String> usernames, final AsyncCallback<String> callback) {
    // set up request params
    Map<String, String> params = new HashMap<String, String>();
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    params.put("auth_token", this.authToken);
    params.put("client", this.client);
    params.put("user_list", CollectionUtils.join(usernames, ","));
    String postParams = MapUtils.translateToParameters(params);
    _logger.fine("Attempting to delete users with params: " + postParams);
    // make the request
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getUserDeleteUrl());
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {          
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            // no exception thrown? then it was a success
            callback.onSuccess(responseText);
          } catch (Exception exception) {
            callback.onFailure(exception);
          }
          
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }  
  }

  @Override
  public void removePersonalUserInfo(String username, final AsyncCallback<String> callback) {
	  UserUpdateParams params = new UserUpdateParams();
	  params.username = username;
	  params.deletePersonalInfo_opt = true;
	  updateUser(params, callback);
  }
  
  @Override
  public void disableUser(String username, final AsyncCallback<String> callback) {
    UserUpdateParams params = new UserUpdateParams();
    params.username = username;
    params.enabled_opt = false;
    updateUser(params, callback);    
  }

  @Override
  public void enableUser(String username, final AsyncCallback<String> callback) {
    UserUpdateParams params = new UserUpdateParams();
    params.username = username;
    params.enabled_opt = true;
    updateUser(params, callback);
  }
  

  @Override
  public void createUser(UserCreateParams params, final AsyncCallback<String> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    params.authToken = this.authToken();
    params.client = this.client();
    String postParams = params.toString();
    _logger.fine("Attempting to create user with params: " + postParams);
    // make the request
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getUserCreateUrl());
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {          
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            // no exception thrown? then it was a success
            callback.onSuccess(responseText);
          } catch (Exception exception) {
            callback.onFailure(exception);
          }
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }    
  }    

  @Override
  public void updateUser(UserUpdateParams params,
                         final AsyncCallback<String> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    params.authToken = this.authToken();
    params.client = this.client();
    String postParams = params.toString();
    _logger.fine("Attempting to update user with params: " + postParams);
    // make the request
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getUserUpdateUrl());
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {          
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            // no exception thrown? then it was a success
            callback.onSuccess(responseText);
          } catch (Exception exception) {
            callback.onFailure(exception);
          }
          
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }    
  }

  @Override
  public void fetchAuditLog(AuditLogSearchParams params, final AsyncCallback<List<AuditLogEntry>> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
    params.authToken = this.authToken;
    params.client = this.client;
    String postParams = params.toString();
    _logger.fine("Fetching audit log with params: " + postParams);
    final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getAuditReadUrl());
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {          
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            List<AuditLogEntry> result = AwDataTranslators.translateAuditReadQueryJSONToAuditLogEntryList(responseText);
            callback.onSuccess(result);            
            // no exception thrown? then it was a success
            callback.onSuccess(result);
          } catch (Exception exception) {
            _logger.severe(exception.getMessage());
            callback.onFailure(exception);
          }
          
        }
  
        @Override
        public void onError(Request request, Throwable exception) {
          _logger.severe(exception.getMessage());
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      _logger.severe(e.getMessage());
      throw new ServerException("Cannot contact server.");
    }    
  }

	@Override
	public void fetchMobilityData(Date date, String username, final AsyncCallback<List<MobilityInfo>> callback) {
		assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_token", this.authToken);
		params.put("client", this.client);
		params.put("date", DateUtils.translateToApiRequestFormat(date));
		if (username != null && !username.isEmpty()) {
			params.put("username", username);
		}
		
		String postParams = MapUtils.translateToParameters(params);
		_logger.fine("Fetching mobility data with params " + postParams);

		final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getMobilityReadUrl());
		try {
			requestBuilder.sendRequest(postParams, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {          
					try {
						String responseText = getResponseTextOrThrowException(requestBuilder, response);
						List<MobilityInfo> result = AwDataTranslators.translateMobilityReadQueryJSONToMobilityInfoList(responseText);	//FIXME
						callback.onSuccess(result);
					} catch (Exception exception) {
						_logger.severe(exception.getMessage());
						callback.onFailure(exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					_logger.severe(exception.getMessage());
					callback.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			_logger.severe(e.getMessage());
			throw new ServerException("Cannot contact server.");
		}    
	}

	@Override
	public void fetchMobilityDataChunked(Date start_date, Date end_date, final AsyncCallback<List<MobilityChunkedInfo>> callback) {
		assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_token", this.authToken);
		params.put("client", this.client);
		params.put("start_date", DateUtils.translateToApiRequestFormat(start_date));
		params.put("end_date", DateUtils.translateToApiRequestFormat(DateUtils.addOneDay(end_date)));
		
		String postParams = MapUtils.translateToParameters(params);
		_logger.fine("Fetching chunked mobility data with params " + postParams);

		final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getMobilityReadChunkedUrl());
		try {
			requestBuilder.sendRequest(postParams, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {          
					try {
						String responseText = getResponseTextOrThrowException(requestBuilder, response);
						List<MobilityChunkedInfo> result = AwDataTranslators.translateMobilityReadChunkedQueryJSONToMobilityChunkedInfoList(responseText);	//FIXME
						callback.onSuccess(result);
					} catch (Exception exception) {
						_logger.severe(exception.getMessage());
						callback.onFailure(exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					_logger.severe(exception.getMessage());
					callback.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			_logger.severe(e.getMessage());
			throw new ServerException("Cannot contact server.");
		}    
	}
	
	@Override
	public void fetchMobilityDates(Date start_date, Date end_date, String username, final AsyncCallback<List<Date>> callback) {
		assert this.isInitialized : "You must call init(username, auth_token) before any api calls";
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_token", this.authToken);
		params.put("client", this.client);
		params.put("start_date", DateUtils.translateToApiRequestFormat(start_date));
		params.put("end_date", DateUtils.translateToApiRequestFormat(DateUtils.addOneDay(end_date)));
		if (username != null && !username.isEmpty()) {
			params.put("username", username);
		}
		
		String postParams = MapUtils.translateToParameters(params);
		_logger.fine("Fetching mobility dates with params " + postParams);

		final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getMobilityDatesReadUrl());
		try {
			requestBuilder.sendRequest(postParams, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {          
					try {
						String responseText = getResponseTextOrThrowException(requestBuilder, response);
						List<Date> result = AwDataTranslators.translateMobilityDatesReadQueryJSONToDatesList(responseText);	//FIXME
						callback.onSuccess(result);
					} catch (Exception exception) {
						_logger.severe(exception.getMessage());
						callback.onFailure(exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					_logger.severe(exception.getMessage());
					callback.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			_logger.severe(e.getMessage());
			throw new ServerException("Cannot contact server.");
		}    
	}

	@Override
	public void fetchRegistrationInfo(final AsyncCallback<RegistrationInfo> callback) {
		// NOTE: this api call doesn't need auth token so it's ok to call before initializing data service
		final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getRegistrationReadUrl());
		_logger.fine("Fetching registration info.");
		String postParams = ""; // no params for this call
		try {
			requestBuilder.sendRequest(postParams, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						String result = getResponseTextOrThrowException(requestBuilder, response);
						RegistrationInfo regInfo = AwDataTranslators.translateRegistrationReadQueryToRegistrationInfo(result);
						callback.onSuccess(regInfo);
					} catch (Exception exception) {
						_logger.severe(exception.getMessage());
						callback.onFailure(exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					_logger.severe(exception.getMessage());
					callback.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			_logger.severe(e.getMessage());
			throw new ServerException("Cannot contact server.");
		}
	}

	@Override
	public void registerUser(String username, String password, String email,
			String recaptcha_challenge_field, String recaptcha_response_field,
			final AsyncCallback<String> callback) {
		final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getUserRegisterUrl());
		Map<String, String> params = new HashMap<String, String>();
		params.put("client", this.client);
		params.put("username", username);
		params.put("password", password);
		params.put("email_address", email);
		params.put("recaptcha_challenge_field", recaptcha_challenge_field);
		params.put("recaptcha_response_field", recaptcha_response_field);
		String postParams = MapUtils.translateToParameters(params);
		_logger.fine("Attempting to register new user with parameters: " + postParams);
		try {
			requestBuilder.sendRequest(postParams, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						getResponseTextOrThrowException(requestBuilder, response);
						// no exception? then it was successful
						callback.onSuccess("");
					} catch (Exception exception) {
						_logger.severe(exception.getMessage());
						callback.onFailure(exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					_logger.severe(exception.getMessage());
					callback.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			_logger.severe(e.getMessage());
			throw new ServerException("Cannot contact server.");
		}
	}
	
	@Override
	public void activateUser(final String registration_id, final AsyncCallback<String> callback) {
		final RequestBuilder requestBuilder = getAwRequestBuilder(AwConstants.getUserActivateUrl());
		Map<String, String> params = new HashMap<String, String>();
		params.put("client", this.client);
		params.put("registration_id", registration_id);
		String postParams = MapUtils.translateToParameters(params);
		_logger.fine("Attempting to activate user with parameters: " + postParams);
		try {
			requestBuilder.sendRequest(postParams, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						getResponseTextOrThrowException(requestBuilder, response);
						// no exception? then it was successful
						callback.onSuccess("");
					} catch (Exception exception) {
						_logger.severe(exception.getMessage());
						callback.onFailure(exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					_logger.severe(exception.getMessage());
					callback.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			_logger.severe(e.getMessage());
			throw new ServerException("Cannot contact server.");
		}
	}
}
