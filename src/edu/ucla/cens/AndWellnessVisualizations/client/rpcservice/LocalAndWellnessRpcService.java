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

import edu.ucla.cens.AndWellnessVisualizations.client.model.AuthorizationTokenQueryAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.DataPointAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.DataPointQueryAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.ErrorAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.ErrorQueryAwData;
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
    
    // Locations of the text files to read
    private final String authorizationData = "/testing/server_response/auth_token.txt";
    private final String dataPoint = "/testing/server_response/data_point.txt";
        
    /**
     * Initializes the various RequestBuilders to read the JSON files.
     */
    public LocalAndWellnessRpcService() {
        authorizationDataLocalService = new RequestBuilder(RequestBuilder.POST, URL.encode(authorizationData));
        dataPointLocalService = new RequestBuilder(RequestBuilder.POST, URL.encode(dataPoint));
    }
    
    /**
     * Returns the login information from /testing/server_response/auth_token.txt.
     * The username must be abc and password 123 for a success, else an error will return.
     */
    public void fetchAuthorizationToken(String userName, String password,
            final AsyncCallback<AuthorizationTokenQueryAwData> callback) {
        // Validate the username/password
        if (!(userName.equals("abc") && password.equals("123"))) {
            callback.onFailure(new AuthorizationRpcServiceException("Invalid username and/or password."));
        }
        
        // Grab the data
        try {
            authorizationDataLocalService.sendRequest(null, new RequestCallback() {
                // Error occured, handle it here
                public void onError(Request request, Throwable exception) {
                    // Couldn't connect to server (could be timeout, SOP violation, etc.)   
                    callback.onFailure(new RpcServiceException("Request to server timed out."));
                }
                
                // Eval the JSON into an overlay class and return
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        // Eval the response into JSON
                        // (Hope this doesn't contain malicious JavaScript!)
                        String responseText = response.getText();
                        AuthorizationTokenQueryAwData serverResponse = AuthorizationTokenQueryAwData.fromJsonString(responseText);
                        
                        // Check for errors
                        if ("failure".equals(serverResponse.getResult())) {
                            callback.onFailure(new AuthorizationRpcServiceException("Invalid username and/or password."));
                        }
                        
                        // Make sure this is a success
                        if (! "success".equals(serverResponse.getResult())) {
                            callback.onFailure(new RpcServiceException("Server returned malformed JSON."));
                        }
                       
                        // Set the authorization token in a local cookie
                        setAuthTokenCookie(serverResponse.getAuthorizationToken());
                        
                        // Success, return the response!
                        callback.onSuccess(serverResponse);
                        
                    } else {
                        // We are reading a local file, this shouldn't happen!
                        callback.onFailure(new RpcServiceException("Cannot find file " + authorizationData));
                    }
                }       
            });
        // Big error occured, handle it here
        } catch (RequestException e) {
            throw new RpcServiceException("Cannot contact server.");     
        }
        
    }

    /**
     * Returns a list of data points representing the requested dataId between startDate
     * and endDate.
     */
    public void fetchDataPoints(Date startDate, Date endDate, String userName,
            List<String> dataIds, String campaignId, String clientName,
            final AsyncCallback<List<DataPointAwData>> callback) {
        // First grab our credentials to authorize with the server
        String authToken = getAuthTokenCookie();
        
        // Check to be sure the token has been set
        if (authToken == null) {
            throw new AuthorizationRpcServiceException("Need to login.");
        }
        
        // Grab the data
        try {
            dataPointLocalService.sendRequest(null, new RequestCallback() {
                // Error occured, handle it here
                public void onError(Request request, Throwable exception) {
                    // Couldn't connect to server (could be timeout, SOP violation, etc.)   
                    callback.onFailure(new RpcServiceException("Request to server timed out."));
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
                            callback.onFailure(new AuthorizationRpcServiceException("Invalid username and/or password."));
                        }
                        
                        // Make sure this is a success
                        if (! "success".equals(serverResponse.getResult())) {
                            callback.onFailure(new RpcServiceException("Server returned malformed JSON."));
                        }
                        
                        // Translate into a List of DataPointAwData
                        JsArray<DataPointAwData> dataPointAwDataArray = serverResponse.getData();
                        List<DataPointAwData> dataPointList = JsArrayUtils.translateToList(dataPointAwDataArray);
                        
                        // Success, return the response!
                        callback.onSuccess(dataPointList);
                        
                    } else {
                        // We are reading a local file, this shouldn't happen!
                        callback.onFailure(new RpcServiceException("Cannot find file " + dataPoint));
                    }
                }       
            });
        // Big error occured, handle it here
        } catch (RequestException e) {
            throw new RpcServiceException("Cannot contact server.");     
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
