package edu.ucla.cens.mobilize.client.presenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.AndWellnessConstants;
import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.CampaignReadParams;
import edu.ucla.cens.mobilize.client.model.CampaignConciseInfo;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.view.CampaignView;

public class CampaignPresenter implements CampaignView.Presenter, Presenter {
  
  // view used in rendering
  CampaignView view;
  
  // internal data structures  
  private ArrayList<CampaignConciseInfo> campaigns = new ArrayList<CampaignConciseInfo>();
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
      this.fetchAndShowCampaignEdit(params.get("id").get(0));
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
  }
  
  /************ METHODS TO LOAD DATA AND DISPLAY SUBVIEWS **************/
  
  public void fetchAndShowAllCampaigns() {
    CampaignReadParams params = new CampaignReadParams(); // empty params fetches everything
    params.outputFormat = CampaignReadParams.OutputFormat.SHORT;
    this.dataService.fetchCampaignListShort(params, new AsyncCallback<List<CampaignConciseInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        // TODO
      }

      @Override
      public void onSuccess(List<CampaignConciseInfo> result) {
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
    this.view.setClassListToChooseFrom(userInfo.getClasses());
    // FIXME: instead, add all members of a class as potential authors
    // each time user adds a class to the campaign
    List<String> authors = new ArrayList<String>();
    authors.add("Bill"); authors.add("Frank"); authors.add("Mary"); authors.add("Alice");
    this.view.setAuthorListToChooseFrom(authors);
    this.view.showCreateForm(this.dataService.authToken(), 
                             AndWellnessConstants.getCampaignCreateUrl());
  }
  
  private void fetchAndShowCampaignEdit(String campaignId) {
    this.dataService.fetchCampaignDetail(campaignId.toString(), 
                                         new AsyncCallback<CampaignDetailedInfo>() {
      @Override
      public void onFailure(Throwable caught) {
      // TODO Auto-generated method stub
      }
      
      @Override
      public void onSuccess(CampaignDetailedInfo result) {
        view.clearPlots();
        view.setClassListToChooseFrom(userInfo.getClasses());
        // FIXME: instead, get list of classes in this campaign, get all authors for those classes
        List<String> authors = new ArrayList<String>();
        authors.add("Bill"); authors.add("Frank"); authors.add("Mary"); authors.add("Alice");
        view.setAuthorListToChooseFrom(authors);
        view.setCampaignEdit(result); 
        view.showEditForm(dataService.authToken(),
                          AndWellnessConstants.getCampaignUpdateUrl());
        // TODO: set left bar links and plots
      }
    }); 
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

  @Override
  public void onCampaignDelete(final String campaignId) {
    if (campaignId != null) {
      dataService.deleteCampaign(campaignId, 
          new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
              try {
                throw caught;
              } catch (Throwable e) {
                String msg = e.getMessage();
                if (msg == null || msg.isEmpty()) msg = "There was a problem completing the operation.";
                showError(msg);
              }
              // FIXME: catch specific exceptions
              
            }
            @Override
            public void onSuccess(String result) {
              // redirect to campaign list so user can verify that 
              // deleted campaign is gone and display success message
              History.newItem(HistoryTokens.campaignList());
              showMessage("Campaign " + campaignId + " has been deleted.");
            }
      });
    }
  }
}
