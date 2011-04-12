package edu.ucla.cens.mobilize.client.presenter;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.MainApp;
import edu.ucla.cens.mobilize.client.view.ResponseView;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.request.DataPointFilterParams;
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

  private static Logger _logger = Logger.getLogger(MainApp.class.getName());
  
  // TODO: contents of campaign filter should be updated when
  // participant name changes. contents of survey filter should
  // be changed when campaign is selected

  public ResponsePresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.campaignIds = userInfo.getParticipantCampaigns();
    this.participants.addAll(userInfo.getVisibleUsers());
    this.eventBus = eventBus;
    this.dataService = dataService;
  }
  
  @Override
  public void go(Map<String, List<String>> params) {
    // TODO: set filters, fetch and display data based on params
    
  }

  @Override
  public void setView(ResponseView view) {
    this.view = view;
    this.view.setPresenter(this);
  }
  
  @Override
  public void onFilterChange() {
    loadData(); // gets filter values during load
    updateDisplay();
  }

  @Override
  public void onShare() {
    // rpc set shared
    // if success, move to shared table
  }

  @Override
  public void onUnshare() {
    // rpc set status to unshared
    // if success, move to unshared table
  }

  @Override
  public void onDelete() {
    // note: view should have prompted user Are You Sure?
    // rpc delete
    // if success, remove from table
  }

  private void loadData() {
    this.responses.clear();
    
    // get filter params from gui
    DataPointFilterParams params = new DataPointFilterParams();
    params.participantId = this.view.getSelectedParticipant();
    params.privacyState = this.view.getSelectedPrivacyState();
    params.surveyId = this.view.getSelectedSurvey();
    
    // data point api only allows queries for one campaign at a time,
    // so iterate through campaigns and update display after loading 
    // data for each one
    for (String campaignId : this.campaignIds) {
      this.dataService.fetchSurveyResponses(campaignId,
                                            params,
                                            new AsyncCallback<List<SurveyResponse>>() {

        @Override
        public void onFailure(Throwable caught) {
          // TODO: display error message
        }

        @Override
        public void onSuccess(List<SurveyResponse> result) {
          responses.addAll(result);
          updateDisplay();
        }
      });
    }   
  }
  
  private void updateDisplay() {
    view.setParticipantList(participants);
    view.selectParticipant("Joe Brown"); // fixme: what if not there?
    view.setCampaignList(campaignIds);
    view.setSurveyList(surveys);
    Privacy privacy = view.getSelectedPrivacyState();
    switch (privacy) {
      case PUBLIC:
        view.renderPublic(responses);
        break;
      case PRIVATE: 
        view.renderPrivate(responses);
        break;
      case INVISIBLE:
        view.renderInvisible(responses);
        break;
      default:
        view.renderAll(responses);
        break;
    }
  }

  
}
