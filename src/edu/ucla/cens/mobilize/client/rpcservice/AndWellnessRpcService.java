package edu.ucla.cens.mobilize.client.rpcservice;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.model.AuthorizationTokenQueryAwData;
import edu.ucla.cens.mobilize.client.model.CampaignInfo;
import edu.ucla.cens.mobilize.client.model.ConfigQueryAwData;
import edu.ucla.cens.mobilize.client.model.DataPointAwData;
import edu.ucla.cens.mobilize.client.model.UserInfoOld;


/**
 * Defines the API to access each of the AndWellness query APIs.  These include /app/auth_token, 
 * /app/q/config, /app/q/upload_stat, /app/q/data_point, and /app/q/binary.  The service will
 * parse the returned data into Models defined in edu.ucla.cens.mobilize.client.model.
 * 
 * Throws a RequestException directly if the server cannot be contacted.
 * Calls AsyncCallback.onError() if the server returns an http status code error,
 * or an error in the JSON response.
 * 
 * @author jhicks
 *
 */
public interface AndWellnessRpcService {
    /**
     * Fetches an authorization token plus a list of campaign names from the server.
     * Throws a runtime error if the username/password combination fails authentication.
     * 
     * @param userName Username to authorize.
     * @param password Password to authorize.
     * @param callback Callback to call when the server responds.
     */
    public void fetchAuthorizationToken(String userName, String password, 
            final AsyncCallback<AuthorizationTokenQueryAwData> callback);
    
    public void fetchDataPoints(Date startDate, Date endDate, String userName, List<String> dataId, String campaignId, String clientName,
            String authToken, final AsyncCallback<List<DataPointAwData>> callback);
    
    public void fetchConfigData(String authToken, final AsyncCallback<ConfigQueryAwData> callback);
    
    public void fetchCampaignList(String authToken,
                                  HashMap<String, String> params,
                                  final AsyncCallback<List<CampaignInfo>> callback);
    
    public void fetchCampaignDetail(String authToken,
                                    String campaignId,
                                    final AsyncCallback<CampaignInfo> callback);
    
    public void fetchUserInfo(String authToken, final AsyncCallback<UserInfoOld> callback);
    
}
