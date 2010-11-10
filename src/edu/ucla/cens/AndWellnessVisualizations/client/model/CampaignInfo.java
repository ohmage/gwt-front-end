package edu.ucla.cens.AndWellnessVisualizations.client.model;

import java.util.ArrayList;
import java.util.List;


/**
 * A data class to store campaign specific information.
 * 
 * This class is immutable, all getter functions return a copy of mutable objects.
 * 
 * @author jhicks
 *
 */
public class CampaignInfo { 
    private String campaignName;  // The name of this campaign
    private List<String> userList;  // A list of the users in enrolled in the current campaign
    private List<ConfigurationInfo> configurationList;  // Holds configuration info for each version of the campaign
    private UserRole userRole;  // Describes the user's campaign permissions
    
    // Data fields necessary to describe a campaign configuration
    public enum UserRole {
        PARTICIPANT, ADMIN, RESEARCHER;
    };
    
    
    public CampaignInfo() {
        userList = new ArrayList<String>();
        configurationList = new ArrayList<ConfigurationInfo>();
    };
    
    /**
     * Copy constructor, creates a copy of the passed in CampaignInfo.
     * 
     * @param campaignInfo The object to copy.
     */
    public CampaignInfo(CampaignInfo campaignInfo) {
        // Copy everything over, immutables don't need to be copied
        setCampaignName(campaignInfo.getCampaignName());
        setUserList(campaignInfo.getUserList());
        setUserRole(campaignInfo.getUserRole());
        setConfigurationList(campaignInfo.getConfigurationList());
    }

    // Setters and getters
    public void setCampaignName(String campaignName) { this.campaignName = campaignName; }
    public String getCampaignName() { return campaignName; }
    
    public void addUser(String userName) { userList.add(userName); }
    public void setUserList(List<String> userList) { this.userList = userList; }
    public List<String> getUserList() { return new ArrayList<String>(userList); }
    
    public void addConfiguration(ConfigurationInfo survey) { configurationList.add(survey); }
    public void setConfigurationList(List<ConfigurationInfo> surveyList) { this.configurationList = surveyList; }
    public List<ConfigurationInfo> getConfigurationList() {
        List<ConfigurationInfo> copy = new ArrayList<ConfigurationInfo>();
        for (ConfigurationInfo configuration:configurationList) {
            copy.add(new ConfigurationInfo(configuration));
        }
        return copy;
    }
    
    public void setUserRole(UserRole userRole) { this.userRole = userRole; }
    public UserRole getUserRole() { return userRole; }
    
    
    
    /**
     * Overrides the equals method.  Compares one CampaignInfo against another.
     */
    public boolean equals(Object toCompare) {
        if (this == toCompare)
            return true;
        
        if ( !(toCompare instanceof CampaignInfo)) {
            return false;
        }
        
        CampaignInfo campaignToCompare = (CampaignInfo) toCompare;
        
        // Now do the actual comparison, just compare campaign name for now
        return (this.campaignName.equals(campaignToCompare.campaignName));
    }
    
    /**
     * Translates this data container to a human readable string.
     */
    public String toString() {
        StringBuffer myString = new StringBuffer();
        myString.append("CampaignInfo: ");
        myString.append("campaignName: " + getCampaignName());
        myString.append(", userList: " + getUserList());
        myString.append(", userRole: " + getUserRole());
        myString.append(", configurationList: " + getConfigurationList());
        
        return myString.toString();
    }
} 