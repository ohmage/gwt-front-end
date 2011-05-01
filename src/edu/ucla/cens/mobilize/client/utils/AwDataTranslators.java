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
import edu.ucla.cens.mobilize.client.common.RunningState;
import edu.ucla.cens.mobilize.client.common.UserRole;
import edu.ucla.cens.mobilize.client.common.UserRoles;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.CampaignDetailAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.ConfigurationsAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.DataPointAwData;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.UserInfoAwData;
import edu.ucla.cens.mobilize.client.model.CampaignConciseInfo;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.CampaignInfo;
import edu.ucla.cens.mobilize.client.model.ConfigurationInfo;
import edu.ucla.cens.mobilize.client.model.PromptInfo;
import edu.ucla.cens.mobilize.client.model.SurveyInfo;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.model.UserInfo;

// json
import com.google.gwt.json.client.JSONArray;
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
     * Translates a ConfigurationsAwData from the AW server into a ConfigurationInfo object.
     * 
     * @param awData The server data to translate.
     * @return The translated ConfigurationInfo.
     */
    public static ConfigurationInfo translateConfigurationsAwDataToConfigurationInfo(ConfigurationsAwData awData) {
        ConfigurationInfo configInfo = new ConfigurationInfo();
     
        _logger.finer("Creating a ConfigurationInfo with campaign version: " + awData.getCampaignVersion());
        
        configInfo.setXmlConfiguration(awData.getCampaignConfiguration());
        
        // Now setup the surveys based on the xml configuration
        configInfo.setSurveyList(translateCampaignConfigurationToSurveyList(awData.getCampaignConfiguration()));
        
        return configInfo;
    }
    
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
    
    // TODO: what exceptions can be thrown and where should they be caught?
    public static List<SurveyResponse> translateDataPointsToSurveyResponses(
        List<DataPointAwData> dataPoints, CampaignDetailedInfo campaignInfo) {

      HashMap<String, SurveyResponse> responses = new HashMap<String, SurveyResponse>();
      
      for (DataPointAwData dataPoint : dataPoints) {
        String surveyId = dataPoint.getSurveyId();
        SurveyInfo surveyInfo = campaignInfo.getSurvey(surveyId);
        if (surveyInfo != null) {
          if (!responses.containsKey(surveyId)) {
            SurveyResponse obj = new SurveyResponse();
            obj.setCampaignId(campaignInfo.getCampaignId());
            obj.setCampaignName(campaignInfo.getCampaignName());
            obj.setResponseDate(dataPoint.getTimeStamp());
            obj.setPrivacyState(dataPoint.getPrivacyState()); // FIXME: what if datapoints (prompts) have conflicting privacy?
            obj.setSurveyId(surveyId);
            if (surveyInfo != null) obj.setSurveyName(surveyInfo.getSurveyName());
            responses.put(surveyId, obj);
          }
          // NOTE: if promptId is not found in the survey info (meaning prompt
          // did not appear for that survey in the xml config) promptInfo will
          // be null here. PromptResponse obj checks for null and falls back to 
          // using the dataPoint label
          PromptInfo promptInfo = surveyInfo.getPrompt(dataPoint.getPromptId()); // can be null
          SurveyResponse surveyResponse = responses.get(surveyId);
          surveyResponse.addPromptResponse(promptInfo, dataPoint);
        } else {
          // data point does not match any survey in campaign info
          // log warning/error
        }
      }
      
      return new ArrayList<SurveyResponse>(responses.values());
    }
    
    // Expects json like:
    // {"result":"success","data":{"temp.user":{"classes":["andwellness"],"roles":["supervisor"],"permissions":{"cancreate":true}}}}
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
          Map<String, String> classNameToIdMap = userDataJSObject.getClasses();
          Map<String, String> classIdToNameMap = new HashMap<String, String>();
          for (String className : classNameToIdMap.keySet()) {
            classIdToNameMap.put(classNameToIdMap.get(className), className);
          }
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
    public static List<CampaignConciseInfo> translateCampaignReadQueryJSONtoCampaignConciseInfoList(
        String responseText) {
      
      // List that will be returned
      List<CampaignConciseInfo> campaigns = new ArrayList<CampaignConciseInfo>(); 
      
      // Parse response obj
      @SuppressWarnings("deprecation")
      JSONValue value = JSONParser.parse(responseText);
      JSONObject responseObj = value.isObject();
      
      // Get data field from response. It's a hash with campaign ids as 
      // keys and serialized campaign info as values.
      if (responseObj == null || !responseObj.containsKey("data")) return null;
      
      JSONObject dataHash = responseObj.get("data").isObject();
      
      // For each campaign, translate the serialized info into a 
      // CampaignConciseInfo object and save
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
          CampaignConciseInfo campaignInfo = new CampaignConciseInfo(campaignId,
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
    

    
}
