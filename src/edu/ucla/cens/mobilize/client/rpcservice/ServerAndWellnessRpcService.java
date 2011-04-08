package edu.ucla.cens.mobilize.client.rpcservice;

import java.util.ArrayList;
import java.util.Arrays;
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

import edu.ucla.cens.mobilize.client.AndWellnessConstants;
import edu.ucla.cens.mobilize.client.common.CampaignId;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.RunningState;
import edu.ucla.cens.mobilize.client.common.UserRole;
import edu.ucla.cens.mobilize.client.model.AuthorizationTokenQueryAwData;
import edu.ucla.cens.mobilize.client.model.CampaignInfo;
import edu.ucla.cens.mobilize.client.model.ConfigQueryAwData;
import edu.ucla.cens.mobilize.client.model.DataPointAwData;
import edu.ucla.cens.mobilize.client.model.DataPointQueryAwData;
import edu.ucla.cens.mobilize.client.model.ErrorAwData;
import edu.ucla.cens.mobilize.client.model.ErrorQueryAwData;
import edu.ucla.cens.mobilize.client.model.UserInfoOld;
import edu.ucla.cens.mobilize.client.utils.DateUtils;
import edu.ucla.cens.mobilize.client.utils.JsArrayUtils;
import edu.ucla.cens.mobilize.client.utils.MapUtils;
import edu.ucla.cens.mobilize.client.utils.StringUtils;

/**
 * An implementation of the AndWellnessRpcService that contacts the AndWellness server
 * as implemented in the AndWellnessServer repository.
 * 
 * @author jhicks
 *
 */
public class ServerAndWellnessRpcService implements AndWellnessRpcService {
    RequestBuilder authorizationService;
    RequestBuilder dataPointService;
    RequestBuilder configurationService;
    
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
    
    // Logging utility
    private static Logger _logger = Logger.getLogger(ServerAndWellnessRpcService.class.getName());
    
    /**
     * Initializes the various RequestBuilders to contact the AW server.
     */
    public ServerAndWellnessRpcService() {
        authorizationService = new RequestBuilder(RequestBuilder.POST, URL.encode(AndWellnessConstants.getAuthorizationUrl()));
        authorizationService.setHeader("Content-Type", URL.encode("application/x-www-form-urlencoded"));
        dataPointService = new RequestBuilder(RequestBuilder.POST, URL.encode(AndWellnessConstants.getDataPointUrl()));
        dataPointService.setHeader("Content-Type", "application/x-www-form-urlencoded");
        configurationService = new RequestBuilder(RequestBuilder.POST, URL.encode(AndWellnessConstants.getConfigurationUrl()));
        configurationService.setHeader("Content-Type", "application/x-www-form-urlencoded");
    }
    
    /**
     * Sends the passed in username and password to the AW server.  Checks the server response
     * to determine whether the login succeeded or failed, and notifies the callback of such.
     * 
     * @param userName The user name to authenticate.
     * @param password The password for the user name.
     * @param callback The interface to handle the server response.
     */
    public void fetchAuthorizationToken(final String userName, String password,
            final AsyncCallback<AuthorizationTokenQueryAwData> callback) {
     
        // Setup the post parameters
        Map<String,String> parameters = new HashMap<String,String>();
        parameters.put("u", userName);
        parameters.put("p", password);
        parameters.put("ci", "2");  // Hack in client ID for now
        String postParams = MapUtils.translateToParameters(parameters);
        
        _logger.fine("Attempting authentication with parameters: " + postParams);
        
        // Send the username/password to the server.
        try {
            authorizationService.sendRequest(postParams, new RequestCallback() {
                // Error occured, handle it here
                public void onError(Request request, Throwable exception) {
                    // Couldn't connect to server (could be timeout, SOP violation, etc.)   
                    callback.onFailure(new ServerException("Request to server timed out."));
                }
                
                // Eval the JSON into an overlay class and return
                public void onResponseReceived(Request request, Response response) {
                    _logger.fine("Authentication server response: " + response.getText());
                    
                    int statusCode = response.getStatusCode();
                    if (200 == statusCode) {
                        // Eval the response into JSON
                        // (Hope this doesn't contain malicious JavaScript!)
                        String responseText = response.getText();
                        AuthorizationTokenQueryAwData serverResponse = AuthorizationTokenQueryAwData.fromJsonString(responseText);
                                                
                        // Check for errors
                        if ("failure".equals(serverResponse.getResult())) {
                            callback.onFailure(new NotLoggedInException("Invalid username or password."));
                            return;
                        }
                        
                        // Make sure this is a success
                        if (! "success".equals(serverResponse.getResult())) {
                            callback.onFailure(new ServerException("Server returned malformed JSON."));
                            return;
                        }
 
                        // Success, return the response!
                        callback.onSuccess(serverResponse);
                        
                    } else {
                        // TODO: handle status codes differently (e.g., 404 server not found)
                        String errorString = response.getText();
                        // Parse the server error and pass back to the callback as a failure
                        Throwable error = parseServerErrorResponse(response.getText());
                        callback.onFailure(error);
                    }
                }       
            });
        // Big error occured, handle it here
        } catch (RequestException e) {
            throw new ServerException("Cannot contact server.");     
        }
        
    }

