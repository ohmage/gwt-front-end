package edu.ucla.cens.AndWellnessVisualizations.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * UserInfo stores all information about the currently logged in user including
 * their name, role, campaign membership, the currently selected campaign, and
 * the currently selected date range.  This object is usually stored in the top
 * level application controller, and is also maintained by the top level app
 * controller.
 * 
 * This object is immutable, that is all calls to get return copies of data.
 * 
 * @author jhicks
 *
 */
public class UserInfo implements Comparable<UserInfo> {
    private String userName;  // The name of the currently logged in user
    private List<CampaignInfo> campaignList;
    private int selectedCampaign = -1;
    private List<String> specialIdList;
    private Date selectedStartDate;
    private Date selectedEndDate;
 
    
    public UserInfo() {
        campaignList = new ArrayList<CampaignInfo>();
        specialIdList = new ArrayList<String>();
    };
 
    
    // Setters/getters, getters always copy to keep immutability
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserName() { return userName; }
    
    public void addCampaign(CampaignInfo campaignInfo) { campaignList.add(campaignInfo); }
    public List<CampaignInfo> getCampaignList() { 
        List<CampaignInfo> copy = new ArrayList<CampaignInfo>();
        for (CampaignInfo object:campaignList) {
            copy.add(new CampaignInfo(object));
        }
        return copy;
    }
    
    public void setSelectedCampaign(int selectedCampaign) { this.selectedCampaign = selectedCampaign; }
    public int getSelectedCampaign() { return selectedCampaign; }
    
    public void addSpecialIdList(String specialId) { specialIdList.add(specialId); }
    public List<String> getSpecialIdList() { return new ArrayList<String>(specialIdList); }
    
    public void setSelectedStartDate(Date selectedStartDate) { this.selectedStartDate = selectedStartDate; }
    public Date getSelectedStartDate() { return (Date)selectedStartDate.clone(); }
    
    public void setSelectedEndDate(Date selectedEndDate) { this.selectedEndDate = selectedEndDate; }
    public Date getSelectedEndDate() { return (Date)selectedEndDate.clone(); }
    

    /**
     * Returns the ID of the selected campaign.
     * 
     * @return The ascii name of the currently selected campaign.  Returns null
     * if there if no campaign selected.
     */
    public String getSelectedCampaignId() {
        int selectedCampaign = getSelectedCampaign();
        String selectedCampaignId;
        
        try {
            selectedCampaignId = campaignList.get(selectedCampaign).getCampaignName();
        }
        catch (IndexOutOfBoundsException err) {
            // There is no selected campaign, return null.
            return null;
        }
        
        return selectedCampaignId;
    }
    
    /**
     * Returns the user list for the passed campaign.
     * 
     * @param campaign The campaign for which to check.
     * @return The list of users from the campaign, null if no users found.
     */
    public List<String> getUserListForCampaign(CampaignInfo campaign) {
        List<String> userList = null;
        
        int campaignIndex = campaignList.indexOf(campaign);
        if (campaignIndex > -1) {
            userList = campaignList.get(campaignIndex).getUserList();
        }
        
        return userList;
    }
    
    // Allows this model to be sorted by Collections.sort() (be userName only)
    public int compareTo(UserInfo arg0) {
        return this.userName.compareTo(arg0.userName);
    }
    
    /**
     * Translates this data container into a string for output.
     * 
     * @return The object represented as a string.
     */
    public String toString() {
        StringBuffer myString = new StringBuffer();
        
        myString.append("UserInfo: ");
        myString.append(", userName: " + getUserName());
        myString.append(", selectedCampaign: " + getSelectedCampaign());
        myString.append(", specialIdList: " + getSpecialIdList());
        myString.append(", campaignList: " + getCampaignList());
        
        return myString.toString();
    }
}
