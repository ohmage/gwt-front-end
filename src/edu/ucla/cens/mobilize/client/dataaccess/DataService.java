package edu.ucla.cens.mobilize.client.dataaccess;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.common.PlotType;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.AuthorizationTokenQueryAwData;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.CampaignReadParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.ClassUpdateParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.DocumentReadParams;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;
import edu.ucla.cens.mobilize.client.model.ClassInfo;
import edu.ucla.cens.mobilize.client.model.DocumentInfo;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.model.UserShortInfo;


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
  
  /**
   * @param userName
   * @param oldPassword
   * @param newPassword
   * @param callback
   */
  void changePassword(String userName, 
                      String oldPassword, 
                      String newPassword, 
                      final AsyncCallback<String> callback);

  /**
   * @param userName
   * @param asyncCallback returns UserInfo object on success
   */
  void fetchUserInfo(String userName, final AsyncCallback<UserInfo> asyncCallback);
  
  void fetchClassMembers(String classUrn, final AsyncCallback<List<UserShortInfo>> callback);
  
  // campaigns
  void fetchCampaignIds(CampaignReadParams params,
                        final AsyncCallback<List<String>> callback);
  
  void fetchCampaignIdToNameMap(CampaignReadParams params,
                                final AsyncCallback<Map<String, String>> callback);
  
  /**
   * Fetches short version of campaign info. If you need access to the xml config
   * or member lists, use fetchCampaignListDetail instead.
   * @param params
   * @param callback
   */
  void fetchCampaignListShort(CampaignReadParams params, 
                              final AsyncCallback<List<CampaignShortInfo>> callback);
  
  /**
   * Fetches long version of campaign info. If you do not need access to the
   * xml config or member lists, use fetchCampaignListShort instead, since it
   * transfers less data. 
   * @param campaignIds
   * @param callback return List\<CampaignDetailedInfo\> on success
   */
  void fetchCampaignListDetail(List<String> campaignIds, 
                             final AsyncCallback<List<CampaignDetailedInfo>> callback);
  
  /**
   * Convenience method. Same as fetchCampaignListDetail but returns just one object.
   * @param campaignId
   * @param callback returns CampaignDetailedInfo on success
   */
  void fetchCampaignDetail(String campaignId, 
      final AsyncCallback<CampaignDetailedInfo> callback);
  
  /**
   * @param campaignId
   * @param callback
   */
  void deleteCampaign(final String campaignId,
                      final AsyncCallback<String> callback);
    
  /**
   * 
   * @param userName
   * @param campaignId
   * @param surveyName
   * @param privacy
   * @param startDate
   * @param endDate
   * @param callback returns List\<SurveyResponse\> on success
   */
  void fetchSurveyResponses(String userName,
                            String campaignId,
                            String surveyName,
                            Privacy privacy,
                            Date startDate,
                            Date endDate,
                            final AsyncCallback<List<SurveyResponse>> callback);
  
  /**
   * Same as fetchSurveyResponses but just gives back the number of responses, not the data.
   * @param userName
   * @param campaignId
   * @param surveyName
   * @param privacy
   * @param startDate
   * @param endDate
   * @param callback returns Integer on success (# of survey responses matching filter criteria)
   */
  void fetchSurveyResponseCount(String userName,
                                String campaignId,
                                String surveyName,
                                Privacy privacy,
                                Date startDate,
                                Date endDate,
                                final AsyncCallback<Integer> callback);
  
  /**
   * Fetches list of unique usernames of participants who've submitted
   * at least one response to the campaign
   * @param campaignId
   * @param callback returns List\<String\> on success
   */
  void fetchParticipantsWithResponses(String campaignId,
                                      boolean onlySharedResponses,
                                      final AsyncCallback<List<String>> callback);
  
  /**
   * @param campaignId
   * @param surveyKey
   * @param newPrivacyState
   * @param callback
   */
  void updateSurveyResponse(String campaignId,
                            int surveyKey,
                            Privacy newPrivacyState,
                            final AsyncCallback<String> callback);
  
  /**
   * @param campaignId
   * @param surveyKey
   * @param callback
   */
  void deleteSurveyResponse(String campaignId,
                             int surveyKey,
                             final AsyncCallback<String> callback);
  
  /** 
   * Generates parameter names and values for making a request. Use this
   * function instead of getSurveyResponses when you want to fill the
   * names/values into an html form and do the request via a form post
   * instead of with ajax. (You'd need to do this, for instance, if you 
   * wanted the browser to prompt the user to save the response as a file.)
   * @param campaignId
   * @return Map\<String, String\> maps parameter names to parameter values
   */  
  Map<String, String> getSurveyResponseExportParams(String campaignId);
  
  /**
   * @param classIds
   * @param callback
   */
  void fetchClassList(List<String> classIds,
                      final AsyncCallback<List<ClassInfo>> callback);
  
  /**
   * Convenience method for getting info about just one class
   * @param classId
   * @param callback
   */ // FIXME: do we need this since we don't have short/long format for class info? 
  void fetchClassDetail(String classId, final AsyncCallback<ClassInfo> callback);
  
  /**
   * @param params
   * @param callback
   */
  void updateClass(ClassUpdateParams params, final AsyncCallback<String> callback);

  /**
   * @param params
   * @param callback returns List\<DocumentInfo\> on success
   */
  void fetchDocumentList(DocumentReadParams params, 
      final AsyncCallback<List<DocumentInfo>> callback);
  
  // NOTE: createDocument and downloadDocument are done with FormPanels

  /**
   * Generates parameter names and values for making a request. Use this
   * function instead of getSurveyResponses when you want to fill the
   * names/values into an html form and do the request via a form post
   * instead of with ajax. (You'd need to do this, for instance, if you 
   * wanted the browser to prompt the user to save the response as a file.)
   * @param String documentId
   */
  Map<String, String> getDocumentDownloadParams(String documentId);

  /**
   * @param documentId
   * @param callback
   */
  void deleteDocument(String documentId, final AsyncCallback<String> callback);

  /**
   * Generates parameter names and values for making a request. Use this
   * function instead of getSurveyResponses when you want to fill the
   * names/values into an html form and do the request via a form post
   * instead of with ajax. (You'd need to do this, for instance, if you 
   * wanted the browser to prompt the user to save the response as a file.)
   * @param campaignId
   * @return
   */
  Map<String, String> getCampaignXmlDownloadParams(String campaignId);
  
  /**
   * Generates url that, when fetched, generates a visualization on-the-fly.
   * @param plotType
   * @param width
   * @param height
   * @param campaignId
   * @param participantId
   * @param promptX
   * @param promptY
   * @return
   */
  String getVisualizationUrl(PlotType plotType, 
                             int width,  
                             int height,
                             String campaignId, 
                             String participantId, 
                             String promptX, 
                             String promptY);
  
  /**
   * If a url returned from getVisualizationUrl gives a broken image, call 
   * this method with the same params to find out what the error was. 
   * (Can be done in the image's onError handler)
   * @param plotType
   * @param width
   * @param height
   * @param campaignId
   * @param participantId
   * @param promptX
   * @param promptY
   */
  void fetchVisualizationError(PlotType plotType, 
                               int width,  
                               int height,
                               String campaignId, 
                               String participantId, 
                               String promptX, 
                               String promptY,
                               AsyncCallback<String> callback); 
  
}
