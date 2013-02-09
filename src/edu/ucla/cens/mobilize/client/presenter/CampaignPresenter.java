package edu.ucla.cens.mobilize.client.presenter;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.RoleCampaign;
import edu.ucla.cens.mobilize.client.common.RunningState;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.CampaignReadParams;
import edu.ucla.cens.mobilize.client.event.UserInfoUpdatedEvent;
import edu.ucla.cens.mobilize.client.event.UserInfoUpdatedEventHandler;
import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.ui.CampaignEditFormPresenter;
import edu.ucla.cens.mobilize.client.utils.AwErrorUtils;
import edu.ucla.cens.mobilize.client.utils.DateUtils;
import edu.ucla.cens.mobilize.client.view.CampaignView;

public class CampaignPresenter implements Presenter {
  
  // view used in rendering
  CampaignView view;

  // presenters for subviews
  CampaignEditFormPresenter campaignEditPresenter;
  
  // internal data structures  
  private EventBus eventBus;
  private DataService dataService;
  private UserInfo userInfo;
  
  // permissions
  boolean canCreate = false;
  
  // logging
  private static Logger _logger = Logger.getLogger(CampaignPresenter.class.getName());

  // note: userinfo is created once in main app controller and passed to presenters
  public CampaignPresenter(UserInfo userInfo,
                           DataService dataService, 
                           EventBus eventBus) {
    this.userInfo = userInfo;
    this.eventBus = eventBus;
    this.dataService = dataService;
    this.canCreate = this.userInfo.canCreate();
    
    bind();
    
    this.campaignEditPresenter = new CampaignEditFormPresenter(userInfo, dataService, eventBus);
    
  }
  
  private void bind() {
    eventBus.addHandler(UserInfoUpdatedEvent.TYPE, new UserInfoUpdatedEventHandler() {
      @Override
      public void onUserInfoChanged(UserInfoUpdatedEvent event) {
        // makes sure userInfo is up to date b/c class list view uses it
        userInfo = event.getUserInfo();
      }
    });
  }
  
  @Override
  public void go(Map<String, String>params) {

    // hide any leftover notifications
    this.view.hideMsg();
    
    // show or hide the campaign creation button
    this.view.setCanCreate(canCreate);
    
    // display any new notifications
    if (userInfo.hasInfoMessage()) this.view.showMsg(userInfo.getInfoMessage());
    if (userInfo.hasErrorMessage()) this.view.showError(userInfo.getErrorMessage(), null);
    userInfo.clearMessages();
    
    // get subview from url params
    if (params.isEmpty() || params.get("v").equals("list")) {
      fetchAndShowCampaignsFilteredByHistoryTokenParams(params);
    } else if (params.get("v").equals("detail") && params.containsKey("id")) {
      // anything after first id is ignored
      fetchAndShowCampaignDetail(params.get("id"));
    } else if (params.get("v").equals("create")) {
      showCampaignCreateForm();
    } else if (params.get("v").equals("edit") && params.containsKey("id")) {
      // anything after first id is ignored
      fetchCampaignAndShowEditForm(params.get("id"));
    } else {
      // unrecognized view - show campaign list by default
      History.newItem(HistoryTokens.campaignList());
    }
  }
  
  public void setView(CampaignView view) {
    this.view = view;
    this.campaignEditPresenter.setView(view.getCampaignEditForm());
    this.view.getCampaignList().setDataService(this.dataService);
    this.view.getCampaignDetail().setDataService(this.dataService);
  }
  
  /************ METHODS TO LOAD DATA AND DISPLAY SUBVIEWS **************/
  
  public void fetchAndShowAllCampaigns() {
    CampaignReadParams params = new CampaignReadParams(); // empty params fetches everything
    params.outputFormat = CampaignReadParams.OutputFormat.SHORT;
    this.dataService.fetchCampaignListShort(params, new AsyncCallback<List<CampaignShortInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        _logger.severe(caught.getMessage());
        view.showList();
        view.showError("There was a problem loading the campaigns:", caught);
        AwErrorUtils.logoutIfAuthException(caught);
      }

      @Override
      public void onSuccess(List<CampaignShortInfo> result) {
        view.setCampaignList(result);
        view.showList();
      }
    });
  }

  // converts history token params to typesafe filter params before fetching
  private void fetchAndShowCampaignsFilteredByHistoryTokenParams(Map<String, String> params) {
    RunningState runningState = null;
    RoleCampaign role = null;
    Date fromDate = null;
    Date toDate = null;
    try {
      // keys must match those in HistoryTokens.campaignList()
      if (params.containsKey("state")) runningState = RunningState.fromServerString(params.get("state")); 
      if (params.containsKey("role")) role = RoleCampaign.fromServerString(params.get("role"));
      if (params.containsKey("from")) fromDate = DateUtils.translateFromHistoryTokenFormat(params.get("from"));
      if (params.containsKey("to")) toDate = DateUtils.translateFromHistoryTokenFormat(params.get("to"));
    } catch (Exception e) {
      // could happen if, e.g., user enters url manually and gives invalid date string
      _logger.warning(e.getMessage());
    }
    
    // now fetch and show
    fetchAndShowCampaigns(runningState, role, fromDate, toDate);
  }
  
  
  public void fetchAndShowCampaigns(final RunningState state,
                                    final RoleCampaign role,
                                    final Date fromDate,
                                    final Date toDate) {
    CampaignReadParams params = new CampaignReadParams();
    params.outputFormat = CampaignReadParams.OutputFormat.SHORT;
    if (state != null) params.runningState_opt = state;
    if (role != null) params.userRole_opt = role;
    if (fromDate != null) params.startDate_opt = fromDate;
    if (toDate != null) params.endDate_opt = toDate;
    this.dataService.fetchCampaignListShort(params, new AsyncCallback<List<CampaignShortInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        _logger.severe(caught.getMessage());
        view.showList();
        view.showError("There was a problem loading the campaigns.", caught);
        AwErrorUtils.logoutIfAuthException(caught);
      }

      @Override
      public void onSuccess(List<CampaignShortInfo> result) {
        Collections.sort(result, campaignNameComparator);
        // display result
        view.setCampaignList(result);
        // set dropdown filters to match values used in query
        view.setCampaignListFilters(state, role, fromDate, toDate);
        // make list subview visible
        view.showList();
      }
    });    
  }

  private void fetchAndShowCampaignDetail(String campaignId) {
    this.dataService.fetchCampaignDetail(campaignId, 
        new AsyncCallback<CampaignDetailedInfo>() {

          @Override
          public void onFailure(Throwable caught) {
            _logger.fine(caught.getMessage());
            History.newItem(HistoryTokens.campaignList()); // redirect to list of campaigns
            view.showError("There was a problem loading the campaign.", caught);
            AwErrorUtils.logoutIfAuthException(caught);
          }

          @Override
          public void onSuccess(CampaignDetailedInfo result) {
            view.setCampaignDetail(result);
            view.showDetail();
          }
    });
  }
  
  private void showCampaignCreateForm() {
    this.campaignEditPresenter.initFormForCreate();
    this.view.showEditForm();
  }
  
  private void fetchCampaignAndShowEditForm(String campaignId) {
    this.campaignEditPresenter.fetchCampaignAndInitFormForEdit(campaignId);
    this.view.showEditForm();
  }
  
  Comparator<CampaignShortInfo> campaignNameComparator = new Comparator<CampaignShortInfo>() {
    @Override
    public int compare(CampaignShortInfo arg0, CampaignShortInfo arg1) {
      return arg0.getCampaignName().compareTo(arg1.getCampaignName());
    }
  };

}
