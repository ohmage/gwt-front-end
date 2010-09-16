package edu.ucla.cens.AndWellnessVisualizations.client.rpcservice;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.AndWellnessVisualizations.client.model.AuthorizationInformationAwData;

/**
 * An implementation of the AndWellnessRpcService that reads data locally from text files
 * in the /testing/server_response directory.
 * 
 * @author jhicks
 *
 */
public class LocalAndWellnessRpcService implements AndWellnessRpcService {
    RequestBuilder authorizationDataLocalService;
    
    // Locations of the text files to read
    private final String authorizationData = "/testing/server_response/auth_token.txt";
    
    /**
     * Initializes the various RequestBuilders to read the JSON files.
     */
    public LocalAndWellnessRpcService() {
        authorizationDataLocalService = new RequestBuilder(RequestBuilder.GET, URL.encode(authorizationData));
    }
    
    /**
     * Returns the login information from /testing/server_response/auth_token.txt.
     * The username must be abc and password 123 for a success, else an error will return.
     */
    public void fetchAuthorizationToken(String userName, String password,
            final AsyncCallback<AuthorizationInformationAwData> callback) {
        // Validate the username/password
        if (!(userName.equals("abc") && password.equals("123"))) {
            callback.onFailure(new RpcServiceException("Invalid username and/or password."));
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
                        AuthorizationInformationAwData serverResponse = AuthorizationInformationAwData.fromJsonString(responseText);
                        
                        // Check for errors
                        if ("failure".equals(serverResponse.getResult())) {
                            callback.onFailure(new RpcServiceException("Invalid username and/or password."));
                        }
                        
                        // Make sure this is a success
                        if (! "success".equals(serverResponse.getResult())) {
                            callback.onFailure(new RpcServiceException("Server returned malformed JSON."));
                        }
                       
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

}
