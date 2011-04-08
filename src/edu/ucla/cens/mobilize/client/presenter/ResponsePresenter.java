package edu.ucla.cens.mobilize.client.presenter;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.view.ResponseView;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.model.UserInfo;

public class ResponsePresenter implements ResponseView.Presenter, Presenter {
  
  ResponseView view;
  EventBus eventBus;
  DataService dataService;
  
  List<String> participants = new ArrayList<String>();
  List<String> campaignIds = new ArrayList<String>();
  List<String> surveys = new ArrayList<String>();
  List<SurveyResponse> privateResponses = new ArrayList<SurveyResponse>();
  List<SurveyResponse> publicResponses = new ArrayList<SurveyResponse>();  

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
    // FIXME: add filters
    loadData();
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
    // String[] names = {"Joe Brown", "Bob Hope", "That other guy"};
    // String[] campaigns = {"Sleep Sens", "Media/Advertising", "Diet Sens"};
    // this.participants = new ArrayList<String>(Arrays.asList(names));
    
    //this.surveys = new ArrayList<String>(Arrays.asList(new String[]{"survey1", "survey2"}));
    
    this.privateResponses.clear();
    this.publicResponses.clear();
    for (String campaignId : this.campaignIds) {
      this.dataService.fetchPrivateSurveyResponses(campaignId, new AsyncCallback<List<SurveyResponse>>() {

        @Override
        public void onFailure(Throwable caught) {
          // TODO: display error message
        }

        @Override
        public void onSuccess(List<SurveyResponse> result) {
          privateResponses.addAll(result);
          // TODO: sort by date
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
    view.renderPrivate(privateResponses);
    view.renderPublic(publicResponses);
  }


  
}
