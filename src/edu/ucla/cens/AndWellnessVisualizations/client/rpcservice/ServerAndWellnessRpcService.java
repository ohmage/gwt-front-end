package edu.ucla.cens.AndWellnessVisualizations.client.rpcservice;

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

import edu.ucla.cens.AndWellnessVisualizations.client.model.AuthorizationTokenQueryAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.ConfigQueryAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.DataPointAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.DataPointQueryAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.ErrorAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.ErrorQueryAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.UserInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.utils.JsArrayUtils;
import edu.ucla.cens.AndWellnessVisualizations.client.utils.MapUtils;

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
    
    // Locations of the text files to read
    private final String authorizationLocation = "http://127.0.0.1:8080/app/auth_token";
    private final String dataPointLocation = "http://127.0.0.1:8080/app/q/data";
    private final String configurationLocation = "http://127.0.0.1:8080/app/q/config";
        
    // Logging utility
    private static Logger _logger = Logger.getLogger(ServerAndWellnessRpcService.class.getName());
    
    /**
     * Initializes the various RequestBuilders to contact the AW server.
     */
    public ServerAndWellnessRpcService() {
        authorizationService = new RequestBuilder(RequestBuilder.POST, URL.encode(authorizationLocation));
        authorizationService.setHeader("Content-Type", URL.encode("application/x-www-form-urlencoded"));
        dataPointService = new RequestBuilder(RequestBuilder.POST, URL.encode(dataPointLocation));
        dataPointService.setHeader("Content-Type", "application/x-www-form-urlencoded");
        configurationService = new RequestBuilder(RequestBuilder.POST, URL.encode(configurationLocation));
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
                    
                    if (200 == response.getStatusCode()) {
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
                        _logger.warning("Server returned bad status.  Headers: " + response.getHeadersAsString());
                        // Server returned an error, assume this is a bad login for now
                        callback.onFailure(new NotLoggedInException("Invalid username or password."));
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
            List<String> dataIds, String campaignId, String clientName, String authToken,
            final AsyncCallback<List<DataPointAwData>> callback) {

        try {
            dataPointService.sendRequest(null, new RequestCallback() {
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
                        // Assume all server errors are invalid logins for now
                        callback.onFailure(new NotLoggedInException("Invalid username and/or password."));
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
    public void fetchConfigData(String campaignId, String authToken, final AsyncCallback<ConfigQueryAwData> callback) {
        // Setup the post parameters
        Map<String,String> parameters = new HashMap<String,String>();
        parameters.put("c", campaignId);
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
                    if (200 == response.getStatusCode()) {
                        // Eval the response into JSON
                        // (Hope this doesn't contain malicious JavaScript!)
                        String responseText = response.getText();
                        ConfigQueryAwData serverResponse = ConfigQueryAwData.fromJsonString(responseText);
                        
                        // Check for errors
                        if ("failure".equals(serverResponse.getResult())) {
                            callback.onFailure(new NotLoggedInException("Invalid username and/or password."));
                        }
                        
                        // Make sure this is a success
                        if (! "success".equals(serverResponse.getResult())) {
                            callback.onFailure(new ServerException("Server returned malformed JSON."));
                        }                    
                        
                        // Success, return the response!
                        callback.onSuccess(serverResponse);
                        
                    } else {
                        _logger.finer("Response status: " + response.getStatusCode());
                        _logger.finer("Response headers:" + response.getHeadersAsString());
                        _logger.finer("Response text: " + response.getText());
                        
                        // Assume all errors are invalid logins for now
                        callback.onFailure(new NotLoggedInException("Invalid username and/or password."));
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
        ErrorQueryAwData errorQuery = ErrorQueryAwData.fromJsonString(errorResponse);
        JsArray<ErrorAwData> errorList = errorQuery.getErrors();
        
        int numErrors = errorList.length();
        
        return null;
    }
}
