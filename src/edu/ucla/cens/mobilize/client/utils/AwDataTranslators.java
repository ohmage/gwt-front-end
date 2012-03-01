package edu.ucla.cens.mobilize.client.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.client.JsArrayString;

import edu.ucla.cens.mobilize.client.common.LocationStatus;
import edu.ucla.cens.mobilize.client.common.MobilityMode;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.PromptType;
import edu.ucla.cens.mobilize.client.common.RequestType;
import edu.ucla.cens.mobilize.client.common.ResponseStatus;
import edu.ucla.cens.mobilize.client.common.RoleClass;
import edu.ucla.cens.mobilize.client.common.RoleDocument;
import edu.ucla.cens.mobilize.client.common.RunningState;
import edu.ucla.cens.mobilize.client.common.RoleCampaign;
import edu.ucla.cens.mobilize.client.common.UserRoles;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.AppConfigAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.AuditLogAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.CampaignDetailAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.ClassAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.ClassSearchAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.DocumentAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.MobilityChunkedDataPointAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.MobilityDataPointAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.MobilityLocationAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.PromptResponseAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.SurveyResponseAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.UserAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.UserInfoAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.UserSearchInfoAwData;
import edu.ucla.cens.mobilize.client.model.AppConfig;
import edu.ucla.cens.mobilize.client.model.AuditLogEntry;
import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.ClassInfo;
import edu.ucla.cens.mobilize.client.model.ClassSearchInfo;
import edu.ucla.cens.mobilize.client.model.DocumentInfo;
import edu.ucla.cens.mobilize.client.model.MobilityChunkedInfo;
import edu.ucla.cens.mobilize.client.model.MobilityInfo;
import edu.ucla.cens.mobilize.client.model.PromptResponse;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.model.UserSearchInfo;
import edu.ucla.cens.mobilize.client.model.UserShortInfo;

// json
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * A collection of translators to translate data from the AndWellness server to local
 * data structures.
 * 
 * @author jhicks
 * @author vhajdik
 * @author ewang9
 *
 */
public class AwDataTranslators {
    // Logging utility
    private static Logger _logger = Logger.getLogger(AwDataTranslators.class.getName());
   
    /**
     * @param errorResponseJSON
     * @return Map of error codes to error descriptions
     */
    public static Map<String, String> translateErrorResponse(String errorResponseJSON) {
      Map<String, String> errorCodeToDescriptionMap = new HashMap<String, String>();
      try {
        JSONArray errorArray = JSONParser.parseStrict(errorResponseJSON).isObject().get("errors").isArray();
        for (int i = 0; i < errorArray.size(); i++) {
          JSONObject errorObj = errorArray.get(i).isObject();
          String errorCode = errorObj.get("code").isString().stringValue();
          String errorMessage = errorObj.get("text").isString().stringValue();
          errorCodeToDescriptionMap.put(errorCode, errorMessage);
        }
      } catch (Exception e) {
        _logger.severe("There was a problem parsing the error response: " + e.getMessage() + 
                       ".  Response JSON was: " + errorResponseJSON);
      }
      return errorCodeToDescriptionMap;
    }
    
    /**
     * Response from a formPanel post that has a content type other than text/html gets
     * wrapped in html pre tags. Ideally, have the responses returned as text/html but
     * if not, strip the pre tags. Note the tags are slightly different on diff browsers.
     * Assumptions: if there is a pre start tag then there is also a pre close tag, they
     * are the outermost tags, the content would never be wrapped in another tag that
     * has "pre" as a prefix in the tag name. 
     * Note: Firefox will try to make  you download the formpanel result if it's anything other than text/html
     * @return The innerHtml within the outermost pre tag, or the original string with 
     * leading and trailing whitespace removed if no pre tags were found
     */
    public static String stripPreTags(String jsonPossiblyWrappedInPreTags) {
      String str = jsonPossiblyWrappedInPreTags.trim();
      // different browsers render this differently. could be "<pre>", "<PRE>", "<pre style=...>"
      if (str.startsWith("<pre") || str.startsWith("<PRE")) { // assumes no other tags with "pre" as prefix
        int indexOfFirstInnerHtmlChar = str.indexOf('>') + 1;
        if (indexOfFirstInnerHtmlChar < str.length()) {
          str = str.substring(indexOfFirstInnerHtmlChar);
          int indexOfPreCloseTag = str.lastIndexOf('<');
          if (indexOfPreCloseTag > -1) {
            str = str.substring(0, indexOfPreCloseTag);
          }
        }
      }
      return str;
    }
    
    public static Integer translateSurveyResponseReadQueryJSONToTotalResponseCount(String surveyResponseReadQueryJSON) {
      int retval = -1;
      try {
        JSONValue value = JSONParser.parseStrict(surveyResponseReadQueryJSON);
        JSONObject responseHash = value.isObject();
        if (responseHash == null ) throw new RuntimeException("Invalid json response: " + surveyResponseReadQueryJSON);
        JSONNumber numberOfSurveys = responseHash.get("metadata").isObject().get("total_num_results").isNumber();
        retval = (int)numberOfSurveys.doubleValue();
      } catch (Exception e) {
        _logger.severe("Could not extract survey count. Json was: " + surveyResponseReadQueryJSON);
      }
      return retval;
    }
    
