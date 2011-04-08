package edu.ucla.cens.mobilize.client.model;

import java.util.HashMap;
import java.util.Map;

import edu.ucla.cens.mobilize.client.common.UserRole;

public class UserStats {
  public Map<UserRole, Integer> activeCampaignCount; // by role
  public int numUnreadResponses = 0;
  
  public UserStats() {
    activeCampaignCount = new HashMap<UserRole, Integer>();
    activeCampaignCount.put(UserRole.ADMIN, 0);
    activeCampaignCount.put(UserRole.ANALYST, 0);
    activeCampaignCount.put(UserRole.AUTHOR, 0);
    activeCampaignCount.put(UserRole.PARTICIPANT, 0);
    activeCampaignCount.put(UserRole.SUPERVISOR, 0);
  }
  
  public void incrementActiveCount(UserRole role) {
    activeCampaignCount.put(role, activeCampaignCount.get(role) + 1);
  }
}
