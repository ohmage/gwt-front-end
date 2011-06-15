package edu.ucla.cens.mobilize.client.presenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.common.RoleCampaign;
import edu.ucla.cens.mobilize.client.common.RunningState;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.CampaignReadParams;
import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.ui.CampaignEditFormPresenter;
import edu.ucla.cens.mobilize.client.utils.DateUtils;
import edu.ucla.cens.mobilize.client.view.CampaignView;

public class CampaignPresenter implements CampaignView.Presenter, Presenter {
  
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
    
    this.campaignEditPresenter = new CampaignEditFormPresenter(userInfo, dataService, eventBus);
  }
  
  @Override
  public void go(Map<String, String>params) {
    // hide any leftover notifications
    this.view.hideMsg();
    
    // display any new notifications
    if (userInfo.hasInfoMessage()) this.view.showMsg(userInfo.getInfoMessage());
    if (userInfo.hasErrorMessage()) this.view.showError(userInfo.getErrorMessage());
    userInfo.clearMessages();
    
    // url param overrides user permission 
    // (for testing. permissions are still enforced on server side) 
    if (params.containsKey("canedit")) {
      this.canCreate = params.get("canedit").equals("1");
      this.view.setCanCreate(this.canCreate);
    }
    
    // get subview from url params
    if (params.isEmpty() || params.get("v").equals("list")) {
      fetchAndShowCampaignsFilteredByHistoryTokenParams(params);
    } else if (params.get("v").equals("detail") && params.containsKey("id")) {
      // anything after first id is ignored
      fetchAndShowCampaignDetail(params.get("id"));
    } else if (params.get("v").equals("author_center")) {
      showAuthorCenter();
    } else if (params.get("v").equals("create")) {
      showCampaignCreateForm();
    } else if (params.get("v").equals("edit") && params.containsKey("id")) {
      // anything after first id is ignored
      fetchCampaignAndShowEditForm(params.get("id"));
    } else {
      // unrecognized view - do nothing
      // TODO: log?
    }
  }
  
  @Override
  public void setView(CampaignView view) {
    this.view = view;
    this.view.setPresenter(this);
    this.campaignEditPresenter.setView(view.getCampaignEditForm());
    this.view.getCampaignList().setDataService(this.dataService);
    this.view.getCampaignDetail().setDataService(this.dataService);
  }
  
  /************ METHODS TO LOAD DATA AND DISPLAY SUBVIEWS **************/
  
  public void fetchAndShowAllCampaigns() {
    CampaignReadParams params = new CampaignReadParams(); // empty params fetches everything
    params.outputFormat = CampaignReadParams.OutputFormat.SHORT;
    this.view.clearPlots();
    this.dataService.fetchCampaignListShort(params, new AsyncCallback<List<CampaignShortInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        _logger.severe(caught.getMessage());
        view.showList();
        view.showError("There was a problem loading the campaigns.");
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
    // FIXME: instead of try/catch, add helper to enums that will return null for bad vals
    RunningState runningState = null;
    RoleCampaign role = null;
    Date fromDate = null;
    Date toDate = null;
    try {
      // keys must match those in HistoryTokens.campaignList()
      if (params.containsKey("state")) runningState = RunningState.valueOf(params.get("state").toUpperCase());
      if (params.containsKey("role")) role = RoleCampaign.valueOf(params.get("role").toUpperCase());
      if (params.containsKey("from")) fromDate = DateUtils.translateFromHistoryTokenFormat(params.get("from"));
      if (params.containsKey("to")) toDate = DateUtils.translateFromHistoryTokenFormat(params.get("to"));
    } catch (Exception e) {
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
    this.view.clearPlots();
    this.dataService.fetchCampaignListShort(params, new AsyncCallback<List<CampaignShortInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        _logger.severe(caught.getMessage());
        view.showList();
        view.showError("There was a problem loading the campaigns.");
      }

      @Override
      public void onSuccess(List<CampaignShortInfo> result) {
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
    view.clearPlots();
    view.setPlotSideBarTitle("Recent Activity");
    this.dataService.fetchCampaignDetail(campaignId, 
        new AsyncCallback<CampaignDetailedInfo>() {

          @Override
          public void onFailure(Throwable caught) {
            _logger.fine(caught.getMessage());
            view.showError("There was a problem loading the campaign.");
          }

          @Override
          public void onSuccess(CampaignDetailedInfo result) {
            view.setCampaignDetail(result);
            // TODO: get plots dynamically (different for different roles)
            view.addPlot("images/histogram_small.png");
            view.addPlot("images/map_small.gif");
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
  
  private void showAuthorCenter() {
    // TODO
  }

  /************** METHODS FOR DISPLAY SUCCESS/ERROR MESSAGES TO USER ***************/
  private void showError(String msg) {
    this.view.showError(msg);
  }
  
  private void showMessage(String msg) {
    this.view.showMsg(msg);
  }

  /************** METHODS FOR HANDLING VIEW EVENTS ***********/
  
  @Override
  public void onCampaignSelected(String campaignId) {
    History.newItem("campaigns?v=detail&id=" + campaignId);
  }
  

  @Override
  public void onFilterChange() {
    // TODO Auto-generated method stub
    // fetch data that matches new filters
    // update display
  }

  @Override
  public void onCampaignCreate() {
    History.newItem("campaigns?v=create");
  }
}