    // returns empty list if there were no responses
    public static List<SurveyResponse> translateSurveyResponseReadQueryJSONToSurveyResponseList(
        String promptResponseReadQueryJSON,
        String campaignId) {
      JSONValue value = JSONParser.parseStrict(promptResponseReadQueryJSON);
      JSONObject responseHash = value.isObject();
      if (responseHash == null) throw new RuntimeException("Invalid json response: " + promptResponseReadQueryJSON);
      
      // if there were no responses for this query (for this campaign) return empty list immediately
      JSONNumber numberOfPrompts = responseHash.get("metadata").isObject().get("number_of_prompts").isNumber();
      
      if (numberOfPrompts.doubleValue() < 1) return new ArrayList<SurveyResponse>();
      
      // data field contains a js array of survey response json objects
      if (!responseHash.containsKey("data")) throw new RuntimeException("data field missing");
      JSONValue dataValue = responseHash.get("data");
      JSONArray array = dataValue.isArray();
      if (array == null) throw new RuntimeException("Json in data field of response is not an array.");
      List<SurveyResponse> surveyResponses = new ArrayList<SurveyResponse>();
      for (int i = 0; i < array.size(); i++) {
        try {
          // parse as overlay object
          JSONObject obj = array.get(i).isObject();
          if (obj == null) throw new Exception("Invalid json: " + array.get(i).toString());
          SurveyResponseAwData surveyResponseAwData = (SurveyResponseAwData)obj.getJavaScriptObject();
          
          // copy data into SurveyResponse object
          SurveyResponse surveyResponse = new SurveyResponse();
          // NOTE(2011/06/19): if survey response key is missing from json, make sure the 
          //   survey_response/read query is passing a "return_id" param (see api wiki)
          surveyResponse.setResponseKey(surveyResponseAwData.getSurveyResponseKey());
          surveyResponse.setCampaignId(campaignId);
          // NOTE: campaignName not included in prompt data, must be filled in later
          surveyResponse.setPrivacyState(Privacy.fromServerString(surveyResponseAwData.getPrivacy()));
          surveyResponse.setResponseDate(new Date((long) surveyResponseAwData.getTime()));
          surveyResponse.setSurveyId(surveyResponseAwData.getSurveyId());
          surveyResponse.setSurveyName(surveyResponseAwData.getSurveyTitle());
          surveyResponse.setUserName(surveyResponseAwData.getUser());
          surveyResponse.setLocation(surveyResponseAwData.getLatitude(), surveyResponseAwData.getLongitude());
          surveyResponse.setLocationStatus(LocationStatus.fromServerString(surveyResponseAwData.getLocationStatus()));
          // survey response contains many prompt responses. parse those now.
          JsArrayString promptIds = surveyResponseAwData.getPromptIdsAsJsArray();
          String promptId = null;
          for (int j = 0; j < promptIds.length(); j++) {
            try {
              promptId = promptIds.get(j);
              PromptResponseAwData promptResponseAwData = surveyResponseAwData.getPromptResponseById(promptId);
              PromptResponse promptResponse = new PromptResponse();
              promptResponse.setPromptId(promptId);
              promptResponse.setIndex(promptResponseAwData.getIndex());
              promptResponse.setText(promptResponseAwData.getPromptText());
              promptResponse.setPromptType(PromptType.fromString(promptResponseAwData.getPromptType()));
              promptResponse.setResponse(promptResponseAwData.getPromptResponse());
              promptResponse.setResponsePreparedForDisplay(
                  getPromptResponseDisplayString(surveyResponse.getUserName(),
                                                 campaignId, 
                                                 promptResponse.getPromptType(), 
                                                 promptResponseAwData));
              // store prompt response in its parent survey response obj
              surveyResponse.addPromptResponse(promptResponse);
            } catch (Exception e) {
              _logger.severe("Unparseable prompt response. Error was: " + e.getMessage());
              _logger.finer("Prompt id was: " + promptId);
              // rethrow exception b/c we don't want to show user a partial survey response -
              // he might share something he didn't mean to
              throw e; 
            }
          }

          // save translated response
          surveyResponses.add(surveyResponse);
          
        } catch (Exception e) {
          _logger.severe("Skipping unparseable survey response. Error was: " + e.getMessage());
          _logger.finer("Survey response json was: " + array.get(i));
        }
      }
      return surveyResponses;
    }
    
    // display strings are generated at translation time to avoid having to pass around
    // a choice glossary with every response
    private static String getPromptResponseDisplayString(String imageOwnerLogin,
                                                         String campaignId,
                                                         PromptType promptType,
                                                         PromptResponseAwData promptResponseAwData) {
      String displayString = null;
      switch (promptType) {
      case MULTI_CHOICE:
        // FIXME: assumes choices are stored as comma separated list - is that correct?
        String[] choices = promptResponseAwData.getPromptResponse().split(",");
        StringBuilder sb = new StringBuilder();
        sb.append(promptResponseAwData.getChoiceLabelFromGlossary(choices[0].trim())); // no comma before the first
        for (int i = 1; i < choices.length; i++) {
          sb.append(",");
          sb.append(promptResponseAwData.getChoiceLabelFromGlossary(choices[i].trim()));
        }
        displayString = sb.toString();
        break;
      case SINGLE_CHOICE:
        String promptResponseStringValue = promptResponseAwData.getPromptResponse();
        displayString = promptResponseAwData.getChoiceLabelFromGlossary(promptResponseStringValue);
        break;
      case MULTI_CHOICE_CUSTOM:
      case SINGLE_CHOICE_CUSTOM:
        // user has typed in an "other" value - display it verbatim
        displayString = promptResponseAwData.getPromptResponse();
        break;
      default: // for all other types, just use the original string
        displayString = promptResponseAwData.getPromptResponse();
        break;
      }
      return displayString;  
    }
    
