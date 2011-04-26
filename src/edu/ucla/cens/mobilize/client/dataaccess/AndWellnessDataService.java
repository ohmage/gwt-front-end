package edu.ucla.cens.mobilize.client.dataaccess;

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

import edu.ucla.cens.mobilize.client.AndWellnessConstants;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.AuthorizationTokenQueryAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.DataPointAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.ErrorAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.ErrorQueryAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.QueryAwData;
import edu.ucla.cens.mobilize.client.dataaccess.request.CampaignReadParams;
import edu.ucla.cens.mobilize.client.dataaccess.request.DataPointFilterParams;
import edu.ucla.cens.mobilize.client.model.CampaignConciseInfo;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.ClassInfo;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.rpcservice.ApiException;
import edu.ucla.cens.mobilize.client.rpcservice.AuthenticationException;
import edu.ucla.cens.mobilize.client.rpcservice.NotLoggedInException;
import edu.ucla.cens.mobilize.client.rpcservice.ServerException;
import edu.ucla.cens.mobilize.client.rpcservice.ServerUnavailableException;
import edu.ucla.cens.mobilize.client.utils.AwDataTranslators;
import edu.ucla.cens.mobilize.client.utils.MapUtils;

/**
 * Class for requesting data from the AndWellnessServer.
 * 
 * @note was previously "ServerAndWellnessRpcService"
 * 
 * @author jhicks 
 * @author vhajdik
 */
public class AndWellnessDataService implements DataService {
  RequestBuilder authorizationService;
  RequestBuilder dataPointService;
  RequestBuilder configurationService;
  RequestBuilder userReadService;
  RequestBuilder campaignReadService;
  RequestBuilder campaignDeleteService;
  
  // NOTE: campaignCreate and campaignUpdate services are not included because 
  // they require file upload and so must be done with a formPanel

  String userName;
  String authToken;
  String client = "gwt";
  boolean isInitialized = false;
  
  private static Logger _logger = Logger.getLogger(AndWellnessDataService.class.getName());
  
  /**
   * Constructor initializes the various RequestBuilders to contact the AW server.
   * 
   * @note urls come from AndWellnessConstants.getXUrl() to enable using 
   * different urls for different deploy type (e.g., local for debug, 
   * remote for release)
   */
  public AndWellnessDataService() {
    /*
    authorizationService = new RequestBuilder(RequestBuilder.POST, URL.encode(AndWellnessConstants.getAuthorizationUrl()));
    authorizationService.setHeader("Content-Type", URL.encode("application/x-www-form-urlencoded"));
    dataPointService = new RequestBuilder(RequestBuilder.POST, URL.encode(AndWellnessConstants.getDataPointUrl()));
    dataPointService.setHeader("Content-Type", "application/x-www-form-urlencoded");
    configurationService = new RequestBuilder(RequestBuilder.POST, URL.encode(AndWellnessConstants.getConfigurationUrl()));
    configurationService.setHeader("Content-Type", "application/x-www-form-urlencoded");
    */
  }

  // lazy init
  private RequestBuilder getAuthorizationRequestBuilder() {
    if (this.authorizationService == null) {
      this.authorizationService = new RequestBuilder(RequestBuilder.POST, URL.encode(AndWellnessConstants.getAuthorizationUrl()));
      this.authorizationService.setHeader("Content-Type", "application/x-www-form-urlencoded");
    }
    return this.authorizationService;
  }
  
  // lazy init
  private RequestBuilder getUserRequestBuilder() {
    if (this.userReadService == null) {
      this.userReadService = new RequestBuilder(RequestBuilder.POST, URL.encode(AndWellnessConstants.getUserReadUrl()));
      this.userReadService.setHeader("Content-Type", "application/x-www-form-urlencoded");
    }
    return this.userReadService;
  }
  
  // lazy init
  private RequestBuilder getCampaignReadRequestBuilder() {
    if (this.campaignReadService == null) {
      this.campaignReadService = new RequestBuilder(RequestBuilder.POST, URL.encode(AndWellnessConstants.getCampaignReadUrl()));
      this.campaignReadService.setHeader("Content-Type", "application/x-www-form-urlencoded");
    }
    return this.campaignReadService;    
  }
  
