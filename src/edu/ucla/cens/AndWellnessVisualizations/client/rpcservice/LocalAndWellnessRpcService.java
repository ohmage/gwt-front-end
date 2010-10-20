package edu.ucla.cens.AndWellnessVisualizations.client.rpcservice;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.AndWellnessVisualizations.client.model.AuthorizationTokenAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.AuthorizationTokenQueryAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.ConfigAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.ConfigQueryAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.DataPointAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.DataPointQueryAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.ErrorAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.ErrorQueryAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.UserInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.utils.JsArrayUtils;

/**
 * An implementation of the AndWellnessRpcService that reads data locally from text files
 * in the /testing/server_response directory.
 * 
 * @author jhicks
 *
 */
public class LocalAndWellnessRpcService implements AndWellnessRpcService {
    RequestBuilder authorizationDataLocalService;
    RequestBuilder dataPointLocalService;
    RequestBuilder configLocalService;
    
    // Locations of the text files to read
    private final String authorizationData = "/testing/server_response/auth_token.txt";
    private final String dataPoint = "/testing/server_response/data_point.txt";
    private final String configData = "/testing/server_response/config_nowhite.txt";
        
    /**
     * Initializes the various RequestBuilders to read the JSON files.
     */
    public LocalAndWellnessRpcService() {
        authorizationDataLocalService = new RequestBuilder(RequestBuilder.POST, URL.encode(authorizationData));
        dataPointLocalService = new RequestBuilder(RequestBuilder.POST, URL.encode(dataPoint));
        configLocalService = new RequestBuilder(RequestBuilder.POST, URL.encode(configData));
    }
    
    /**
     * Returns the login information from /testing/server_response/auth_token.txt.
     * The username must be abc and password 123 for a success, else an error will return.
     */
    public void fetchAuthorizationToken(final String userName, String password,
            final AsyncCallback<UserInfo> callback) {
        // Validate the username/password
        if (!(userName.equals("abc") && password.equals("123"))) {
            callback.onFailure(new NotLoggedInException("Invalid username and/or password."));
            return;
        }
        
        // Grab the data
        try {
            authorizationDataLocalService.sendRequest(null, new RequestCallback() {
                // Error occured, handle it here
                public void onError(Request request, Throwable exception) {
                    // Couldn't connect to server (could be timeout, SOP violation, etc.)   
                    callback.onFailure(new ServerException("Request to server timed out."));
                }
                
                // Eval the JSON into an overlay class and return
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        UserInfo translatedResponse;
                        // Eval the response into JSON
                        // (Hope this doesn't contain malicious JavaScript!)
                        String responseText = response.getText();
                        AuthorizationTokenQueryAwData serverResponse = AuthorizationTokenQueryAwData.fromJsonString(responseText);
                        AuthorizationTokenAwData serverResponseData;
                        
                        // Check for errors
                        if ("failure".equals(serverResponse.getResult())) {
                            callback.onFailure(new NotLoggedInException("Invalid username and/or password."));
                        }
                        
                        // Make sure this is a success
                        if (! "success".equals(serverResponse.getResult())) {
                            callback.onFailure(new ServerException("Server returned malformed JSON."));
                        }
                       
                        // Grab the data from the response
                        serverResponseData = serverResponse.getData();
                        
                        // Translate data to a UserInfo object for return to callback
                        translatedResponse = new UserInfo();
                        translatedResponse.setUserName(userName);
                        translatedResponse.setCampaignMembershipList(serverResponseData.getStringCampaignNameList());
                        translatedResponse.setAuthToken(serverResponseData.getAuthorizationToken());
                        
                        // Success, return the response!
                        callback.onSuccess(translatedResponse);
                        
                    } else {
                        // We are reading a local file, this shouldn't happen!
                        callback.onFailure(new ServerException("Cannot find file " + authorizationData));
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
            dataPointLocalService.sendRequest(null, new RequestCallback() {
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
                        // We are reading a local file, this shouldn't happen!
                        callback.onFailure(new ServerException("Cannot find file " + dataPoint));
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
    public void fetchConfigData(final AsyncCallback<ConfigAwData> callback) {
        // First grab our credentials to authorize with the server
        String authToken = getAuthTokenCookie();
        
        // Check to be sure the token has been set
        if (authToken == null) {
            throw new NotLoggedInException("Need to login.");
        }
        
     // Grab the data
        try {
            configLocalService.sendRequest(null, new RequestCallback() {
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
                        callback.onSuccess(serverResponse.getData());
                        
                    } else {
                        // We are reading a local file, this shouldn't happen!
                        callback.onFailure(new ServerException("Cannot find file " + dataPoint));
                    }
                }       
            });
        // Big error occured, handle it here
        } catch (RequestException e) {
            throw new ServerException("Cannot contact server.");     
        }
        
    }
    
    
    
    /**
     * Throws various RpcServiceExceptions based on the error codes.
     * 
     * @param errorResponse The JSON error response from the server.
     */
    private void parseServerErrorResponse(String errorResponse) {
        ErrorQueryAwData errorQuery = ErrorQueryAwData.fromJsonString(errorResponse);
        JsArray<ErrorAwData> errorList = errorQuery.getErrors();
        
        int numErrors = errorList.length();
    }
    
    /**
     * Sets the authorization token in a local cookie.
     * 
     * @param token The token to set.
     */
    void setAuthTokenCookie(String token) {
        Cookies.setCookie("token", token);
    }
    
    /**
     * Returns the set authorization token.  Returns NULL if no token set.
     * 
     * @return The authorization token.
     */
    private String getAuthTokenCookie() {
        return Cookies.getCookie("token");
    }
}