    // Expects json like:
    // {"result":"success","data":[{"user":"ohmage.ht"},{"user":"ohmage.johnj"},{"user":"ohmage.baa"},{"user":"ohmage.bab"},{"user":"ohmage.bac"},{"user":"ohmage.bag"},{"user":"ohmage.baj"},{"user":"ohmage.gail"}],"metadata":{"number_of_prompts":147,"items":["urn:ohmage:user:id"],"number_of_surveys":21}}
    public static List<String> translateSurveyResponseParticipantQuery(String queryJSON) {
      List<String> usernames = new ArrayList<String>();
      try {
        JSONValue value = JSONParser.parseStrict(queryJSON);
        JSONObject responseObj = value.isObject();

        // return empty list if there are zero participants
        JSONNumber numberOfSurveys = responseObj.get("metadata").isObject().get("number_of_surveys").isNumber();
        if (numberOfSurveys.doubleValue() < 1) return new ArrayList<String>(); 

        // check for data field
        if (!responseObj.containsKey("data")) throw new RuntimeException("missing data field");
        
        // get the list 
        JSONArray objArray = responseObj.get("data").isArray();
        for (int i = 0; i < objArray.size(); i++) {
          usernames.add(objArray.get(i).isObject().get("user").isString().stringValue());
        }
      } catch (Exception e) {
        _logger.severe("Failed to parse survey response participant query. JSON was: " + queryJSON);
        usernames = null; // null indicates error
      }
      return usernames;
    }
    
    // Expects json like:
    // {"result":"success","data":{"user.bh.pa":{"classes":{"urn:class:ca:lausd:BoyleHeights_HS:CS102:Spring:2011":"BH_HS_CS102_Spring_2011"},"permissions":{"can_create_campaigns":true},"campaign_roles":["participant","author","analyst"],"campaigns":{"urn:campaign:ca:lausd:BoyleHeights_HS:CS102:Spring:2011:Snack":"Snack"},"class_roles":["restricted"]}}}
    public static List<UserInfo> translateUserReadQueryJSONToUserInfoList(String userReadQueryResponseJSON) {
      List<UserInfo> users = new ArrayList<UserInfo>();
      
      // Parse response obj
      JSONValue value = JSONParser.parseStrict(userReadQueryResponseJSON);
      JSONObject responseObj = value.isObject();
      
      if (responseObj == null || !responseObj.containsKey("data")) {
        throw new RuntimeException("Invalid json format");
      }
      
      // data field maps usernames to serialized userinfo values
      JSONObject usernameToUserDataHash = responseObj.get("data").isObject();
      
      // For each user, translate the serialized info into a UserInfo object and save
      if (usernameToUserDataHash == null) return null;
      Set<String> usernames = usernameToUserDataHash.keySet();
      JSONValue jsonValue = null;
      for (String username : usernames) {
        // wrap in try/catch because it uses a UserInfoAwData
        try { 
          jsonValue = usernameToUserDataHash.get(username);
          JSONObject userJSONObject = jsonValue.isObject();
          if (userJSONObject == null) throw new Exception("user data field not a valid JSON object");
          UserInfoAwData userDataJSObject = (UserInfoAwData)userJSONObject.getJavaScriptObject();
          boolean isAdmin = userDataJSObject.getIsAdminFlag();
          boolean canCreateFlag = userDataJSObject.getCanCreateFlag();
          Map<String, String> campaignIdToNameMap = userDataJSObject.getCampaigns();
          Map<String, String> classIdToNameMap = userDataJSObject.getClasses();
          // get roles and translate to enum values          
          List<String> campaignRolesAsStrings = userDataJSObject.getCampaignRoles();
          List<RoleCampaign> campaignRoles = new ArrayList<RoleCampaign>();
          for (String roleString : campaignRolesAsStrings) {
            campaignRoles.add(RoleCampaign.valueOf(roleString.toUpperCase()));
          }
          // privileged flag will be set if user has PRIVILEGED role in any class
          List<String> classRolesAsStrings = userDataJSObject.getClassRoles();
          boolean isPrivileged = classRolesAsStrings.contains(RoleClass.PRIVILEGED.toServerString());
          
          UserInfo userInfo = new UserInfo();
          userInfo.setUserName(username);
          userInfo.setAdminFlag(isAdmin);
          userInfo.setPrivilegeFlag(isPrivileged);
          userInfo.setCanCreateFlag(canCreateFlag);
          userInfo.setCampaigns(campaignIdToNameMap);
          userInfo.setClasses(classIdToNameMap);
          userInfo.setCampaignRoles(campaignRoles);
          users.add(userInfo);
          
        } catch (Exception e) { 
          _logger.warning("Could not parse json for user: " + username + ". Skipping record.");
          _logger.fine(e.getMessage());
          _logger.finer("jsonValue: " + (jsonValue != null ? jsonValue.toString() : "null"));
        }
      }
      return users;
    }

