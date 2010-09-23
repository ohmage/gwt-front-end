package edu.ucla.cens.AndWellnessVisualizations.client.model;

/**
 * Singleton class to store information about the current AndWellness campaign.
 * 
 * @author jhicks
 *
 */
public class CampaignInfo {
    private static CampaignInfo campaignInfo = null;
    
    // Data fields
    
    
    // Singleton, make the ctor private
    private CampaignInfo() {};
    
    // Grab an instance of the class
    public static CampaignInfo getInstance() {
        if (campaignInfo == null) {
            campaignInfo = new CampaignInfo();
        }
        
        return campaignInfo;
    }
    
    public String getCampaignId() {
        // TODO Auto-generated method stub
        return null;
    }
    
    

}
