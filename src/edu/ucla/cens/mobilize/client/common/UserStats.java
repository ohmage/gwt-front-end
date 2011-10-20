package edu.ucla.cens.mobilize.client.common;

import java.util.HashMap;
import java.util.Map;


public class UserStats {
  public Map<RoleCampaign, Integer> activeCampaignCount; // by role
  public int numUnreadResponses = 0;
  
  public UserStats() {
    activeCampaignCount = new HashMap<RoleCampaign, Integer>();
    activeCampaignCount.put(RoleCampaign.ADMIN, 0);
    activeCampaignCount.put(RoleCampaign.ANALYST, 0);
    activeCampaignCount.put(RoleCampaign.AUTHOR, 0);
    activeCampaignCount.put(RoleCampaign.PARTICIPANT, 0);
    activeCampaignCount.put(RoleCampaign.SUPERVISOR, 0);
  }
  
  public void incrementActiveCount(RoleCampaign role) {
    activeCampaignCount.put(role, activeCampaignCount.get(role) + 1);
  }
}