    // {"result":"success","data":{"xerox.gimp":{"first_name":"firstName","organization":"organization","json_data":{},"last_name":"lastName","personal_id":"123456789","email_address":"test@example.com"}}}
    public static List<UserShortInfo> translateUserReadQueryJSONToUserShortInfoList(String userReadQueryResponseJSON) {
      List<UserShortInfo> users = new ArrayList<UserShortInfo>();
      
      // Parse response obj
      JSONValue value = JSONParser.parseStrict(userReadQueryResponseJSON);
      JSONObject responseObj = value.isObject();
      
      if (responseObj == null || !responseObj.containsKey("data")) {
        throw new RuntimeException("Invalid json format");
      }
      
      // data field maps usernames to serialized userShortInfos
      JSONObject usernameToUserDataHash = responseObj.get("data").isObject();
      
      // For each user, translate the serialized info into a UserInfo object and save
      if (usernameToUserDataHash == null) return null;
      Set<String> usernames = usernameToUserDataHash.keySet();
      JSONValue jsonValue = null;
      for (String username : usernames) {
        // wrap in try/catch because it uses a UserInfoAwData
        try { 
          jsonValue = usernameToUserDataHash.get(username);
          JSONObject userJSONObject = jsonValue.isObject();
          if (userJSONObject == null) throw new Exception("user data field not a valid JSON object");
          UserAwData userAwData = (UserAwData)userJSONObject.getJavaScriptObject();
          UserShortInfo userInfo = new UserShortInfo();
          userInfo.setUsername(username);
          userInfo.setFirstName(userAwData.getFirstName());
          userInfo.setLastName(userAwData.getLastName());
          userInfo.setPersonalId(userAwData.getPersonalId());
          userInfo.setOrganization(userAwData.getOrganization());
          userInfo.setEmail(userAwData.getEmail());
          users.add(userInfo);
          
        } catch (Exception e) { 
          _logger.warning("Could not parse json for user: " + username + ". Skipping record.");
          _logger.fine(e.getMessage());
          _logger.finer("jsonValue: " + (jsonValue != null ? jsonValue.toString() : "null"));
        }
      }
      return users;
    }
    
    // {"result":"success","data":{"ohmage.bbj":{"classes":{"urn:class:ca:ucla:Mobilize:May:2011":"restricted"},"permissions":{"enabled":true,"new_account":false,"admin":false,"can_create_campaigns":true},"campaigns":{"urn:campaign:ca:ucla:Mobilize:May:2011:Advertisement":["analyst","participant"],"urn:campaign:ca:ucla:Mobilize:May:2011:Sleep":["analyst","participant"]}}}}
    public static List<UserSearchInfo> translateUserSearchQueryJSONToUserSearchInfoList(String userSearchQueryResponseJSON) {
      List<UserSearchInfo> users = new ArrayList<UserSearchInfo>();
      
      // Parse response obj
      JSONValue value = JSONParser.parseStrict(userSearchQueryResponseJSON);
      JSONObject responseObj = value.isObject();
      
      if (responseObj == null || !responseObj.containsKey("data")) {
        throw new RuntimeException("Invalid json format");
      }
      
      // data field maps usernames to serialized userShortInfos
      JSONObject usernameToUserDataHash = responseObj.get("data").isObject();
      
      // For each user, translate the serialized info into a UserInfo object and save
      if (usernameToUserDataHash == null) return null;
      Set<String> usernames = usernameToUserDataHash.keySet();
      JSONValue jsonValue = null;
      for (String username : usernames) {
        // wrap in try/catch because it uses a UserSearchInfoAwData
        try { 
          jsonValue = usernameToUserDataHash.get(username);
          JSONObject userJSONObject = jsonValue.isObject();
          if (userJSONObject == null) throw new Exception("user data field not a valid JSON object");
          UserSearchInfoAwData userAwData = (UserSearchInfoAwData)userJSONObject.getJavaScriptObject();
          UserSearchInfo userInfo = new UserSearchInfo();
          userInfo.setUsername(username);
          userInfo.setFirstName(userAwData.getFirstName());
          userInfo.setLastName(userAwData.getLastName());
          userInfo.setPersonalId(userAwData.getPersonalId());
          userInfo.setOrganization(userAwData.getOrganization());
          userInfo.setEmail(userAwData.getEmailAddress());
          userInfo.setAdmin(userAwData.getAdminFlag());
          userInfo.setCanCreateCampaigns(userAwData.getCanCreateFlag());
          userInfo.setEnabled(userAwData.getEnabledFlag());
          userInfo.setNewAccount(userAwData.getNewAccountFlag());
          // TODO: get class and campaign roles instead of just urns?
          JsArrayString classUrns = userAwData.getClassUrns();
          for (int i = 0; i < classUrns.length(); i++) {
            userInfo.addClassUrn(classUrns.get(i));
          }
          JsArrayString campaignUrns = userAwData.getCampaignUrns();
          for (int i = 0; i < campaignUrns.length(); i++) {
            userInfo.addCampaignUrn(campaignUrns.get(i));
          }
          users.add(userInfo);
          
        } catch (Exception e) { 
          _logger.warning("Could not parse json for user: " + username + ". Skipping record.");
          _logger.fine(e.getMessage());
          _logger.finer("jsonValue: " + (jsonValue != null ? jsonValue.toString() : "null"));
        }
      }
      return users;
    }
    
