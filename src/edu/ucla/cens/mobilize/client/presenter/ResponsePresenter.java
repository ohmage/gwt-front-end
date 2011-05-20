package edu.ucla.cens.mobilize.client.presenter;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.MainApp;
import edu.ucla.cens.mobilize.client.utils.CollectionUtils;
import edu.ucla.cens.mobilize.client.view.ResponseView;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.UserRole;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.CampaignReadParams;
import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.model.UserInfo;

public class ResponsePresenter implements ResponseView.Presenter, Presenter {
  
  ResponseView view;
  EventBus eventBus;
  DataService dataService;
  
  List<String> participants = new ArrayList<String>();
  List<String> campaignIds = new ArrayList<String>();
  List<String> surveys = new ArrayList<String>();
  List<SurveyResponse> responses = new ArrayList<SurveyResponse>();
  
  UserInfo userInfo;

  private static Logger _logger = Logger.getLogger(ResponsePresenter.class.getName());
  
  // TODO: contents of campaign filter should be updated when
  // participant name changes. contents of survey filter should
  // be changed when campaign is selected

  public ResponsePresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    //this.campaignIds = userInfo.getParticipantCampaigns();
    this.participants.addAll(userInfo.getVisibleUsers());
    this.eventBus = eventBus;
    this.dataService = dataService;
    this.userInfo = userInfo;
  }
  
  @Override
  public void go(Map<String, List<String>> params) {
    // TODO: set filters, fetch and display data based on params
    fetchAndShowResponses(); 
  }

  @Override
  public void setView(ResponseView view) {
    this.view = view;
    this.view.setPresenter(this);
    setViewEventHandlers();
  }
  
  @Override
  public void onFilterChange() {
    String userName = this.view.getSelectedParticipant();
    String campaignId = this.view.getSelectedCampaign();
    String surveyId = this.view.getSelectedSurvey();
    Privacy privacy = this.view.getSelectedPrivacyState();
    fetchAndShowResponses(userName, campaignId, surveyId, privacy);
  }
  
  // view must be set before calling this
  private void setViewEventHandlers() {
    assert view != null : "view must be set before calling setViewEventHandlers";
    // clicking a share buttons shares all selected responses
    for (HasClickHandlers shareButton : this.view.getShareButtons()) {
      shareButton.addClickHandler(shareClickHandler);
    }
    // clicking a make private button makes all selected responses private
    for (HasClickHandlers makePrivateButton : this.view.getMakePrivateButtons()) {
      makePrivateButton.addClickHandler(makePrivateClickHandler);
    }
    // clicking delete button deletes all selected responses
    for (HasClickHandlers deleteButton : this.view.getDeleteButtons()) {
      deleteButton.addClickHandler(deleteClickHandler);
    }
  }
  
  private ClickHandler shareClickHandler = new ClickHandler() {
    @Override
    public void onClick(ClickEvent event) {
      shareSelectedResponses();
    }
  };
  
  private ClickHandler makePrivateClickHandler = new ClickHandler() {
    @Override
    public void onClick(ClickEvent event) {
      makeSelectedResponsesPrivate();
    }
  };

  private ClickHandler deleteClickHandler = new ClickHandler() {
    @Override
    public void onClick(ClickEvent event) {
      Window.alert("TODO: prompt for delete confirmation");
      deleteSelectedResponses();
    }
  };
  
  
  private void shareSelectedResponses() {
    List<String> responseKeys = this.view.getSelectedSurveyResponseKeys();
    Window.alert("would have shared: " + CollectionUtils.join(responseKeys, ","));
  }
  
  private void makeSelectedResponsesPrivate() {
    List<String> responseKeys = this.view.getSelectedSurveyResponseKeys();
    Window.alert("would have made private: " + CollectionUtils.join(responseKeys, ","));
  }
  
  private void deleteSelectedResponses() {
    List<String> responseKeys = this.view.getSelectedSurveyResponseKeys();
    Window.alert("would have deleted: " + CollectionUtils.join(responseKeys, ","));
  }
  
  // call with defaults (logged in user and other filters set to show all)
  private void fetchAndShowResponses() { 
    fetchAndShowResponses(this.userInfo.getUserName(), null, null, null);
  }
  
  // fetches list of campaigns, then fetches responses for each
  // args are values to filter by. any arg set to null or "" is ignored
  private void fetchAndShowResponses(final String userName,
                                     final String campaignId, 
                                     final String surveyName,
                                     final Privacy privacy) {
    this.responses.clear();
    
    CampaignReadParams campaignReadParams = new CampaignReadParams();
    campaignReadParams.userRole_opt = UserRole.PARTICIPANT;
    
    // filter by campaign, if applicable
    if (campaignId != null && !campaignId.isEmpty()) {
      campaignReadParams.campaignUrns_opt.add(campaignId);
    }
    
    this.dataService.fetchCampaignListShort(campaignReadParams, new AsyncCallback<List<CampaignShortInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        // TODO Auto-generated method stub
      }

      @Override
      public void onSuccess(List<CampaignShortInfo> result) {
        
        for (CampaignShortInfo campaignInfo : result) {
          fetchAndShowResponsesForCampaign(userName,
                                           campaignInfo.getCampaignId(), 
                                           campaignInfo.getCampaignName(),
                                           surveyName,
                                           privacy);
        }
      }
    });
  }
  
  // fetch responses for just one campaign, add them to existing list and refresh display
  // TODO: sort after adding
  private void fetchAndShowResponsesForCampaign(String userName,
                                                String campaignId, 
                                                final String campaignName,
                                                String surveyName,
                                                Privacy privacy) {
    this.dataService.fetchSurveyResponses(userName,
        campaignId,
        surveyName,
        privacy,
        new AsyncCallback<List<SurveyResponse>>() {
          @Override
          public void onFailure(Throwable caught) {
            // TODO: display error message
          }
          
          @Override
          public void onSuccess(List<SurveyResponse> result) {
            // fill in campaign name before displaying
            for (SurveyResponse response : result) {
              response.setCampaignName(campaignName);
            }
            // add to responses already fetched from other campaigns
            responses.addAll(result);
            // TODO: sort
            updateDisplay();
          }
    });

  }
  
  private void updateDisplay() {
    view.setParticipantList(participants);
    view.selectParticipant("Joe Brown"); // fixme: what if not there?
    view.setCampaignList(campaignIds);
    view.setSurveyList(surveys);
    Privacy privacy = view.getSelectedPrivacyState();
    switch (privacy) {
      case SHARED:
        view.renderShared(this.responses);
        break;
      case PRIVATE: 
        view.renderPrivate(this.responses);
        break;
      case INVISIBLE:
        view.renderInvisible(this.responses);
        break;
      default:
        view.renderAll(this.responses);
        break;
    }
  }

  
}
