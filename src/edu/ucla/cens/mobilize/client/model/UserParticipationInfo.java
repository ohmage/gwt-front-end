package edu.ucla.cens.mobilize.client.model;

import java.util.HashMap;
import java.util.Map;

import edu.ucla.cens.mobilize.client.common.Privacy;

public class UserParticipationInfo {
  private String username;
  private Map<Privacy, Integer> responseCountsByPrivacy = new HashMap<Privacy, Integer>();
  private int totalResponseCount;
  
  public UserParticipationInfo(String username) {
    this.username = username;
    initializeResponseCounts();
  }
  
  private void initializeResponseCounts() {
    // initialize all privacy counts to 0
    for (Privacy privacy : Privacy.values()) {
      this.responseCountsByPrivacy.put(privacy, 0);
    }
    this.totalResponseCount = 0;
  }
  
  public String getUsername() {
    return username;
  }
  /*
  public void setUsername(String username) {
    this.username = username;
  }*/

  // increment total counts and count by privacy state 
  public void countResponse(SurveyResponse response) {
    Privacy responsePrivacyState = response.getPrivacyState();
    // assumes all possible privacy states are stored (see initializeResponseCounts())
    int oldCount = responseCountsByPrivacy.get(responsePrivacyState);
    this.responseCountsByPrivacy.put(responsePrivacyState, oldCount + 1);
    this.totalResponseCount++;
  }
  
  public int getTotalResponseCount() {
    return this.totalResponseCount;
  }
  
  public int getResponseCount(Privacy responsePrivacyState) {
    return this.responseCountsByPrivacy.get(responsePrivacyState);
  }
  
  
}