    public static int translateUserSearchQueryJSONToUserCount(String userSearchQueryJSON) {
      int retval = -1;
      try {
        JSONValue value = JSONParser.parseStrict(userSearchQueryJSON);
        JSONObject responseHash = value.isObject();
        if (responseHash == null ) throw new RuntimeException("Invalid json response: " + userSearchQueryJSON);
        JSONNumber numberOfUsers = responseHash.get("metadata").isObject().get("total_num_results").isNumber();
        retval = (int)numberOfUsers.doubleValue();
      } catch (Exception e) {
        _logger.severe("Could not extract user count. Exception: " + e.getMessage());
      }
      return retval;
    }
    
    // {"result":"success","data":{"urn:andwellness:nih":{"user_roles":["supervisor"],"name":"NIH","privacy_state":"private","creation_timestamp":"2011-04-12 15:33:34.0","running_state":"active"}},"metadata":{"items":["urn:andwellness:nih"],"number_of_results":1}}
    public static List<CampaignShortInfo> translateCampaignReadQueryJSONtoCampaignShortInfoList(
        String responseText) {
      
      // List that will be returned
      List<CampaignShortInfo> campaigns = new ArrayList<CampaignShortInfo>(); 
      
      // Parse response obj
      @SuppressWarnings("deprecation")
      JSONValue value = JSONParser.parse(responseText);
      JSONObject responseObj = value.isObject();
      
      if (responseObj == null || !responseObj.containsKey("data")) {
        throw new RuntimeException("Invalid json format");
      }
      
      // data field is a map with campaign ids as keys and serialized campaign infos as values
      JSONObject dataHash = responseObj.get("data").isObject();
      
      // For each campaign, translate the serialized info into a 
      // CampaignShortInfo object and save
      if (dataHash == null) return null;
      Set<String> campaignIds = dataHash.keySet();
      JSONValue jsonValue = null;
      for (String campaignId : campaignIds) {
        try { 
          JSONObject campaignHash = dataHash.get(campaignId).isObject();
          // name
          String campaignName = campaignHash.get("name").isString().stringValue();
          // user roles
          JSONArray userRoleStrings = campaignHash.get("user_roles").isArray();
          UserRoles userRoles = new UserRoles();
          for (int i = 0; i < userRoleStrings.size(); i++) {
            String userRoleString = userRoleStrings.get(i).isString().stringValue().toUpperCase();
            userRoles.addRole(RoleCampaign.valueOf(userRoleString));
          }
          // privacy state
          String privacyStateString = campaignHash.get("privacy_state").isString().stringValue();
          Privacy privacyState = Privacy.valueOf(privacyStateString.toUpperCase());
          // running state
          String runningStateString = campaignHash.get("running_state").isString().stringValue();
          RunningState runningState = RunningState.valueOf(runningStateString.toUpperCase());
          // creation time
          String creationTimeString = campaignHash.get("creation_timestamp").isString().stringValue();
          Date creationTime = DateUtils.translateFromServerFormat(creationTimeString);
          // create the object
          CampaignShortInfo campaignInfo = new CampaignShortInfo(campaignId,
                                                                     campaignName,
                                                                     runningState,
                                                                     privacyState,
                                                                     userRoles,
                                                                     creationTime);
          // save
          campaigns.add(campaignInfo);    
          
        } catch (Exception e) { 
          _logger.warning("Could not parse json for campaign id: " + campaignId + ". Skipping record.");
          _logger.fine(e.getMessage());
          _logger.finer("jsonValue: " + (jsonValue != null ? jsonValue.toString() : "null"));
        }
      }
      
      return campaigns;
    }

    // {"result":"success","data":{"urn:andwellness:nih":{"classes":["urn:sys:andwellness"],"user_role_campaign":{"analyst":[],"author":[],"supervisor":["temp.user"],"participant":[]},"user_roles":["supervisor"],"name":"NIH","privacy_state":"private","xml":"","creation_timestamp":"2011-04-12 15:33:34.0","running_state":"active"}},"metadata":{"items":["urn:andwellness:nih"],"number_of_results":1}}
    public static List<CampaignDetailedInfo> translateCampaignReadQueryJSONtoCampaignDetailedInfoList(
        String responseText) {
      // List that will be returned
      List<CampaignDetailedInfo> campaigns = new ArrayList<CampaignDetailedInfo>(); 
      
      // Parse response obj
      @SuppressWarnings("deprecation")
      JSONValue value = JSONParser.parse(responseText);
      JSONObject responseObj = value.isObject();
      
      if (responseObj == null || !responseObj.containsKey("data")) {
        throw new RuntimeException("Invalid json format");
      }
      
      // data field is a map with campaign ids as keys and serialized campaign info as values
      JSONObject dataHash = responseObj.get("data").isObject();
      
      // For each campaign, translate json into a CampaignDetailedInfo 
      if (dataHash == null) return null;
      Set<String> campaignIds = dataHash.keySet();
      JSONValue jsonValue = null;
      for (String campaignId : campaignIds) {
        try {
          CampaignDetailedInfo campaign = new CampaignDetailedInfo();
          JSONObject campaignHash = dataHash.get(campaignId).isObject();
          CampaignDetailAwData campaignAwData = (CampaignDetailAwData)campaignHash.getJavaScriptObject();
          campaign.setCampaignId(campaignId);
          campaign.setCampaignName(campaignAwData.getCampaignName());
          // user roles
          JsArrayString rolesOfCurrentUser = campaignAwData.getUserRoles();
          for (int i = 0; i < rolesOfCurrentUser.length(); i++) {
            campaign.addUserRole(RoleCampaign.fromServerString(rolesOfCurrentUser.get(i)));
          }
          JsArrayString supervisors = campaignAwData.getSupervisors();
          if (supervisors != null) { // user might not have permission to see supervisors
            for (int i = 0; i < supervisors.length(); i++) {
              campaign.addSupervisor(supervisors.get(i));
            }
          }
          JsArrayString authors = campaignAwData.getAuthors();
          if (authors != null) {
            for (int i = 0; i < authors.length(); i++) {
              campaign.addAuthor(authors.get(i));
            }
          }
          JsArrayString classes = campaignAwData.getClasses();
          for (int i = 0; i < classes.length(); i++) {
            campaign.addClass(classes.get(i));
          }
          campaign.setDescription(campaignAwData.getDescription());
          campaign.setPrivacy(Privacy.valueOf(campaignAwData.getPrivacyState().toUpperCase()));
          campaign.setRunningState(RunningState.valueOf(campaignAwData.getRunningState().toUpperCase()));
          campaign.setCreationTime(DateUtils.translateFromServerFormat(campaignAwData.getCreationTime()));
          campaign.setXmlConfig(campaignAwData.getXmlConfig());
          
          // save
          campaigns.add(campaign);    
          
        } catch (Exception e) { 
          _logger.warning("Could not parse json for campaign id: " + campaignId + ". Skipping record.");
          _logger.fine(e.getMessage());
          _logger.finer("jsonValue: " + (jsonValue != null ? jsonValue.toString() : "null"));
        }
      }
      
      return campaigns;
    }

