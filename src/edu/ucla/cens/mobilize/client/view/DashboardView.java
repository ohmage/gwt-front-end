package edu.ucla.cens.mobilize.client.view;

public interface DashboardView {

  interface Presenter {
    void setView(DashboardView view);
  } 
  
  // recent activity data setters
  void setNumUnreadSurveyResponses(int num);
  void setNumActiveParticipantCampaigns(int num);
  void setNumActiveAuthorCampaigns(int num);
    
  // quick link setters
  void setPermissions(boolean canEdit, boolean canUpload);
  
  // counts
  void showParticipantRoleCount(int count);
  void showAuthorRoleCount(int count);
  void showPrivateResponseCount(int count);
  
  void hideParticipantRoleCount();
  void hideAuthorRoleCount();
  void hidePrivateResponseCount();
  
}

