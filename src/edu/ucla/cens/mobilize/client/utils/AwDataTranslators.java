package edu.ucla.cens.mobilize.client.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import edu.ucla.cens.mobilize.client.common.UserRole;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.CampaignInfo;
import edu.ucla.cens.mobilize.client.model.CampaignsAwData;
import edu.ucla.cens.mobilize.client.model.ConfigQueryAwData;
import edu.ucla.cens.mobilize.client.model.ConfigurationInfo;
import edu.ucla.cens.mobilize.client.model.ConfigurationsAwData;
import edu.ucla.cens.mobilize.client.model.DataPointAwData;
import edu.ucla.cens.mobilize.client.model.PromptInfo;
import edu.ucla.cens.mobilize.client.model.PromptResponse;
import edu.ucla.cens.mobilize.client.model.SurveyInfo;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.model.UserInfoOld;

/**
 * A collection of translators to translate data from the AndWellness server to local
 * data structures.
 * 
 * @author jhicks
 *
 */
public class AwDataTranslators {
    // Logging utility
    private static Logger _logger = Logger.getLogger(AwDataTranslators.class.getName());
    
    /**
     * Translates a ConfigQueryAwData server response into a UserInfo object.  The UserInfo object
     * is fairly complex, consisting of CampaignInfos, which contain SurveyInfos, which contain PromptInfos.
     * Most of this is translated from the XML configuration which is contained in the config query response.
     * 
     * @param userName The user name to use in the user info object.
     * @param awData The andwellness json to translate.
     * @return The translated UserInfo.
     */
    public static UserInfoOld translateConfigQueryAwDataToUserInfo(String userName, ConfigQueryAwData awData) {
        UserInfoOld userInfo = new UserInfoOld();
        userInfo.setUserName(userName);
        
        _logger.finer("Creating a UserInfo for user: " + userName);
        
        // Run through the campaigns, storing each in a CampaignInfo
        JsArray<CampaignsAwData> campaignListJS = awData.getCampaignList();
        for (int i = 0; i < campaignListJS.length(); ++i) {
            CampaignInfo newCampaignInfo = translateCampaignsAwDataToCampaignInfo(campaignListJS.get(i));
            userInfo.addCampaign(newCampaignInfo);
        }
        
        return userInfo;
    }
    
    /**
     * Translates a CampaignsAwData from the AW server config API into a CampaignInfo object.
     * 
     * @param awData The JSON to translate.
     * @return The translated CampaignInfo object.
     */
    public static CampaignInfo translateCampaignsAwDataToCampaignInfo(CampaignsAwData awData) {
        CampaignInfo campaignInfo = new CampaignInfo();
        
        _logger.finer("Creating a CampaignInfo with campaign name: " + awData.getCampaignName());

        // Set the basic campaign information
        campaignInfo.setCampaignName(awData.getCampaignName());
        
        // Now set the correct user role
        String userRoleString = awData.getUserRole();
        if ("participant".equals(userRoleString)) {
          campaignInfo.setUserRole(UserRole.PARTICIPANT);
        }
        else if ("administrator".equals(userRoleString)) {
          campaignInfo.setUserRole(UserRole.ADMIN);
        }
        else if ("researcher".equals(userRoleString)) {
          campaignInfo.setUserRole(UserRole.RESEARCHER);
        }
        else if ("author".equals(userRoleString)) {
          campaignInfo.setUserRole(UserRole.AUTHOR);
        } 
        else if ("supervisor".equals(userRoleString)) {
          campaignInfo.setUserRole(UserRole.SUPERVISOR);
        } 
        else if ("analyst".equals(userRoleString)) {
          campaignInfo.setUserRole(UserRole.ANALYST);
        }
        else {
            _logger.warning("Do not understand the user role in the JSON: " + userRoleString);
        }
        
        // Copy over the list of user names
        JsArrayString userListJS = awData.getUserList();
        for (int i = 0; i < userListJS.length(); ++i) {
            campaignInfo.addUser(userListJS.get(i));
        }
        
        // For each configuration data, add a ConfigurationInfo
        JsArray<ConfigurationsAwData> configInfoJS = awData.getConfigurations();
        for (int i = 0; i < configInfoJS.length(); ++i) {
            ConfigurationInfo configInfo = translateConfigurationsAwDataToConfigurationInfo(configInfoJS.get(i));
            campaignInfo.addConfiguration(configInfo);
        }
        
        return campaignInfo;
    }
    
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
}
