package edu.ucla.cens.mobilize.client.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.PromptType;
import edu.ucla.cens.mobilize.client.common.RunningState;
import edu.ucla.cens.mobilize.client.common.UserRole;
import edu.ucla.cens.mobilize.client.common.UserRoles;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.CampaignDetailAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.ClassAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.PromptResponseAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.UserInfoAwData;
import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.ClassInfo;
import edu.ucla.cens.mobilize.client.model.PromptInfo;
import edu.ucla.cens.mobilize.client.model.PromptResponse;
import edu.ucla.cens.mobilize.client.model.SurveyInfo;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.model.UserInfo;

// json
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * A collection of translators to translate data from the AndWellness server to local
 * data structures.
 * 
 * @author jhicks
 * @author vhajdik
 *
 */
public class AwDataTranslators {
    // Logging utility
    private static Logger _logger = Logger.getLogger(AwDataTranslators.class.getName());
    
    /**
     * Translates a campaign configuration file from the AW server config API into a list of
     * survey information.  The prompt info is hidden fairly deep in the XML tree, so there
     * are a good number of nested loops here.
     * 
     * @param campaignConfiguration The XML campaign configuration to translate.
     * @return The translated list of survey information.
     */
    public static List<SurveyInfo> translateCampaignConfigurationToSurveyList(String campaignConfiguration) {
        List<SurveyInfo> surveyInfoList = new ArrayList<SurveyInfo>();
        
        // XML parse the campaign configuration
        Document xmlDocument = XMLParser.parse(campaignConfiguration);

        // Find all survey nodes
        NodeList surveyNodes = xmlDocument.getElementsByTagName("survey");
        // Loop through the survey nodes, create a new SurveyInfo for each
        for (int i = 0; i < surveyNodes.getLength(); ++i) {
            SurveyInfo surveyInfo = new SurveyInfo();
            Node surveyNode = surveyNodes.item(i);
            
            // Loop over every child in the survey node, add to the survey info based on the node name
            NodeList surveyNodeChildren = surveyNode.getChildNodes();
            for (int j = 0; j < surveyNodeChildren.getLength(); ++j) {
                Node surveyNodeChild = surveyNodeChildren.item(j);
                String surveyNodeChildName = surveyNodeChild.getNodeName();
                
                if ("id".equals(surveyNodeChildName)) {
                    surveyInfo.setSurveyName(surveyNodeChild.getChildNodes().item(0).getNodeValue());
                }
                
                if ("title".equals(surveyNodeChildName)) {
                    surveyInfo.setSurveyTitle(surveyNodeChild.getChildNodes().item(0).getNodeValue());
                }
                
                if ("description".equals(surveyNodeChildName)) {
                    surveyInfo.setSurveyDescription(surveyNodeChild.getChildNodes().item(0).getNodeValue());
                }
                
                if ("contentList".equals(surveyNodeChildName)) {
                    // The content list contains either a prompt or a repeatableSet
                    NodeList contentListNodes = surveyNodeChild.getChildNodes();
                    for (int k = 0; k < contentListNodes.getLength(); ++k) {
                        Node contentListNode = contentListNodes.item(k);
                        
                        // Translate prompt directly
                        if ("prompt".equals(contentListNode.getNodeName())) {
                            surveyInfo.addPrompt(translatePromptNodeToPromptInfo(contentListNode));
                        }
                        // repeatableSets have a prompts node which contains a list of prompt (2 more levels to go)
                        if ("repeatableSet".equals(contentListNode.getNodeName())) {
                            NodeList repeatableSetNodes = contentListNode.getChildNodes();
                            for (int l = 0; l < repeatableSetNodes.getLength(); ++l) {
                                Node repeatableSetNode = repeatableSetNodes.item(l);
                                if ("prompts".equals(repeatableSetNode.getNodeName())){
                                    NodeList promptsNodes = repeatableSetNode.getChildNodes();
                                    // We finally have gotten to the actual prompts
                                    for (int m = 0; m < promptsNodes.getLength(); ++m) {
                                        Node promptsNode = promptsNodes.item(m);
                                        // There should only be prompt nodes here, but check to be sure anyway
                                        if ("prompt".equals(promptsNode.getNodeName())) {
                                            surveyInfo.addPrompt(translatePromptNodeToPromptInfo(promptsNode));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            _logger.finer("Created a SurveyInfo for survey: " + surveyInfo.getSurveyName());
            
            // Add the completed SurveyInfo to the overall survey info list
            surveyInfoList.add(surveyInfo);
        }
        
        return surveyInfoList;
    }
    
    /**
     * Translates a com.google.gwt.xml.client.Node representing a prompt into a PromptInfo object.
     * 
     * @param promptNode The XML Node to translate.
     * @return The translated PromptInfo.
     */
    public static PromptInfo translatePromptNodeToPromptInfo(Node promptNode) {
        PromptInfo promptInfo = new PromptInfo();

        // Loop over the prompt node children, finding the nodes we need
        NodeList promptNodeChildren = promptNode.getChildNodes();
        for (int i = 0; i < promptNodeChildren.getLength(); ++i) {
            Node promptNodeChild = promptNodeChildren.item(i);
            String promptNodeChildName = promptNodeChild.getNodeName();
            
            if ("id".equals(promptNodeChildName)) {
                promptInfo.setPromptId(promptNodeChild.getChildNodes().item(0).getNodeValue());
            }
            if ("promptType".equals(promptNodeChildName)) {
                promptInfo.setPromptType(promptNodeChild.getChildNodes().item(0).getNodeValue());
            }
            if ("displayLabel".equals(promptNodeChildName)) {
                promptInfo.setDisplayLabel(promptNodeChild.getChildNodes().item(0).getNodeValue());
            }
            if ("displayType".equals(promptNodeChildName)) {
                promptInfo.setDisplayType(promptNodeChild.getChildNodes().item(0).getNodeValue());
            }
            if ("unit".equals(promptNodeChildName)) {
                promptInfo.setUnit(promptNodeChild.getChildNodes().item(0).getNodeValue());
            }
        }
        
        _logger.finer("Created a prompt with id: " + promptInfo.getPromptId());
        
        return promptInfo;
    }
    
    // returns null if there were no responses
    public static List<SurveyResponse> translateSurveyResponseReadQueryJSONToSurveyResponseList(
        String promptResponseReadQueryJSON,
        String campaignId) throws Exception {
      @SuppressWarnings("deprecation")
      JSONValue value = JSONParser.parse(promptResponseReadQueryJSON);
      JSONObject responseHash = value.isObject();
      if (responseHash == null) throw new Exception("Invalid json response: " + responseHash);
      
      // if there were no responses for this query (for this campaign) return empty list immediately
      JSONNumber numberOfPrompts = responseHash.get("metadata").isObject().get("number_of_prompts").isNumber();
      if (numberOfPrompts.doubleValue() < 1) return new ArrayList<SurveyResponse>();
      
      // data field contains a js array of prompt response json objects
      if (!responseHash.containsKey("data")) throw new Exception("data field missing");
      JSONValue dataValue = responseHash.get("data");
      JSONArray array = dataValue.isArray();
      if (array == null) throw new Exception("Json in data field of response is not an array.");
      Map<Integer, SurveyResponse> surveyResponsesByKey = new HashMap<Integer, SurveyResponse>();
      for (int i = 0; i < array.size(); i++) {
        try {
          // parse as overlay object
          JSONObject obj = array.get(i).isObject();
          if (obj == null) throw new Exception("Invalid json: " + array.get(i).toString());
          PromptResponseAwData promptAwData = (PromptResponseAwData)obj.getJavaScriptObject();
          
          // copy data into PromptResponse object
          PromptResponse promptResponse = new PromptResponse();
          promptResponse.setPromptId(promptAwData.getPromptId());
          promptResponse.setText(promptAwData.getPromptText());
          promptResponse.setPromptType(PromptType.fromString(promptAwData.getPromptType()));
          promptResponse.setResponse(promptAwData.getPromptResponse());
          promptResponse.setResponsePreparedForDisplay(getPromptResponseDisplayString(campaignId, promptResponse.getPromptType(), promptAwData));
          Integer surveyResponseKey = promptAwData.getSurveyResponseKey();
          if (surveyResponseKey == null) throw new Exception("No survey response key found in json: " + promptAwData.toString());

          // One survey response contains many prompt responses. Every prompt response
          // returned by the data api contains info about its parent survey response
          // (uniquely identified by the survey response key) but the survey response
          // info only needs to be saved the first time that key is seen.
          if (!surveyResponsesByKey.containsKey(surveyResponseKey)) {
            SurveyResponse surveyResponse = new SurveyResponse();
            surveyResponse.setResponseKey(surveyResponseKey);
            surveyResponse.setCampaignId(campaignId); 
            // NOTE: campaignName not included in prompt data, must be filled in later
            surveyResponse.setResponseDate(promptAwData.getTimestamp());
            surveyResponse.setUserName(promptAwData.getUser());
            surveyResponse.setSurveyId(promptAwData.getSurveyId());
            surveyResponse.setSurveyName(promptAwData.getSurveyTitle());
            // GOTCHA: assumes privacy is same for all prompts within a survey response.
            // If we enabled per-prompt privacy instead of per-survey, this would break.
            surveyResponse.setPrivacyState(Privacy.valueOf(promptAwData.getPrivacy().toUpperCase()));
            surveyResponsesByKey.put(surveyResponse.getResponseKey(), surveyResponse);
          }
          
          // store prompt response object in its parent survey response object
          surveyResponsesByKey.get(surveyResponseKey).addPromptResponse(promptResponse);
          
        } catch (Exception e) {
          _logger.severe("Skipping unparseable prompt response. Error was: " + e.getMessage());
          _logger.finer("Prompt response json was: " + array.get(i));
        }
      }
      // TODO: sort responses?      
      List<SurveyResponse> surveyResponses = new ArrayList<SurveyResponse>();
      surveyResponses.addAll(surveyResponsesByKey.values());
      return surveyResponses;
    }
    
    public static String getPromptResponseDisplayString(String campaignId,
                                                        PromptType promptType,
                                                        PromptResponseAwData promptResponseAwData) {
      String displayString = null;
      switch (promptType) {
      case PHOTO:
        // prompt response is the image uuid
        displayString = AwUrlBasedResourceUtils.getImageUrl(campaignId, promptResponseAwData.getPromptResponse());
        break;
      case MULTI_CHOICE:
      case MULTI_CHOICE_CUSTOM:
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
      case SINGLE_CHOICE_CUSTOM:
        String promptResponseStringValue = promptResponseAwData.getPromptResponse();
        displayString = promptResponseAwData.getChoiceLabelFromGlossary(promptResponseStringValue);
        break;
      default: // for all other types, just use the original string
        displayString = promptResponseAwData.getPromptResponse();
        break;
      }
      return displayString;  
    }
    
    // Expects json like:
    // {"result":"success","data":{"user.adv.supa":{"classes":{"urn:class:ca:lausd:BoyleHeights_HS:CS102:Spring:2011":"BH_HS_CS102_Spring_2011","urn:class:ca:lausd:Addams_HS:CS101:Fall:2011":"Addams_HS_CS101_Fall_2011"},"permissions":{"cancreate":true},"campaign_roles":["participant","author","analyst","supervisor"],"class_roles":["privileged"]}}}
    public static List<UserInfo> translateUserReadQueryJSONToUserInfoList(String userReadQueryResponseJSON) {
      List<UserInfo> users = new ArrayList<UserInfo>();
      
      // Parse response obj
      @SuppressWarnings("deprecation")
      JSONValue value = JSONParser.parse(userReadQueryResponseJSON);
      JSONObject responseObj = value.isObject();
      
      // Get data field from response. It's a hash with usernames as keys and
      // serialized userinfos as values.
      if (responseObj == null || !responseObj.containsKey("data")) return null;
      JSONObject userNameToUserDataHash = responseObj.get("data").isObject();
      
      // For each user, translate the serialized info into a UserInfo object and save
      if (userNameToUserDataHash == null) return null;
      Set<String> userNames = userNameToUserDataHash.keySet();
      JSONValue jsonValue = null;
      for (String userName : userNames) {
        // wrap in try/catch because it uses a UserInfoAwData
        try { 
          jsonValue = userNameToUserDataHash.get(userName);
          JSONObject userJSONObject = jsonValue.isObject();
          if (userJSONObject == null) throw new Exception("user data field not a valid JSON object");
          UserInfoAwData userDataJSObject = (UserInfoAwData)userJSONObject.getJavaScriptObject();
          boolean canCreateFlag = userDataJSObject.getCanCreateFlag();
          Map<String, String> classIdToNameMap = userDataJSObject.getClasses();
          List<String> rolesAsStrings = userDataJSObject.getRoles();
          List<UserRole> roles = new ArrayList<UserRole>();
          for (String roleString : rolesAsStrings) {
            roles.add(UserRole.valueOf(roleString.toUpperCase()));
          }
          UserInfo userInfo = new UserInfo(userName, canCreateFlag, classIdToNameMap, roles);
          users.add(userInfo);
        } catch (Exception e) { // FIXME: which exceptions?
          _logger.warning("Could not parse json for user: " + userName + ". Skipping record.");
          _logger.fine(e.getMessage());
          _logger.finer("jsonValue: " + (jsonValue != null ? jsonValue.toString() : "null"));
        }
      }
      return users;
    }

    
    // FIXME: correct example when server response is fixed to get rid of extra array
    // {"result":"success","data":{"urn:andwellness:nih":{"user_roles":["supervisor"],"name":"NIH","privacy_state":"private","creation_timestamp":"2011-04-12 15:33:34.0","running_state":"active"}},"metadata":{"items":["urn:andwellness:nih"],"number_of_results":1}}
    public static List<CampaignShortInfo> translateCampaignReadQueryJSONtoCampaignShortInfoList(
        String responseText) {
      
      // List that will be returned
      List<CampaignShortInfo> campaigns = new ArrayList<CampaignShortInfo>(); 
      
      // Parse response obj
      @SuppressWarnings("deprecation")
      JSONValue value = JSONParser.parse(responseText);
      JSONObject responseObj = value.isObject();
      
      // Get data field from response. It's a hash with campaign ids as 
      // keys and serialized campaign info as values.
      if (responseObj == null || !responseObj.containsKey("data")) return null;
      
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
            userRoles.addRole(UserRole.valueOf(userRoleString));
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
          
        } catch (Exception e) { // FIXME: which exceptions?
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
      
      // Get data field from response. It's a hash with campaign ids as 
      // keys and serialized campaign info as values.
      if (responseObj == null || !responseObj.containsKey("data")) return null;
      
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
          JsArrayString supervisors = campaignAwData.getSupervisors();
          for (int i = 0; i < supervisors.length(); i++) {
            campaign.addSupervisor(supervisors.get(i));
          }
          JsArrayString authors = campaignAwData.getAuthors();
          for (int i = 0; i < authors.length(); i++) {
            campaign.addAuthor(authors.get(i));
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
          
        } catch (Exception e) { // FIXME: which exceptions?
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
          JsArrayString privilegedUsers = awData.getPrivilegedUsers();
          for (int i = 0; i < privilegedUsers.length(); i++) {
            classInfo.addPrivilegedMember(privilegedUsers.get(i)); 
          }
          JsArrayString restrictedUsers = awData.getRestrictedUsers();
          for (int i = 0; i < restrictedUsers.length(); i++) {
            classInfo.addMember(restrictedUsers.get(i));
          }
          classInfos.add(classInfo);
        } catch (Exception e) { // FIXME: which exception?
          _logger.warning("Could not parse json for class id: " + classId + ". Skipping record.");
          _logger.fine(e.getMessage());
        }
      }

      return classInfos;
    }
    

    
}
