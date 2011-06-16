package edu.ucla.cens.mobilize.client.dataaccess;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.AuthorizationTokenQueryAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.DataPointAwData;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.CampaignReadParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.ClassUpdateParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.DocumentReadParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.SurveyResponseReadParams;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;
import edu.ucla.cens.mobilize.client.model.ClassInfo;
import edu.ucla.cens.mobilize.client.model.DocumentInfo;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.model.UserInfo;


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
 * @author vhajdik
 *
 */
public interface DataService {
  /**
   * Initializes the data service for this user. All future calls will 
   * use auth_token for authentication.
   * @param userName
   * @param authToken (see fetchAuthorizationToken)
   */
  void init(String userName, String authToken);
  
  /**
   * Accessor for the auth token currently being used by the data service.
   * Useful so presenters can access the token for use in forms.
   * @return authToken
   */
  String authToken(); 
  
  /**
   * Accessor for the client string. Useful so presenters can access the client
   * string for use in forms. (Note that using the same client string in all
   * api calls makes it easier to filter server side logs.)
   * @return String representing the gwt web client
   */
  String client();
  
  /**
   * Fetches an authorization token plus a list of campaign names from the server.
   * Throws a runtime error if the username/password combination fails authentication.
   * 
   * @param userName Username to authorize.
   * @param password Password to authorize.
   * @param callback Callback to call when the server responds.
   */
  void fetchAuthorizationToken(String userName, String password, 
                               final AsyncCallback<AuthorizationTokenQueryAwData> callback);
  
  void changePassword(String userName, 
                      String oldPassword, 
                      String newPassword, 
                      final AsyncCallback<String> callback);
  
  // users
  void fetchUserInfo(String userName, final AsyncCallback<UserInfo> asyncCallback);
  
  // campaigns
  void fetchCampaignIds(CampaignReadParams params,
                        final AsyncCallback<List<String>> callback);
  
  void fetchCampaignIdToNameMap(CampaignReadParams params,
                                final AsyncCallback<Map<String, String>> callback);
  
  void fetchCampaignListShort(CampaignReadParams params, 
                              final AsyncCallback<List<CampaignShortInfo>> callback);
  
  void fetchCampaignListDetail(List<String> campaignIds, 
                             final AsyncCallback<List<CampaignDetailedInfo>> callback);
  
  void fetchCampaignDetail(String campaignId, 
      final AsyncCallback<CampaignDetailedInfo> callback);
  
  void deleteCampaign(final String campaignId,
                      final AsyncCallback<String> callback);
    
  void fetchDataPoints(String campaignId,
                       SurveyResponseReadParams params,
                       final AsyncCallback<List<DataPointAwData>> callback);

  void fetchSurveyResponses(String userName,
                            String campaignId,
                            String surveyName,
                            Privacy privacy,
                            Date startDate,
                            Date endDate,
                            final AsyncCallback<List<SurveyResponse>> callback);
  void updateSurveyResponse(String campaignId,
                            int surveyKey,
                            Privacy newPrivacyState,
                            final AsyncCallback<String> callback);
  void deleteSurveyResponse(String campaignId,
                             int surveyKey,
                             final AsyncCallback<String> callback);
  
  Map<String, String> getSurveyResponseExportParams(String campaignId);
  
  void fetchClassList(List<String> classIds,
                      final AsyncCallback<List<ClassInfo>> callback);
  
  void fetchClassDetail(String classId, final AsyncCallback<ClassInfo> callback);
  
  void updateClass(ClassUpdateParams params, final AsyncCallback<String> callback);
  
  void fetchDocumentList(DocumentReadParams params, 
      final AsyncCallback<List<DocumentInfo>> callback);
  
  // NOTE: createDocument and downloadDocument are done with FormPanels
  
  // download is done with a formpanel, dataservice just provides params
  Map<String, String> getDocumentDownloadParams(String documentId);
  
  void deleteDocument(String documentId, final AsyncCallback<String> callback);

  // download is done with a formpanel, dataservice just provides params
  Map<String, String> getCampaignXmlDownloadParams(String campaignId);
  
}
