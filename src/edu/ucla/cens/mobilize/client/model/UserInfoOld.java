package edu.ucla.cens.mobilize.client.model;

import java.util.ArrayList;
import java.util.HashMap;
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
public class UserInfoOld implements Comparable<UserInfoOld> {
    private String userName;  // The name of the currently logged in user
    private HashMap<String, CampaignInfo> campaigns;
    //private UserPermissions permissions;
    
    
    public UserInfoOld() {
      campaigns = new HashMap<String, CampaignInfo>();
    };
    
    // Setters/getters, getters always copy to keep immutability
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserName() { return userName; }
    
    public void addCampaign(CampaignInfo info) {
      addCampaign(info.getCampaignId(), info);
    }
    
    public void addCampaign(String cid, CampaignInfo info) {
      campaigns.put(cid, info);
    }
    
    // useful for populating drop downs, for instance
    public List<CampaignInfo> getCampaignList() {
      return new ArrayList<CampaignInfo>(campaigns.values()); // copy
    }
    
    public HashMap<String, CampaignInfo> getCampaigns() {
      return new HashMap<String, CampaignInfo>(campaigns); // copy
    }
    
    // returns CampaignInfo or null
    public CampaignInfo getCampaign(String cid) {
      return campaigns.containsKey(cid) ? campaigns.get(cid) : null;
    }
    
    // Allows this model to be sorted by Collections.sort() (be userName only)
    public int compareTo(UserInfoOld arg0) {
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
        myString.append(", campaignList: " + getCampaignList());
        
        return myString.toString();
    }
}