    /**
     * Returns a list of data points representing the requested dataId between startDate
     * and endDate.
     */
    public void fetchDataPoints(Date startDate, Date endDate, String userName,
            List<String> dataIds, String campaignId, String campaignVersion, String authToken,
            final AsyncCallback<List<DataPointAwData>> callback) {
        StringBuffer postParams = new StringBuffer();
        
        // addParam can possibly throw an IllegalArgumentException if one of our passed in params
        // is null, just throw it up
        try {
            StringUtils.addParam(postParams, "u", userName);
            StringUtils.addParam(postParams, "t", authToken);
            StringUtils.addParam(postParams, "c", campaignId);
            StringUtils.addParam(postParams, "s", DateUtils.translateToServerUploadFormat(startDate));
            StringUtils.addParam(postParams, "e", DateUtils.translateToServerUploadFormat(endDate));
            StringUtils.addParam(postParams, "ci", "2");
            StringUtils.addParam(postParams, "cv", campaignVersion);
            
            // Add every data Id to the param list
            for (String dataId:dataIds) {
                StringUtils.addParam(postParams, "i", dataId);
            }
        }
        catch (IllegalArgumentException err) {
            _logger.severe("One or more passed parameters is bad.");
            _logger.finer("user: " + userName);
            _logger.finer("authToken: " + authToken);
            _logger.finer("campaignId: " + campaignId);
            _logger.finer("startDate: " + DateUtils.translateToServerUploadFormat(startDate));
            _logger.finer("endDate: " + DateUtils.translateToServerUploadFormat(endDate));
            _logger.finer("campaignVersion: " + campaignVersion);
            
            throw err;
        }
        
        _logger.finer("Contacting data query API with parameter string: " + postParams.toString());
        
        try {
            dataPointService.sendRequest(postParams.toString(), new RequestCallback() {
                // Error occured, handle it here
                public void onError(Request request, Throwable exception) {
                    // Couldn't connect to server (could be timeout, SOP violation, etc.)   
                    callback.onFailure(new ServerException("Request to server timed out."));
                }
                
                // Eval the JSON into an overlay class and return
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        // Eval the response into JSON
                        // (Hope this doesn't contain malicious JavaScript!)
                        String responseText = response.getText();
                        DataPointQueryAwData serverResponse = DataPointQueryAwData.fromJsonString(responseText);
                        
                        // Check for errors
                        if ("failure".equals(serverResponse.getResult())) {
                            callback.onFailure(new NotLoggedInException("Invalid username and/or password."));
                        }
                        
                        // Make sure this is a success
                        if (! "success".equals(serverResponse.getResult())) {
                            callback.onFailure(new ServerException("Server returned malformed JSON."));
                        }
                        
                        // Translate into a List of DataPointAwData
                        JsArray<DataPointAwData> dataPointAwDataArray = serverResponse.getData();
                        List<DataPointAwData> dataPointList = JsArrayUtils.translateToList(dataPointAwDataArray);
                        
                        // Success, return the response!
                        callback.onSuccess(dataPointList);
                        
                    } else {
                        // Parse the server error and pass back to the callback as a failure
                        Throwable error = parseServerErrorResponse(response.getText());
                        callback.onFailure(error);
                    }
                }       
            });
        // Big error occured, handle it here
        } catch (RequestException e) {
            throw new ServerException("Cannot contact server.");     
        }
        
    }
    
    /**
     * Fetches the configuration information from the local file system.
     * Parses into a javascript overlay object and passes back to the callback.
     * 
     * @param callback The callback to accept the config data.
     */
    public void fetchConfigData(String authToken, final AsyncCallback<ConfigQueryAwData> callback) {
        // Setup the post parameters
        Map<String,String> parameters = new HashMap<String,String>();
        parameters.put("ci", "2");  // Hack in client ID for now
        parameters.put("t", authToken);
        String postParams = MapUtils.translateToParameters(parameters);
        
        _logger.fine("Attempting to fetch config with parameters: " + postParams);
        
        // Grab the data
        try {
            configurationService.sendRequest(postParams, new RequestCallback() {
                // Error occured, handle it here
                public void onError(Request request, Throwable exception) {
                    // Couldn't connect to server (could be timeout, SOP violation, etc.)   
                    callback.onFailure(new ServerException("Request to server timed out."));
                }
                
                // Eval the JSON into an overlay class and return
                public void onResponseReceived(Request request, Response response) {
                    _logger.finer("Received response from the server for config API");
                    
                    if (200 == response.getStatusCode()) {
                        String responseText;
                        ConfigQueryAwData serverResponse = null;
                        
                        // Eval the response into JSON
                        // (Hope this doesn't contain malicious JavaScript!)
                        responseText = response.getText();
                        serverResponse = ConfigQueryAwData.fromJsonString(responseText);

                        // Check for errors
                        if ("failure".equals(serverResponse.getResult())) {
                            callback.onFailure(new NotLoggedInException("Invalid username and/or password."));
                            return;
                        }
                        
                        // Make sure this is a success
                        if (! "success".equals(serverResponse.getResult())) {
                            callback.onFailure(new ServerException("Server returned malformed JSON."));
                            return;
                        }                    
                        
                        // Success, return the response!
                        callback.onSuccess(serverResponse);
                        
                    } else {
                        // Parse the server error and pass back to the callback as a failure
                        Throwable error = parseServerErrorResponse(response.getText());
                        callback.onFailure(error);
                    }
                }       
            });
        // Big error occurred, handle it here
        } catch (RequestException e) {
            throw new ServerException("Cannot contact server.");     
        }
        
    }
    
    
    /**
     * Returns various RpcServiceExceptions based on the error codes.
     * 
     * @param errorResponse The JSON error response from the server.
     */
    private Throwable parseServerErrorResponse(String errorResponse) {
        Throwable returnError = null;
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

    private List<CampaignInfo> campaigns = new ArrayList<CampaignInfo>(); // deletme
    
    @Override
    public void fetchCampaignList(String authToken,
                                  HashMap<String, String> params,
                                  AsyncCallback<List<CampaignInfo>> callback) {
      // FIXME: use real data
      campaigns.clear();
      for (int i = 0; i < 3; i++) {
        CampaignInfo info = new CampaignInfo();
        info.setCampaignName("NIH_SleepSens" + Integer.toString(i));
        List<UserRole> roles = new ArrayList<UserRole>();
        roles.add(UserRole.PARTICIPANT);
        info.setUserRoles(roles);
        info.setDescription("Monitor sleeping patterns");
        info.setPrimaryAuthor("Bill");
        info.setPrivacy(Privacy.PUBLIC);
        info.setRunningState(RunningState.RUNNING);
        campaigns.add(info);
        
        info = new CampaignInfo();
        info.setCampaignName("NIH_DietSens" + Integer.toString(i));
        roles = new ArrayList<UserRole>();
        roles.add(UserRole.PARTICIPANT);
        roles.add(UserRole.AUTHOR);
        info.setUserRoles(roles);
        info.setDescription("What people eat");
        info.setPrimaryAuthor("Mary");
        info.setPrivacy(Privacy.PUBLIC);
        info.setRunningState(RunningState.RUNNING);
        campaigns.add(info);
        
        info = new CampaignInfo();
        info.setCampaignName("Advertising" + Integer.toString(i));
        roles = new ArrayList<UserRole>();
        roles.add(UserRole.PARTICIPANT);
        info.setUserRoles(roles);
        info.setDescription("Raise awareness of advertisements in the community");
        info.setPrimaryAuthor("SomeGuy");
        info.setPrivacy(Privacy.PUBLIC);
        info.setRunningState(RunningState.STOPPED);
        campaigns.add(info);
      }
      
      callback.onSuccess(campaigns);
    }

    @Override
    public void fetchCampaignDetail(String authToken,
        String campaignId,
        AsyncCallback<CampaignInfo> callback) {
      boolean wasFound = false;
      for (CampaignInfo info : campaigns) {
        if (info.getCampaignId().equals(campaignId)) {
          wasFound = true;
          callback.onSuccess(info);
        }
      }
      
      if (!wasFound) callback.onFailure(new Throwable("not found"));
    }

    @Override
    public void fetchUserInfo(String authToken, AsyncCallback<UserInfoOld> callback) {
      UserInfoOld info = new UserInfoOld();
      info.setUserName("myusername");
      callback.onSuccess(info);
      // FIXME
      
    }
    
}
