package edu.ucla.cens.mobilize.client.model;

import java.util.ArrayList;
import java.util.List;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.RunningState;
import edu.ucla.cens.mobilize.client.common.UserRole;


/**
 * A data class to store campaign specific information.
 * 
 * This class is immutable, all getter functions return a copy of mutable objects.
 * 
 * @author jhicks
 *
 */
public class CampaignInfo { 
    private String campaignId;
    private String campaignName;  // The name of this campaign
    private String description; // FIXME: add desc to translator
    private List<String> userList;  // A list of the users in enrolled in the current campaign
    private List<String> participantGroups; // list of user groups enrolled in the campaign (e.g., classes, for mobilize)
    // FIXME: just one configurationInfo per campaign now
    private List<ConfigurationInfo> configurationList;  // Holds configuration info for each version of the campaign
    //private UserRole userRole;  // Describes the user's campaign permissions
    private List<UserRole> userRoles;
    private RunningState runningState;
    private Privacy privacy;
    private String primaryAuthor;

    // deprecated
    private UserRole userRole;
    
    
    public CampaignInfo() {
        userList = new ArrayList<String>();
        participantGroups = new ArrayList<String>();
        configurationList = new ArrayList<ConfigurationInfo>();
        // FIXME: choose defaults
        userRoles = new ArrayList<UserRole>();
        userRoles.add(UserRole.PARTICIPANT);
        privacy = Privacy.PUBLIC;
        runningState = RunningState.RUNNING;
        description = "fixme: campaign description goes here";
        primaryAuthor = "fixme: primary author name goes here";
        // FIXME: real data
        participantGroups.add("CS101");
        participantGroups.add("CS210");
        participantGroups.add("STAT201");
    };
    
    public Object getKey() {
      return (Object)campaignId;
    }
    
    /**
     * Copy constructor, creates a copy of the passed in CampaignInfo.
     * 
     * @param campaignInfo The object to copy.
     */
    public CampaignInfo(CampaignInfo campaignInfo) {
      /*
        // Copy everything over, immutables don't need to be copied
        setCampaignName(campaignInfo.getCampaignName());
        setUserList(campaignInfo.getUserList());
        // FIXME: setUserGroup, runningState, privacy, primaryAuthor
        setUserRole(campaignInfo.getUserRole());
        setConfigurationList(campaignInfo.getConfigurationList());
        */
      // FIXME!!!
    }

    // getters
    public String getCampaignId() {
      return campaignId;
    }
    
    public String getCampaignName() {
      return campaignName;
    }
    
    public String getDescription() {
      return description;
    }
    
    public List<String> getUserList() {
      return userList;
    }
    
    public List<String> getParticipantGroups() {
      return participantGroups;
    }
    
    public ConfigurationInfo getConfigurationInfo() {
      // FIXME: should only be one
      return configurationList.get(0);
    }
    
    public List<UserRole> getUserRoles() {
      return userRoles;
    }
    
    // deprecated
    public UserRole getUserRole() {
      return userRoles.get(0);
    }
    public void setUserRole(UserRole role) {
      userRoles.clear();
      userRoles.add(role);
    }
    public void addUser(String userName) {
    }
    public void addConfiguration(ConfigurationInfo config) {
    }
    
    
    public RunningState getRunningState() {
      return runningState;
    }
    
    public Privacy getPrivacy() {
      return privacy;
    }
    
    public String getPrimaryAuthor() {
      return primaryAuthor;
    }
    
    public List<String> getAuthors() {
      // FIXME!!!
      return null;
    }
    
    public List<String> getSupervisor() {
      // FIXME!!!
      return null;
    }
    
    // setters
    public void setCampaignId(String campaignId) {
      // TODO
    }
    
    public void setCampaignName(String campaignName) {
      this.campaignName = campaignName;
      this.campaignId = campaignName; // FIXME: urn instead
    }
    
    public void setDescription(String description) {
      this.description = description;
    }

    public void addParticipantGroup(String participantGroup) {
      this.participantGroups.add(participantGroup);
    }
    
    public void removeParticipantGroup(String participantGroup) {
      if (this.participantGroups.contains(participantGroup)) {
        this.participantGroups.remove(participantGroup);
      }
    }
    
    public void clearParticipantGroups() {
      this.participantGroups.clear();
    }
    
    public void setConfiguration(ConfigurationInfo config) {
      this.configurationList.clear();
      this.configurationList.add(config);
    }

    // FIXME: add/remove user role
    public void setUserRoles (List<UserRole> userRoles) {
      this.userRoles = userRoles;
    }
    
    public void setRunningState (RunningState runningState) {
      this.runningState = runningState;
    }
    
    public void setPrivacy(Privacy privacy) {
      this.privacy = privacy;
    }
    
    public void setPrimaryAuthor(String primaryAuthor) {
      this.primaryAuthor = primaryAuthor;
    }
                                                      
    
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
        //return (this.campaignName.equals(campaignToCompare.campaignName));
        
        return (this.campaignId.equals(campaignToCompare.campaignId));
    }
    
    /**
     * Translates this data container to a human readable string.
     */
    public String toString() {
        StringBuffer myString = new StringBuffer();
        myString.append("CampaignInfo: ");
        myString.append("campaignName: " + getCampaignName());
        myString.append("description: " + getDescription());
        myString.append(", userList: " + getUserList());
        myString.append(", userRoles: ");
        //myString.append(", userRole: " + getUserRole());
        //myString.append(", configurationList: " + getConfigurationList());
        // FIXME: participant groups, running state, privacy, primary author
        
        return myString.toString();
    }
} 
