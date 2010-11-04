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
    private String campaignVersion;  // The version of this campaign
    private List<String> userList;  // A list of the users in enrolled in the current campaign
    private List<SurveyInfo> surveyList;  // A list of the configured surveys in this campaign
    private String xmlConfiguration;  // The XML used to configure this campaign
    private UserRole userRole;  // Describes the user's campaign permissions
    
    // Data fields necessary to describe a campaign configuration
    public enum UserRole {
        PARTICIPANT, ADMIN, RESEARCHER;
    };
    
    
    public CampaignInfo() {
        userList = new ArrayList<String>();
        surveyList = new ArrayList<SurveyInfo>();
    };
    
    /**
     * Copy constructor, creates a copy of the passed in CampaignInfo.
     * 
     * @param campaignInfo The object to copy.
     */
    public CampaignInfo(CampaignInfo campaignInfo) {
        CampaignInfo copy = new CampaignInfo();
        
        // Copy everything over, immutables don't need to be copied
        copy.setCampaignName(campaignInfo.getCampaignName());
        copy.setCampaignVersion(campaignInfo.getCampaignVersion());
        copy.setUserList(campaignInfo.getUserList());
        copy.setSurveyList(campaignInfo.getSurveyList());
        copy.setXmlConfiguration(campaignInfo.getXmlConfiguration());
        copy.setUserRole(campaignInfo.getUserRole());
    }

    // Setters and getters
    public void setCampaignName(String campaignName) { this.campaignName = campaignName; }
    public String getCampaignName() { return campaignName; }
    
    public void setCampaignVersion(String campaignVersion) { this.campaignVersion = campaignVersion; }
    public String getCampaignVersion() { return campaignVersion; }
    
    public void addUser(String userName) { userList.add(userName); }
    public void setUserList(List<String> userList) { this.userList = userList; }
    public List<String> getUserList() { return new ArrayList<String>(userList); }
    
    public void addSurvey(SurveyInfo survey) { surveyList.add(survey); }
    public void setSurveyList(List<SurveyInfo> surveyList) { this.surveyList = surveyList; }
    public List<SurveyInfo> getSurveyList() {
        List<SurveyInfo> copy = new ArrayList<SurveyInfo>();
        for (SurveyInfo survey:surveyList) {
            copy.add(new SurveyInfo(survey));
        }
        return copy;
    }
    
    public void setXmlConfiguration(String xmlConfiguration) { this.xmlConfiguration = xmlConfiguration; }
    public String getXmlConfiguration() { return xmlConfiguration; }
    
    public void setUserRole(UserRole userRole) { this.userRole = userRole; }
    public UserRole getUserRole() { return userRole; }
    
    /**
     * Translates this data container to a human readable string.
     */
    public String toString() {
        StringBuffer myString = new StringBuffer();
        myString.append("CampaignInfo: ");
        myString.append("campaignName: " + getCampaignName());
        myString.append(", campaignVersion: " + getCampaignVersion());
        myString.append(", userList: " + getUserList());
        myString.append(", userRole: " + getUserRole());
        myString.append(", surveyList: " + getSurveyList());
        
        return myString.toString();
    }
} 