    public static List<ClassInfo> translateClassReadQueryJSONToClassInfoList(String classReadQueryJSON) throws Exception {
      List<ClassInfo> classInfos = new ArrayList<ClassInfo>(); // retval
      @SuppressWarnings("deprecation")
      JSONValue value = JSONParser.parse(classReadQueryJSON);
      JSONObject obj = value.isObject();
      if (obj == null || !obj.containsKey("data")) throw new Exception("Invalid json format.");
      JSONObject dataHash = obj.get("data").isObject();
      for (String classId : dataHash.keySet()) {
        try {
          ClassAwData awData = (ClassAwData)dataHash.get(classId).isObject().getJavaScriptObject();
          ClassInfo classInfo = new ClassInfo();
          classInfo.setClassId(classId);
          classInfo.setClassName(awData.getName());
          classInfo.setDescription(awData.getDescription());
          JsArrayString users = awData.getUserNames();
          for (int i = 0; i < users.length(); i++) {
            String username = users.get(i);
            String roleString = awData.getUserRole(username);
            // Restricted users only get a blank string instead of a member role in the json.
            // For simplicity, we set the role to restricted in that case.
            RoleClass role = roleString.isEmpty() ? RoleClass.RESTRICTED : 
                                                    RoleClass.valueOf(awData.getUserRole(username).toUpperCase());
            classInfo.addMember(username, role);
          }
          classInfos.add(classInfo);
        } catch (Exception e) { 
          _logger.warning("Could not parse json for class id: " + classId + ". Skipping record.");
          _logger.fine(e.getMessage());
        }
      }

      return classInfos;
    }
    
    public static List<ClassSearchInfo> translateClassSearchQueryJSONToClassSearchInfoList(String classSearchQueryJSON) throws Exception {
      List<ClassSearchInfo> classSearchInfos = new ArrayList<ClassSearchInfo>(); // retval
      JSONValue value = JSONParser.parseStrict(classSearchQueryJSON);
      JSONObject obj = value.isObject();
      if (obj == null || !obj.containsKey("data")) throw new Exception("Invalid json format.");
      JSONObject dataHash = obj.get("data").isObject();
      for (String classUrn : dataHash.keySet()) {
        try {
          ClassSearchAwData awData = (ClassSearchAwData)dataHash.get(classUrn).isObject().getJavaScriptObject();
          ClassSearchInfo classSearchInfo = new ClassSearchInfo();
          classSearchInfo.setClassUrn(classUrn);
          classSearchInfo.setClassName(awData.getName());
          classSearchInfo.setDescription(awData.getDescription());
          JsArrayString users = awData.getUserNames();
          for (int i = 0; i < users.length(); i++) {
            classSearchInfo.addMember(users.get(i));
          }
          classSearchInfos.add(classSearchInfo);
          JsArrayString campaignUrns = awData.getCampaignUrns();
          for (int i = 0; i < campaignUrns.length(); i++) {
            classSearchInfo.addCampaign(campaignUrns.get(i));
          }
        } catch (Exception e) { 
          _logger.warning("Could not parse json for class urn: " + classUrn + ". Skipping record.");
          _logger.fine(e.getMessage());
        }
      }

      return classSearchInfos;
    }

