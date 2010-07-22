package edu.ucla.cens.AndWellnessVisualizations.client;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.AndWellnessVisualizations.client.data.HoursSinceLastSurveyAwData;

/**
 * Implements service calls needed for the DataFilterPresenter.  Asks the server
 * for user information and data based on user input.
 * 
 * @author jhicks
 *
 */
public class DataFilterService {
    // The URL of the server API
    private final String userListApiUrl = "/app/q/hours-since-last-update";
    RequestBuilder userListBuilder;
    
    // Fields to hack in javascript values for fetchUserInfo call for now
    private String userNameJS;
    private String userPrivilegesJS;
    
    public DataFilterService() {
        // Initialize the request builder, used to generate requests for server data
        userListBuilder = new RequestBuilder(RequestBuilder.GET, URL.encode(userListApiUrl));
    }

    public void fetchUserInfo(final AsyncCallback<UserInfo> callback) {
        int userRole;
        
        try {
            // Doesn't actually connect to a server for now, instead grabs info from javascript and returns
            doGrabJavaScriptValues(); 
            
            // If this fails, throw a failure event
            if (userNameJS == "" || userPrivilegesJS == "") {
                throw new Exception();
            }
            
            // For now assume a "true" value means researcher (3) and a false means participant (2)
            if (userPrivilegesJS == "true") {
                userRole = 3;
            }
            else {
                userRole = 2;
            }
            
            UserInfo userInfo = new UserInfo(userNameJS, userRole);
            
            // Send this back to the callback
            callback.onSuccess(userInfo);        
        }
        catch (Exception e) {
            callback.onFailure(e);
        }
    }
    
    public void fetchUserList(final AsyncCallback<ArrayList<String>> callback) {
        // Build the server request, implement callbacks to handle returned json
        try {
            userListBuilder.sendRequest(null, new RequestCallback() {
                // Error occured, handle it here
                public void onError(Request request, Throwable exception) {
                    // Couldn't connect to server (could be timeout, SOP violation, etc.)   
                    callback.onFailure(exception);
                }

                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        // TODO Check for redirect response or other json error codes
                        
                        // Success!  Process the response.
                        String responseText = response.getText();
                        // Parse into JSON
                        JsArray<HoursSinceLastSurveyAwData> userDataJson = HoursSinceLastSurveyAwData.fromJSONString(responseText);
                        // The callback expects an ArrayList of user names, make it now
                        ArrayList<String> userList = new ArrayList<String>();
                        
                        // Loop over the array of data, add all users into the list to return
                        for (int i = 0; i < userDataJson.length(); ++i) {
                            userList.add(userDataJson.get(i).getUserName());
                        }
                        // Pass the new list back to the requesting function
                        callback.onSuccess(userList);
                        
                    // TODO handle other status codes
                   
                        
                    } else {
                        // Handle the error.  Can get the status text from response.getStatusText()
                        // TODO Make a new throwable exception based on the response/status text
                    }
                }       
            });
        // Big error occured, handle it here
        } catch (RequestException e) {
            // TODO: Handle couldn't connect to server        
        } 
    }

    public void fetchDateRange(Date startDate, int numDays,
        AsyncCallback<ArrayList<Object>> callback) {
        // TODO Auto-generated method stub

    }

    public void fetchDateRangeUser(Date startDate, int numDays,
        ArrayList<String> user, AsyncCallback<ArrayList<Object>> callback) {
        // TODO Auto-generated method stub

    }

    // Grab javascript values and put into this object
    private native void doGrabJavaScriptValues() /*-{
        this.@edu.ucla.cens.AndWellnessVisualizations.client.DataFilterService::userNameJS = $wnd.userName;
        this.@edu.ucla.cens.AndWellnessVisualizations.client.DataFilterService::userPrivilegesJS = $wnd.isResearcher;
    }-*/;
}
