package edu.ucla.cens.mobilize.client.presenter;
import java.util.HashMap;
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
import edu.ucla.cens.mobilize.client.event.ResponseDataChangedEvent;
import edu.ucla.cens.mobilize.client.event.ResponseDataChangedEventHandler;
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
  
  // response counts are stored per-campaign because the data api query is done per-campaign 
  private Map<String, Integer> _campaignIdToPrivateResponseCountMap = new HashMap<String, Integer>();
  
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
        fetchAndShowCampaignCounts();
      }
    });
    
    eventBus.addHandler(ResponseDataChangedEvent.TYPE, new ResponseDataChangedEventHandler() {
      @Override
      public void onSurveyResponseDataChanged(ResponseDataChangedEvent event) {
        fetchAndShowResponseCounts();
      }
    });
  }
  
  @Override
  public void setView(DashboardView view) {
    this.view = view;
  }
  
  private void fetchAndShowDashboardData() {
    // initialize to 0
    view.showAuthorRoleCount(0);
    view.showParticipantRoleCount(0);
    view.showPrivateResponseCount(0);
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
        view.showParticipantRoleCount(_participantRoleCount);
        view.showAuthorRoleCount(_authorRoleCount);
      }
    });
  }
  
  private void fetchAndShowResponseCounts() {
    _campaignIdToPrivateResponseCountMap.clear();
    
    SurveyResponseReadParams params = new SurveyResponseReadParams();
    params.privacyState_opt = Privacy.PRIVATE;
    params.userList.add(userInfo.getUserName());
    
    // back end only lets you query responses for one campaign at a time
    for (String campaignId : userInfo.getCampaignIds()) {
      final String campaignIdKey = campaignId;
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
          // NOTE: response counts are stored per campaign id and summed just before 
          // display because storing a single sum and updating it could result in 
          // double counting if two update events happened too close together
          _campaignIdToPrivateResponseCountMap.put(campaignIdKey, result);
          int privateResponseCount = 0;
          for (String key : _campaignIdToPrivateResponseCountMap.keySet()) {
        	  int toAdd = _campaignIdToPrivateResponseCountMap.get(key);
        	  if (toAdd > 0) {
                privateResponseCount += toAdd;
        	  }
          }
          view.showPrivateResponseCount(privateResponseCount);
        }
      });
    }
            
  }

}