    // {"result":"success","data":{"7bf3ab79-d30d-4cec-91b0-75d5d337be5d":{"class_role":{},"user_role":"owner","last_modified":"2011-05-26 13:01:03","description":"","name":"testdoc1.txt","privacy_state":"private","campaign_roles":{"urn:campaign:ca:lausd:Addams_HS:CS101:Fall:2011:Advertisement":"reader"},"size":27},"35e795c7-c6dc-4f22-8293-aa3369729b35":{"class_role":{},"user_role":"owner","last_modified":"2011-05-26 13:02:08","description":"","name":"testdoc3.txt","privacy_state":"private","campaign_roles":{"urn:campaign:ca:lausd:Addams_HS:CS101:Fall:2011:Advertisement":"reader"},"size":27},"0c61e063-cc11-46fa-aa13-20f9fbc560e6":{"class_role":{},"user_role":"owner","last_modified":"2011-05-26 13:01:46","description":"","name":"testdoc2.txt","privacy_state":"private","campaign_roles":{"urn:campaign:ca:lausd:Addams_HS:CS101:Fall:2011:Advertisement":"reader"},"size":27}}}
    public static List<DocumentInfo> translateDocumentReadQueryJSONToDocumentInfoList(String documentReadQueryJSON) throws Exception {
      List<DocumentInfo> documentInfos = new ArrayList<DocumentInfo>(); // retval
      JSONValue value = JSONParser.parseStrict(documentReadQueryJSON);
      JSONObject obj = value.isObject();
      if (obj == null || !obj.containsKey("data")) throw new Exception("Invalid json format.");
      JSONObject dataHash = obj.get("data").isObject();
      for (String documentId : dataHash.keySet()) {
        try {
          DocumentAwData awData = (DocumentAwData)dataHash.get(documentId).isObject().getJavaScriptObject();
          DocumentInfo docInfo = new DocumentInfo();
          docInfo.setLastModifiedTimestamp(DateUtils.translateFromServerFormat(awData.getLastModified()));
          //docInfo.setCreationTimestamp(TODO);
          docInfo.setDescription(awData.getDescription());
          docInfo.setDocumentId(documentId);
          docInfo.setDocumentName(awData.getDocumentName());
          docInfo.setCreator(awData.getCreator());
          docInfo.setPrivacy(Privacy.fromServerString(awData.getPrivacyState()));
          docInfo.setSize(awData.getSize());
          docInfo.setUserRole(RoleDocument.fromServerString(awData.getUserRole()));
          docInfo.setUserMaxRole(RoleDocument.fromServerString(awData.getUserMaxRole()));
          for (String classUrn : awData.getClassUrns()) {
            docInfo.addClass(classUrn,
                             RoleDocument.fromServerString(awData.getClassRole(classUrn)));
          }
          for (String campaignUrn : awData.getCampaignUrns()) {
            docInfo.addCampaign(campaignUrn, 
                                RoleDocument.fromServerString(awData.getCampaignRole(campaignUrn)));
          }
          documentInfos.add(docInfo);
        } catch (Exception e) {
          _logger.warning("Could not parse json for document id: " + documentId + ". Skipping record.");
          _logger.fine(e.getMessage());
        }
      }
      return documentInfos;
    }

    // {"result":"success","data":{"application_build":"55e80e9","application_name":"ohmage","application_version":"2.5","default_survey_response_sharing_state":"private"}}
    public static AppConfig translateAppConfigReadQueryToAppConfig(String result) {
      AppConfig appConfig = new AppConfig();
      AppConfigAwData awData = AppConfigAwData.fromJsonString(result);
      AppConfig.setAppName(awData.getApplicationName());
      if (awData.mobilityEnabledFlagExists()) {
        AppConfig.setMobilityEnabled(awData.getMobilityEnabled());
      } else { // Default behavior is "true" if no flag exists
    	AppConfig.setMobilityEnabled(true);
      }
      return appConfig;
    }

    public static List<AuditLogEntry> translateAuditReadQueryJSONToAuditLogEntryList(String auditReadQueryJSON) throws Exception {
      DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss.s");
      List<AuditLogEntry> auditLogEntries = new ArrayList<AuditLogEntry>(); // retval
      JSONValue value = JSONParser.parseStrict(auditReadQueryJSON);
      JSONObject obj = value.isObject();
      if (obj == null || !obj.containsKey("audits")) throw new Exception("Invalid json format.");
      JSONArray auditList = obj.get("audits").isArray();
      for (int i = 0; i < auditList.size(); i++) {
        try {
          AuditLogAwData awData = (AuditLogAwData)auditList.get(i).isObject().getJavaScriptObject();
          AuditLogEntry audit = new AuditLogEntry();
          audit.setTimestamp(dateTimeFormat.parse(awData.getTimestamp()));
          audit.setClient(awData.getClient());
          audit.setExtraDataJson(awData.getExtraData().toSource());
          audit.setReceivedMillis(awData.getReceivedMillis());
          audit.setRespondedMillis(awData.getRespondedMillis());
          audit.setRequestParamsJson(awData.getRequestParams().toSource());
          audit.setRequestType(RequestType.fromServerString(awData.getRequestType()));
          audit.setResponseStatus(ResponseStatus.fromServerString(awData.getResponseStatus()));
          audit.setUri(awData.getUri());
          audit.setTimeToFillRequest(audit.getRespondedMillis() - audit.getReceivedMillis());
          auditLogEntries.add(audit);
        } catch (Exception e) {
          _logger.warning("Could not parse json for audit log entry " + i + ", skipping entry.");
          _logger.fine(e.getMessage());
        }
      }
      return auditLogEntries;
    }

    
	public static List<MobilityInfo> translateMobilityReadQueryJSONToMobilityInfoList(String mobilityReadQueryJSON) throws Exception {
		List<MobilityInfo> mobilityInfos = new ArrayList<MobilityInfo>(); // retval
		
		JSONValue value = JSONParser.parseStrict(mobilityReadQueryJSON);
		JSONObject obj = value.isObject();
		if (obj == null || !obj.containsKey("data")) throw new Exception("Invalid json format.");
		JSONArray dataHash = obj.get("data").isArray();
		if (dataHash == null) throw new Exception("dataHash has invalid json format.");
		
		for (int i = 0; i < dataHash.size(); i++) {
			try {
				//parse mobility array JSON
				MobilityDataPointAwData awData = (MobilityDataPointAwData)dataHash.get(i).isObject().getJavaScriptObject();
				MobilityInfo mobInfo = new MobilityInfo();
				
				mobInfo.setMode(MobilityMode.fromServerString(awData.getMode()));
				mobInfo.setDate(new Date(awData.getTime()));
				//mobInfo.setDate(DateUtils.translateFromServerFormat(awData.getTimestamp()));	//original
				mobInfo.setTimezone(awData.getTimezone());
				mobInfo.setLocationStatus(LocationStatus.fromServerString(awData.getLocStatus()));
				if (mobInfo.getLocationStatus() != LocationStatus.UNAVAILABLE) {
					MobilityLocationAwData l = awData.getLocation();
					mobInfo.setLocationLat(l.getLatitude());
					mobInfo.setLocationLong(l.getLongitude());
					mobInfo.setLocationAccuracy(l.getAccuracy());
					mobInfo.setLocationProvider(l.getProvider());
					mobInfo.setLocationTimestamp(new Date(l.getTime()));
				}
				//add to list "mobilityInfos"
				mobilityInfos.add(mobInfo);
			} catch (Exception e) {
				_logger.warning("Could not parse json for mobility id: " + dataHash.get(i) + ". Skipping record.");
				_logger.fine(e.getMessage());
			}
		}
		
		return mobilityInfos;
	}
	
