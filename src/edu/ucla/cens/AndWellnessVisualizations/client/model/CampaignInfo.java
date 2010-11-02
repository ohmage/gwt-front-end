package edu.ucla.cens.AndWellnessVisualizations.client.model;

import java.util.Collections;
import java.util.List;

/**
 * Singleton class to store information about the current AndWellness campaign.
 * 
 * @author jhicks
 *
 */
public class CampaignInfo {
    private static CampaignInfo campaignInfo = null;
    
    // Data fields necessary to describe a campaign configuration
    public enum UserRole {
        PARTICIPANT, ADMIN, RESEARCHER;
    };
    
    private UserRole userRole;  // Describes the user's campaign permissions
    private List<String> userList;  // A list of the users in enrolled in the current campaign
    private List<String> dataPointIdList;  // A list of data points in the campaign
    private String xmlConfiguration;  // The XML used to configure this campaign
    
    // Singleton, make the ctor private
    private CampaignInfo() {};
    
    // Grab an instance of the class
    public static CampaignInfo getInstance() {
        if (campaignInfo == null) {
            campaignInfo = new CampaignInfo();
        }
        
        return campaignInfo;
    }
    
    // Reset the campaign info with new information
    public void clear() {
        campaignInfo = new CampaignInfo();
    }
    
    // Setters and getters
    public void setUserRole(UserRole userRole) { this.userRole = userRole; }
    public UserRole getUserRole() { return userRole; }
    public void setUserList(List<String> userList) { this.userList = userList; }
    public List<String> getUserList() { return userList; }
    
    public void setDataPointIdList(List<String> dataPointIdList) { 
        this.dataPointIdList = dataPointIdList;
        // Make sure the list is sorted
        Collections.sort(this.dataPointIdList);
    }
    public List<String> getDataPointIdList() { 
        return dataPointIdList; 
    }
    
    // Add a single data point id to the list
    public void addDataPointId(String dataPointId) {
        this.dataPointIdList.add(dataPointId);
        // Make sure the list is still sorted
        Collections.sort(this.dataPointIdList);
    }
    
    public void setXmlConfiguration(String xmlConfiguration) { this.xmlConfiguration = xmlConfiguration; }
    public String getXmlConfiguration() { return xmlConfiguration; }
} 