  //lazy init
  private RequestBuilder getCampaignDeleteRequestBuilder() {
    if (this.campaignDeleteService == null) {
      this.campaignDeleteService = new RequestBuilder(RequestBuilder.POST, URL.encode(AndWellnessConstants.getCampaignDeleteUrl()));
      this.campaignDeleteService.setHeader("Content-Type", "application/x-www-form-urlencoded");
    }
    return this.campaignDeleteService;    
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
      E0304("0304", "invalid campaign id");
      
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
  
  /**
   * Returns various RpcServiceExceptions based on the error codes.
   *
   * http://www.lecs.cs.ucla.edu/wikis/andwellness/index.php/AndWellness-Error-Handling
   * 
   * @param errorResponse The JSON error response from the server.
   * @return exception
   */
  private static Exception parseServerErrorResponse(String errorResponse) {
      Exception returnError = null;
      ErrorQueryAwData errorQuery = ErrorQueryAwData.fromJsonString(errorResponse);
      JsArray<ErrorAwData> errorList = errorQuery.getErrors();
      
      _logger.fine("Received an error response from the server, parsing");
      
      int numErrors = errorList.length();
      
      // Lets just throw the first error for now
      if (numErrors > 0) {
          ErrorCode errorCode = ErrorCode.translateServerError(errorList.get(0).getCode());
          
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
              returnError = new AuthenticationException(errorCode.getErrorDesc());
              break;
          case E0300:
          case E0301:
          case E0302:
          case E0304:
              returnError = new ApiException(errorCode.getErrorDesc());
              break;
          default:
              returnError = new ServerException("Unknown server error.");
              break;
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
    _logger.fine("Authentication server response: " + response.getText());
    
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
      // Server down or incorrect request url
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
  public void init(String userName, String authToken) {
    this.userName = userName;
    this.authToken = authToken;
    this.isInitialized = true;
  }


  @Override
  public String authToken() {
    return this.authToken;
  }
  
  /**
   * Sends the passed in username and password to the AW server.  Checks the server response
   * to determine whether the login succeeded or failed, and notifies the callback of such.
   * Saves auth info for use in future data fetches.
   * @param userName The user name to authenticate.
   * @param password The password for the user name.
   * @param callback The interface to handle the server response.
   */
  public void fetchAuthorizationToken(final String userName, String password,
          final AsyncCallback<AuthorizationTokenQueryAwData> callback) {
   
      // Setup the post parameters
      Map<String,String> parameters = new HashMap<String,String>();

      // params 
      parameters.put("user", userName);
      parameters.put("password", password);
      parameters.put("client", "2");  // Hack in client ID for now
      
      String postParams = MapUtils.translateToParameters(parameters);
      
      // is it ok that this logs the password? (security?)
      _logger.finest("Attempting authentication with parameters: " + postParams);
      
      // Send the username/password to the server.
      RequestBuilder requestBuilder = getAuthorizationRequestBuilder();
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
                    responseText = getResponseTextOrThrowException(authorizationService, response);
                    result = AuthorizationTokenQueryAwData.fromJsonString(responseText);
                    init(userName, result.getAuthorizationToken()); // save for future fetches
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
  public void fetchUserInfo(final String username, final AsyncCallback<UserInfo> callback) {
    final RequestBuilder requestBuilder = getUserRequestBuilder();
    Map<String, String> params = new HashMap<String, String>();
    assert this.isInitialized : "You must call init(username, auth_token) before any fetches";
    params.put("auth_token", this.authToken);
    params.put("usernames", username); // FIXME: allow more than one?
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
            // FIXME: assumes first user is the one you want. should check id instead
            callback.onSuccess(userInfos.get(0));
          } else {
            callback.onFailure(new Exception("Failed to parse user data."));
          }
          
        }

        @Override
        public void onError(Request request, Throwable exception) {
          // TODO Auto-generated method stub
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
  public void fetchCampaignIds(Map<String, List<String>> params,
      AsyncCallback<List<String>> callback) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void fetchCampaignListShort(CampaignReadParams params,
                                final AsyncCallback<List<CampaignConciseInfo>> callback) {
    assert this.isInitialized : "You must call init(username, auth_token) before any fetches";
    params.authToken = this.authToken;
    params.client = this.client;
    String postParams = params.toString();
    _logger.fine("Attempting to fetch campaign list with parameters: " + postParams);
    final RequestBuilder requestBuilder = getCampaignReadRequestBuilder();
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            List<CampaignConciseInfo> campaigns = AwDataTranslators.translateCampaignReadQueryJSONtoCampaignConciseInfoList(responseText);
            callback.onSuccess(campaigns);
          } catch (Exception exception) {
            callback.onFailure(exception);
          }
          
        }

        @Override
        public void onError(Request request, Throwable exception) {
          // TODO Auto-generated method stub
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
    
    // FIXME: uncomment this when query by urn is working (just getting all campaigns for now)
    //List<String> campaignIdList = new ArrayList<String>();
    //campaignIdList.add(campaignId);
    //params.campaignUrns_opt = campaignIdList;
    String postParams = params.toString();
    _logger.fine("Attempting to fetch campaign detail list with parameters: " + postParams);
    final RequestBuilder requestBuilder = getCampaignReadRequestBuilder();
    try {
      requestBuilder.sendRequest(postParams, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {
          try {
            String responseText = getResponseTextOrThrowException(requestBuilder, response);
            List<CampaignDetailedInfo> campaigns = AwDataTranslators.translateCampaignReadQueryJSONtoCampaignDetailedInfoList(responseText);
            if (campaigns.size() > 0) {
              // FIXME: there should just be one campaign here but getting all campaigns
              // until api to get campaign by urn is finished
              for (CampaignDetailedInfo campaign : campaigns) {
                if (campaign.getCampaignId().equals(campaignId)) {
                  callback.onSuccess(campaign);
                  break;
                }
              }
              //callback.onSuccess(campaigns.get(0)); // there's just one in the list
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
    params.put("campaign_urn", campaignId);
    String postParams = MapUtils.translateToParameters(params);
    _logger.fine("Attempting to delete campaign with parameters: " + postParams);
    // make the request
    final RequestBuilder requestBuilder = getCampaignDeleteRequestBuilder();
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
  public void fetchDataPoints(String campaignId, DataPointFilterParams params,
      AsyncCallback<List<DataPointAwData>> callback) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void fetchSurveyResponses(String campaignId,
      DataPointFilterParams params, AsyncCallback<List<SurveyResponse>> callback) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void fetchClassList(String schoolId,
      AsyncCallback<List<ClassInfo>> callback) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void fetchClassDetail(String classId, AsyncCallback<ClassInfo> callback) {
    // TODO Auto-generated method stub
    
  }
  
  
}
