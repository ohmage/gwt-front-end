package edu.ucla.cens.mobilize.client.presenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.ResponseDelete;
import edu.ucla.cens.mobilize.client.dataaccess.request.CampaignReadParams;
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
      this.showAllCampaigns();
    } else if (params.get("v").get(0).equals("detail") && params.containsKey("id")) {
      // anything after first id is ignored
      this.showCampaign(params.get("id").get(0));
    } else if (params.get("v").get(0).equals("author_center")) {
      this.showAuthorCenter();
    } else if (params.get("v").get(0).equals("create")) {
      this.showCampaignCreateForm();
    } else if (params.get("v").get(0).equals("edit") && params.containsKey("id")) {
      // anything after first id is ignored
      this.showEdit(params.get("id").get(0));
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
  
  /************ METHODS TO SWITCH BETWEEN SUBVIEWS **************/
  
  public void showAllCampaigns() {
    loadAllCampaigns();
    //TODO: show overall activity?
    this.view.clearPlots();
  }
  
  private void showCampaign(String campaignId) {
    loadCampaign(campaignId); // FIXME: don't need separate function for this
    this.view.clearPlots();
    this.view.setPlotSideBarTitle("Recent Activity");
    // todo: get plots dynamically (different for different roles)
    this.view.addPlot("images/histogram_small.png");
    this.view.addPlot("images/map_small.gif");
  }
  
  private void showCampaignCreateForm() {
    this.view.setClassListToChooseFrom(userInfo.getClasses());
    // FIXME: instead, add all members of a class as potential authors
    // each time user adds a class to the campaign
    List<String> authors = new ArrayList<String>();
    authors.add("Bill"); authors.add("Frank"); authors.add("Mary"); authors.add("Alice");
    this.view.setAuthorListToChooseFrom(authors);
    this.view.showCreateForm();
  }
  
  private void showEdit(String campaignId) {
    this.dataService.fetchCampaignDetail(campaignId.toString(), 
                                         new AsyncCallback<CampaignDetailedInfo>() {
      @Override
      public void onFailure(Throwable caught) {
      // TODO Auto-generated method stub
      }
      
      @Override
      public void onSuccess(CampaignDetailedInfo result) {
        view.setClassListToChooseFrom(userInfo.getClasses());
        // FIXME: instead, get list of classes in this campaign, get all authors for those classes
        List<String> authors = new ArrayList<String>();
        authors.add("Bill"); authors.add("Frank"); authors.add("Mary"); authors.add("Alice");
        view.setAuthorListToChooseFrom(authors);
        view.setCampaignEdit(result); 
        view.showEditForm();
      }
    }); 
    this.view.setCampaignEdit(campaign);
    this.view.showEditForm();
    this.view.clearPlots();
    // TODO: set left bar links and plots
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
  /************** METHODS FOR FETCHING DATA FROM ANDWELLNESS DATASERVICE ***************/
  
  private void loadAllCampaigns() {
    CampaignReadParams params = new CampaignReadParams();
    params.outputFormat = CampaignReadParams.OutputFormat.SHORT;
    this.dataService.fetchCampaignList(params, new AsyncCallback<List<CampaignConciseInfo>>() {
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

  }
  
  private void loadCampaign(String campaignId) {
    this.dataService.fetchCampaignDetail(campaignId.toString(), 
        new AsyncCallback<CampaignDetailedInfo>() {

          @Override
          public void onFailure(Throwable caught) {
            // TODO Auto-generated method stub
          }

          @Override
          public void onSuccess(CampaignDetailedInfo result) {
            view.setCampaignDetail(result, userInfo.canEdit(result.getCampaignId()));
            view.showDetail();
          }
    });

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
  public void onCampaignDelete(String campaignId) {
    if (campaignId != null) {
      dataService.deleteCampaign(campaignId, 
          new AsyncCallback<ResponseDelete>() {
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
            public void onSuccess(ResponseDelete result) {
              // redirect to campaign list so user can verify that 
              // deleted campaign is gone and display success message
              showAllCampaigns();
              showMessage("Campaign deleted.");
            }
      });
    }
  }
}
