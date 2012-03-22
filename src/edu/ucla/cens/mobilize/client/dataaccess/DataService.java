package edu.ucla.cens.mobilize.client.dataaccess;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.common.PlotType;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.AuthorizationTokenQueryAwData;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.AuditLogSearchParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.CampaignReadParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.ClassSearchParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.ClassUpdateParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.DocumentReadParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.SurveyResponseReadParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.UserCreateParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.UserSearchParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.UserUpdateParams;
import edu.ucla.cens.mobilize.client.model.AppConfig;
import edu.ucla.cens.mobilize.client.model.AuditLogEntry;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;
import edu.ucla.cens.mobilize.client.model.ClassInfo;
import edu.ucla.cens.mobilize.client.model.ClassSearchInfo;
import edu.ucla.cens.mobilize.client.model.DocumentInfo;
import edu.ucla.cens.mobilize.client.model.MobilityChunkedInfo;
import edu.ucla.cens.mobilize.client.model.MobilityInfo;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.model.SurveyResponseData;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.model.UserSearchData;
import edu.ucla.cens.mobilize.client.model.UserSearchInfo;
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
   * Fetches properties of this application installation from the db so they
   * can be used to construct install-specific features of the app such as
   * logo, description, privacy states, etc 
   * @param callback
   */
  void fetchAppConfig(final AsyncCallback<AppConfig> callback);
  
  /**
   * @param userName
   * @param oldPassword
   * @param newPassword
   * @param callback
   */
  void changePassword(String username, 
                      String oldPassword, 
                      String newPassword, 
                      final AsyncCallback<String> callback);
  
  void adminChangePassword(String usernameLoggedInUser,
                           String passwordLoggedInUser,
                           String usernameThatOwnsPassword,
                           String newPassword,
                           final AsyncCallback<String> callback);
  
  /**
   * @param username
   * @param asyncCallback returns UserInfo object on success
   */
  void fetchUserInfo(String username, final AsyncCallback<UserInfo> asyncCallback);
  
  void fetchUserShortInfo(String username, final AsyncCallback<UserShortInfo> asyncCallback);
  
  /**
   * Lets admin query user search api.
   * @param UserSearchParams
   * @param callback
   */
  void fetchUserSearchResults(UserSearchParams params, final AsyncCallback<List<UserSearchInfo>> callback);
  
  void fetchUserSearchData(UserSearchParams params, final AsyncCallback<UserSearchData> callback);
  
  /**
   * Lets admin query search api for detailed info about a single user. Same as 
   * fetchUserSearchResults except that it only returns info about a single user
   * whose name matches the username string exactly. (fetchUserSearchResults would
   * return a list of all users whose usernames contain the search string.)
   * @note will return null to the onSuccess callback if the search api doesn't return 
   *   a record with username that matches exactly, even if there are partial matches
   * @param username
   * @param callback 
   */
  void fetchUserSearchInfo(String username, final AsyncCallback<UserSearchInfo> callback);
  
  void fetchClassMembers(Collection<String> classUrn, final AsyncCallback<List<UserShortInfo>> callback);
  
  void removePersonalUserInfo(String username, final AsyncCallback<String> callback);
  
  void deleteUsers(Collection<String> usernames, final AsyncCallback<String> callback);
  
  void disableUser(String username, final AsyncCallback<String> callback);
  
  void enableUser(String username, final AsyncCallback<String> callback);
  
  void updateUser(UserUpdateParams params, final AsyncCallback<String> callback);

  void createUser(UserCreateParams params, final AsyncCallback<String> callback);
  
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

  // TODO: deprecate this and use fetchSurveyResponses(ResponseReadParams...) everywhere instead?
  /**
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
   * Fetch survey response data to match the given parameter object
   * @param params
   * @param callback
   */
  void fetchSurveyResponses(SurveyResponseReadParams params,
                            final AsyncCallback<List<SurveyResponse>> callback);

  void fetchSurveyResponseData(SurveyResponseReadParams params, 
                               final AsyncCallback<SurveyResponseData> callback);
  
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
                            String surveyKey,
                            Privacy newPrivacyState,
                            final AsyncCallback<String> callback);
  
  /**
   * @param campaignId
   * @param surveyKey
   * @param callback
   */
  void deleteSurveyResponse(String campaignId,
                            String surveyKey,
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
  
  
  void fetchClassSearchResults(ClassSearchParams params, final AsyncCallback<List<ClassSearchInfo>> callback);
  
  /**
   * Same as fetchClassSearchResults except it only returns the single result with class urn
   * that exactly matches the classUrn argument. If no result matches exactly (even if there
   * are partial matches) it will return null to the onSuccess function.
   * @param params
   * @param callback 
   */
  void fetchClassSearchInfo(String classUrn, final AsyncCallback<ClassSearchInfo> callback);
  
  /**
   * @param callback Returns a map of class urns to names (for all classes)
   */
  void fetchClassNamesAndUrns(final AsyncCallback<Map<String, String>> callback);
  
  /**
   * Convenience method for getting info about just one class
   * @param classId
   * @param callback
   */ // FIXME: do we need this since we don't have short/long format for class info? 
  void fetchClassDetail(String classId, final AsyncCallback<ClassInfo> callback);
  
  /**
   * @param params ClassUpdateParams
   * @param callback AsyncCallback\<String\>
   */
  void createClass(ClassUpdateParams params, final AsyncCallback<String> callback);
  
  /**
   * @param params
   * @param callback
   */
  void updateClass(ClassUpdateParams params, final AsyncCallback<String> callback);

  /**
   * @param classUrn
   * @param callback
   */
  void deleteClass(String classUrn, final AsyncCallback<String> callback);
  
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
   * Generates parameter names and values for querying the class roster read API.
   * Given as a map of parameter names to values instead of a dataService call 
   * because you need to make the request with a FormPanel post to get the
   * browser to prompt the user to save the file. 
   * @param classUrns List of class urns
   * @return Map of param names to values that can be used in a FormPanel to fetch the roster
   */
  Map<String, String> getClassRosterCsvDownloadParams(List<String> classUrns);
  
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
                             String promptY, 
                             Date startDate, 
                             Date endDate, 
                             boolean includePrivateResponses);
  
  /**
   * If a url returned from getVisualizationUrl gives a broken image, call 
   * this method with the same params to find out what the error was. 
   * (Can be done in the image's onError handler)
   * GOTCHA: if you make any changes to getVisualizationUrl make sure to make them here too
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
                               Date startDate, 
                               Date endDate, 
                               boolean includePrivateResponses,
                               AsyncCallback<String> callback); 

  void fetchAuditLog(AuditLogSearchParams params, AsyncCallback<List<AuditLogEntry>> callback);
 
  /**
   * Retrieves mobility data via mobility/read
   * @param date
   * @param username (optional)
   * @param callback returns list of MobilityInfo on success (# of mobility data points)
   */
  void fetchMobilityData(Date date,
                         String username,
                         AsyncCallback<List<MobilityInfo>> callback);
  
  /**
   * Retrieves mobility data via mobility/read/chunked
   * @param start_date
   * @param end_date
   * @param callback returns list of MobilityChunkedInfo on success (# of mobility chunks)
   */
  void fetchMobilityDataChunked(Date start_date,
                                Date end_date,
                                AsyncCallback<List<MobilityChunkedInfo>> callback);
  
  /**
   * Retrieves mobility data via mobility/dates/read
   * @param start_date
   * @param end_date
   * @param username (optional)
   * @param callback returns list of Date on success (dates that contain mobility data)
   */
  void fetchMobilityDates(Date start_date,
                          Date end_date,
                          String username,
                          AsyncCallback<List<Date>> callback);
}
