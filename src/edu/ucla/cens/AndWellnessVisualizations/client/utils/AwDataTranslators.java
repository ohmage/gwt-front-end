package edu.ucla.cens.AndWellnessVisualizations.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;

import edu.ucla.cens.AndWellnessVisualizations.client.model.AuthorizationTokenQueryAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.CampaignInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.model.ConfigQueryAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.UserInfo;

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
     * Translates AuthorizationTokenQueryAwData from the AW server to a UserInfo object.
     * 
     * @param userName The user name to use in the user info object.
     * @param awData The andwellness json to translate.
     * @return The translated UserInfo.
     */
    public static UserInfo translateAwDataToUserInfo(String userName, AuthorizationTokenQueryAwData awData) {
        UserInfo userInfo = new UserInfo();
        
        userInfo.setUserName(userName);
        userInfo.setCampaignMembershipList(awData.getStringCampaignNameList());
        userInfo.setAuthToken(awData.getAuthorizationToken());
        
        return userInfo;
    }
    
    /**
     * Translates a ConfigQueryAwData into the CampaignInfo singleton.
     * 
     * @param configQuery
     */
    public static void translateConfigQueryAwData(ConfigQueryAwData configQuery) {
        CampaignInfo campaignInfo = CampaignInfo.getInstance();
        String userRoleString;
        List<String> userList = new ArrayList<String>();
        List<String> dataPointList;
        
        // Clear out anything currently in the campaign info
        campaignInfo.clear();
        
        // Now run through the AwData and insert everything necessary
        userRoleString = configQuery.getUserRole();
        
        // Translate the user role into the correct enum
        if ("researcher".equals(userRoleString)) {
            campaignInfo.setUserRole(CampaignInfo.UserRole.RESEARCHER);
        }
        else if ("admin".equals(userRoleString)) {
            campaignInfo.setUserRole(CampaignInfo.UserRole.ADMIN);
        }
        else if ("participant".equals(userRoleString)) {
            campaignInfo.setUserRole(CampaignInfo.UserRole.PARTICIPANT);
        }
        else {
            _logger.severe("Unknown user role: " + userRoleString);
        }
        
        // Translate the user list json into a string array
        JsArrayString userListJs = configQuery.getSpecialId();
        for (int i = 0; i < userListJs.length(); ++i) {
            userList.add(userListJs.get(i));
        }
        campaignInfo.setUserList(userList);
        
        // Pull out the XML configuration, pull out the data point ids
        String xmlConfiguration = configQuery.getConfigurationXML();
        campaignInfo.setXmlConfiguration(xmlConfiguration);
        
        try {
            dataPointList = parseXMLForPromptId(xmlConfiguration);
        }
        catch (DOMParseException exception) {
            // This is quite bad, write out a severe message to log and continue to raise
            _logger.severe("Unable to parse the XML configuration from the server.");
            throw exception;
        }

        campaignInfo.setDataPointIdList(dataPointList);
        
        // Now pull out the special IDs and add
        JsArrayString specialIdJs = configQuery.getSpecialId();
        for (int i = 0; i < specialIdJs.length(); ++i) {
            String specialId = specialIdJs.get(i);
            campaignInfo.addDataPointId(specialId);
            
            _logger.finer("Adding special id: " + specialId);
        }
    }
    
    /**
     * Parses an XML string for id nodes within prompt nodes.  Returns the contents of the
     * id nodes in a string list.
     * 
     * @param xmlToParse The XML string to parse.
     * @return The list of prompt ids.
     */
    private static List<String> parseXMLForPromptId(String xmlToParse) throws DOMParseException {
        List<String> promptIdList = new ArrayList<String>();
        
        // Parse the XML here, hope this isn't malicious XML...
        Document xmlDocument = XMLParser.parse(xmlToParse);
        
        NodeList nodes = xmlDocument.getElementsByTagName("prompt");
        
        _logger.finer("Found " + nodes.getLength() + " prompt nodes");
        
        // Grab all the children of the prompt nodes, search for the id node
        for (int i = 0; i < nodes.getLength(); ++i) {
            NodeList childNodes = nodes.item(i).getChildNodes();
            
            for (int j = 0; j < childNodes.getLength(); ++j) {
                Node childNode = childNodes.item(j);
                // Is this the id node?
                if ("id".equals(childNode.getNodeName())) {
                    // Grab the id and insert into our list
                    String promptId = childNode.getChildNodes().item(0).getNodeValue();
                    promptIdList.add(promptId);
                    
                    _logger.finer("Found prompt id: " + promptId);
                }
            }
        }
        
        return promptIdList;
    }
}