	public static List<MobilityChunkedInfo> translateMobilityReadChunkedQueryJSONToMobilityChunkedInfoList(String mobilityReadChunkedQueryJSON) throws Exception {
		List<MobilityChunkedInfo> mobilityInfos = new ArrayList<MobilityChunkedInfo>(); // retval
		
		JSONValue value = JSONParser.parseStrict(mobilityReadChunkedQueryJSON);
		JSONObject obj = value.isObject();
		if (obj == null || !obj.containsKey("data")) throw new Exception("Invalid json format.");
		JSONArray dataHash = obj.get("data").isArray();
		if (dataHash == null) throw new Exception("dataHash has invalid json format.");
		
		for (int i = 0; i < dataHash.size(); i++) {
			try {
				//parse mobility array JSON
				MobilityChunkedDataPointAwData awData = (MobilityChunkedDataPointAwData)dataHash.get(i).isObject().getJavaScriptObject();
				MobilityChunkedInfo mobInfo = new MobilityChunkedInfo();
				
				Map<String, Integer> mc_str = awData.getModesCountObj().getMobilityModeCount();
				Map<MobilityMode, Integer> mc = new HashMap<MobilityMode, Integer>();
				for (String key : mc_str.keySet()) {
					mc.put(MobilityMode.fromServerString(key), mc_str.get(key));
				}
				mobInfo.setModeCount(mc);
				
				mobInfo.setDuration(awData.getDuration());
				mobInfo.setDate(DateUtils.translateFromServerFormat(awData.getTimeStamp()));	//old format
				//mobInfo.setDate(DateUtils.translateFromEpochServerFormat(awData.getTimestamp()));	//epoch long msec
				mobInfo.setTimezone(awData.getTimezone());
				mobInfo.setLocationStatus(LocationStatus.fromServerString(awData.getLocStatus()));
				
				if (mobInfo.getLocationStatus() != LocationStatus.UNAVAILABLE) {
					MobilityLocationAwData l = awData.getLocation();
					
					mobInfo.setLocationLat(l.getLatitude());
					mobInfo.setLocationLong(l.getLongitude());
					mobInfo.setLocationAccuracy(l.getAccuracy());
					mobInfo.setLocationProvider(l.getProvider());
					mobInfo.setLocationTimestamp(new Date(l.getTime()));
				}
				
				//add to list "mobilityInfos"
				mobilityInfos.add(mobInfo);
			} catch (Exception e) {
				_logger.warning("Could not parse json for mobility id: " + dataHash.get(i) + ". Skipping record.");
				_logger.fine(e.getMessage());
			}
		}
		
		return mobilityInfos;
	}
	
	public static List<Date> translateMobilityDatesReadQueryJSONToDatesList(String mobilityDatesReadQueryJSON) throws Exception {
		List<Date> mobilityDates = new ArrayList<Date>(); // retval
		
		JSONValue value = JSONParser.parseStrict(mobilityDatesReadQueryJSON);
		JSONObject obj = value.isObject();
		if (obj == null || !obj.containsKey("data")) throw new Exception("Invalid json format.");
		JSONArray data = obj.get("data").isArray();
		if (data == null) throw new Exception("dataHash has invalid json format.");
		
		for (int i = 0; i < data.size(); i++) {
			try {
				JSONValue jsonVal = data.get(i);
				JSONString jsonStr = jsonVal.isString();
				String dateStr = jsonStr.stringValue();
				Date d = DateUtils.translateFromServerFormat(dateStr);
				mobilityDates.add(d);
			} catch (Exception e) {
				_logger.warning("Could not parse json for mobility date: " + data.get(i) + ". Skipping record.");
				_logger.fine(e.getMessage());
			}
		}
		
		return mobilityDates;
	}


}
