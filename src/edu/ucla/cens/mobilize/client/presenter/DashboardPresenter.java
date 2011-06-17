package edu.ucla.cens.mobilize.client.presenter;
import java.util.Map;

import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.view.DashboardView;

public class DashboardPresenter implements DashboardView.Presenter, Presenter {
  
  private UserInfo user;
  private DashboardView view;
  
  private int authorCount = 0;
  private int participantCount = 0;
  private int responseCount = 0;

  private boolean canEdit = false;
  private boolean canUpload = false;
  
  public DashboardPresenter(UserInfo userInfo) {
    this.user = userInfo;
  }

  @Override
  public void go(Map<String, String> params) {

    if (user != null) {
      this.canEdit = user.canCreate();
      this.canUpload = user.canUpload();
    }
      
    // url params override user settings (for testing)
    // note: back end also checks permissions on all actions
    if (params.containsKey("canedit")) {
      this.canEdit = params.get("canedit").equals("1");      
    } 
    if (params.containsKey("canupload")) {
      this.canUpload = params.get("canupload").equals("1");
    }
        
    updateDisplay();
  }
  
  @Override
  public void setView(DashboardView view) {
    this.view = view;
    this.view.setPresenter(this);
    updateDisplay();
  }
  
  public void updateDisplay() {
    this.view.setPermissions(this.canEdit, this.canUpload);
    fetchDashboardData();
    this.view.setNumActiveAuthorCampaigns(authorCount);
    this.view.setNumActiveParticipantCampaigns(participantCount);
    this.view.setNumUnreadSurveyResponses(responseCount);
  }
  
  private void fetchDashboardData() {
    authorCount = 3;
    participantCount = 5;
    responseCount = 8;
  }

}
