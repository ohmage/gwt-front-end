package edu.ucla.cens.mobilize.client.presenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.AwConstants;
import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.CampaignReadParams;
import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.ui.CampaignEditFormPresenter;
import edu.ucla.cens.mobilize.client.view.CampaignView;

public class CampaignPresenter implements CampaignView.Presenter, Presenter {
  
  // view used in rendering
  CampaignView view;

  // edit form has a lot of data logic, so gets its own presenter
  CampaignEditFormPresenter campaignEditPresenter;
  
  // internal data structures  
  private ArrayList<CampaignShortInfo> campaigns = new ArrayList<CampaignShortInfo>();
  private CampaignDetailedInfo campaign;
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
  public void go(Map<String, List<String>>params) {
    // hide any leftover notifications
    this.view.hideMsg();
    
    // url param overrides user permission 
    // (for testing. permissions are still enforced on server side) 
    if (params.containsKey("canedit")) {
      this.canCreate = params.get("canedit").get(0).equals("1");
      this.view.setCanCreate(this.canCreate);
    }
    
    // get subview from url params
    if (params.isEmpty()) {
      this.fetchAndShowAllCampaigns();
    } else if (params.get("v").get(0).equals("detail") && params.containsKey("id")) {
      // anything after first id is ignored
      this.fetchAndShowCampaignDetail(params.get("id").get(0));
    } else if (params.get("v").get(0).equals("author_center")) {
      this.showAuthorCenter();
    } else if (params.get("v").get(0).equals("create")) {
      this.showCampaignCreateForm();
    } else if (params.get("v").get(0).equals("edit") && params.containsKey("id")) {
      // anything after first id is ignored
      this.fetchCampaignAndShowEditForm(params.get("id").get(0));
    } else {
      // unrecognized view - do nothing
      // TODO: log?
    }
  }

  // wire up event handlers
  private void bind() {
    // on save campaign
    
    // on delete campaign

  }
  
  @Override
  public void setView(CampaignView view) {
    this.view = view;
    this.view.setPresenter(this);
    // FIXME: better way to connect subview to presenter?
    this.campaignEditPresenter.setView(view.getCampaignEditForm());
  }
  
  /************ METHODS TO LOAD DATA AND DISPLAY SUBVIEWS **************/
  
  public void fetchAndShowAllCampaigns() {
    CampaignReadParams params = new CampaignReadParams(); // empty params fetches everything
    params.outputFormat = CampaignReadParams.OutputFormat.SHORT;
    this.dataService.fetchCampaignListShort(params, new AsyncCallback<List<CampaignShortInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        // TODO
      }

      @Override
      public void onSuccess(List<CampaignShortInfo> result) {
        campaigns.clear();
        campaigns.addAll(result);
        view.setCampaignList(campaigns);
        view.showList();
      }
    });
    this.view.clearPlots();
  }

  private void fetchAndShowCampaignDetail(String campaignId) {
    this.dataService.fetchCampaignDetail(campaignId, 
        new AsyncCallback<CampaignDetailedInfo>() {

          @Override
          public void onFailure(Throwable caught) {
            _logger.fine(caught.getMessage());
            // TODO: show error to user
          }

          @Override
          public void onSuccess(CampaignDetailedInfo result) {
            boolean userCanEditCampaign = result.canEdit(userInfo.getUserName());
            view.setCampaignDetail(result, userCanEditCampaign);
            view.showDetail();
            view.clearPlots();
            view.setPlotSideBarTitle("Recent Activity");
            // todo: get plots dynamically (different for different roles)
            view.addPlot("images/histogram_small.png");
            view.addPlot("images/map_small.gif");
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
