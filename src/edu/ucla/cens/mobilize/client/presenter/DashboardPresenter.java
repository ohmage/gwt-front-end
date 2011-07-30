package edu.ucla.cens.mobilize.client.presenter;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.CampaignReadParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.SurveyResponseReadParams;
import edu.ucla.cens.mobilize.client.event.CampaignDataChangedEvent;
import edu.ucla.cens.mobilize.client.event.CampaignDataChangedEventHandler;
import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.view.DashboardView;

public class DashboardPresenter implements DashboardView.Presenter, Presenter {
  
  private UserInfo userInfo;
  private DataService dataService;
  private EventBus eventBus;
  
  private DashboardView view;
  
  private int _authorRoleCount = 0;
  private int _participantRoleCount = 0;
  private int _privateResponseCount = 0;

  private boolean canEdit = false;
  private boolean canUpload = false;
  
  private boolean isLoaded = false;

  private static Logger _logger = Logger.getLogger(DashboardPresenter.class.getName());
  
  public DashboardPresenter(UserInfo userInfo, 
                            DataService dataService,
                            EventBus eventBus) {
    this.userInfo = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
  }

  @Override
  public void go(Map<String, String> params) {
    assert view != null : "view must be set before displaying dashboard data";
    if (userInfo != null) {
      this.canEdit = userInfo.canCreate();
      this.canUpload = userInfo.canUpload();
      this.view.setPermissions(this.canEdit, this.canUpload);
    }
    if (!isLoaded) fetchAndShowDashboardData(); // load counts first time the dashboard is shown 
    bind(); // update counts on data change events
  }
  
  private void bind() {
    eventBus.addHandler(CampaignDataChangedEvent.TYPE, new CampaignDataChangedEventHandler() {
      @Override
      public void onCampaignDataChanged(CampaignDataChangedEvent event) {
        fetchAndShowDashboardData(); // updates counts
      }
    });
  }
  
  @Override
  public void setView(DashboardView view) {
    this.view = view;
  }
  
  private void fetchAndShowDashboardData() {
    fetchAndShowCampaignCounts();
    fetchAndShowResponseCounts();
    isLoaded = true;
  }
  
  private void fetchAndShowCampaignCounts() {
    dataService.fetchCampaignListShort(new CampaignReadParams(), 
                                       new AsyncCallback<List<CampaignShortInfo>>() {

      @Override
      public void onFailure(Throwable caught) {
        _logger.severe(caught.getMessage());
        view.hideAuthorRoleCount();
        view.hideParticipantRoleCount();
      }
  
      @Override
      public void onSuccess(List<CampaignShortInfo> result) {
        _participantRoleCount = 0;
        _authorRoleCount = 0;
        for (CampaignShortInfo campaign : result) {
          if (campaign.isRunning() && campaign.userIsParticipant()) {
            _participantRoleCount++;
          }
          if (campaign.isRunning() && campaign.userIsAuthor()) {
            _authorRoleCount++;
          }
        }
        if (_participantRoleCount > 0) view.showParticipantRoleCount(_participantRoleCount);
        else view.hideParticipantRoleCount();
        if (_authorRoleCount > 0) view.showAuthorRoleCount(_authorRoleCount);
        else view.hideAuthorRoleCount();
      }
    });
  }
  
  private void fetchAndShowResponseCounts() {
    _privateResponseCount = 0;
    
    SurveyResponseReadParams params = new SurveyResponseReadParams();
    params.privacyState_opt = Privacy.PRIVATE;
    params.userList.add(userInfo.getUserName());
    
    // back end only lets you query responses for one campaign at a time
    for (String campaignId : userInfo.getCampaignIds()) {
      params.campaignUrn = campaignId; // update campaign id and repeat query
      dataService.fetchSurveyResponseCount(userInfo.getUserName(), 
                                           campaignId, 
                                           null, // all survey ids
                                           Privacy.PRIVATE, 
                                           null, // no start date 
                                           null, // no end date
                                           new AsyncCallback<Integer>() {
        @Override
        public void onFailure(Throwable caught) {
          _logger.severe(caught.getMessage());
        }
        
        @Override
        public void onSuccess(Integer result) {
          _privateResponseCount = _privateResponseCount + result;
          if (_privateResponseCount > 0) view.showPrivateResponseCount(_privateResponseCount);
          else view.hidePrivateResponseCount();
        }
      });
    }
            
  }

}

