package edu.ucla.cens.mobilize.client.presenter;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.utils.CollectionUtils;
import edu.ucla.cens.mobilize.client.view.ResponseView;
import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.RoleCampaign;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.CampaignReadParams;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;
import edu.ucla.cens.mobilize.client.model.ClassInfo;
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
  
  public ResponsePresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.eventBus = eventBus;
    this.dataService = dataService;
    this.userInfo = userInfo;
  }
  
  private void fetchAndFillParticipantChoices() {
    if (userInfo.isPrivileged()) { // privileged users see all members of classes
      List<String> userClassIds = new ArrayList<String>(userInfo.getClassIds());
      dataService.fetchClassList(userClassIds, new AsyncCallback<List<ClassInfo>>() {
        @Override
        public void onFailure(Throwable caught) {
          _logger.fine("There was a problem loading participants for filter. " + 
                       "Defaulting to show only logged in user.");
          participants.clear();
          participants.add(userInfo.getUserName());
          view.setParticipantList(participants);
        }

        @Override
        public void onSuccess(List<ClassInfo> result) {
          List<String> participants = new ArrayList<String>();
          for (ClassInfo classInfo : result) {
            participants.addAll(classInfo.getMemberLogins());
          }
          view.setParticipantList(participants);
        }
      });
    } else {
      participants.clear();
      participants.add(userInfo.getUserName());
      view.setParticipantList(participants);
    }
  }
  
  private void fetchAndFillSurveyChoicesForSelectedCampaign(final String surveyToSelectWhenDone) {
    String campaignId = view.getSelectedCampaign();
    dataService.fetchCampaignDetail(campaignId, new AsyncCallback<CampaignDetailedInfo>() {
      @Override
      public void onFailure(Throwable caught) {
        _logger.severe("Failed to load campaign filter. Error was: " + caught.getMessage());
      }

      @Override
      public void onSuccess(CampaignDetailedInfo result) {
        view.setSurveyList(result.getSurveyIds()); // FIXME: ok to have ids here instead of names?
        if (surveyToSelectWhenDone != null) view.selectSurvey(surveyToSelectWhenDone);
      }
    });
  }
  
  @Override
  public void go(Map<String, List<String>> params) {
    assert view != null : "ResponsePresenter.go() called before view was set";
    fetchAndFillParticipantChoices();
    if (params.containsKey("uid")) view.selectParticipant(params.get("uid").get(0)); 
    view.setCampaignChoices(userInfo.getCampaigns()); 
    if (params.containsKey("cid")) view.selectCampaign(params.get("cid").get(0));
    String surveyToSelect = params.containsKey("sid") ? params.get("sid").get(0) : null; 
    this.fetchAndFillSurveyChoicesForSelectedCampaign(surveyToSelect);
    fetchAndShowFilteredResponses(); 
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
    String surveyName = this.view.getSelectedSurvey();
    Privacy privacy = this.view.getSelectedPrivacyState();
    History.newItem(HistoryTokens.responseList(userName, campaignId, surveyName, privacy));
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
    
    this.view.getCampaignFilter().addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        fetchAndFillSurveyChoicesForSelectedCampaign(null); 
      }
    });
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
      view.showConfirmDelete(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          deleteSelectedResponses();          
        }
      });
    }
  };
  
  // GOTCHA: assumes the response you care about is in this.responses
  private String getCampaignUrnForSurveyKey(int surveyKey) {
    String campaignUrn = null;
    for (SurveyResponse surveyResponse : this.responses) {
      if (surveyResponse.getResponseKey() == surveyKey) {
        campaignUrn = surveyResponse.getCampaignId();
        break;
      }
    }
    return campaignUrn;
  }
  
  // Loops through responses, sending a data request to update each one. 
  // Responses in the display are updated one at a time when their request returns.
  private void shareSelectedResponses() {
    List<String> responseKeys = this.view.getSelectedSurveyResponseKeys();
    for (String responseKey : responseKeys) {
      final int surveyKey = Integer.parseInt(responseKey);
      String campaignUrn = getCampaignUrnForSurveyKey(surveyKey);
      if (campaignUrn == null) {
        _logger.severe("Could not find campaign urn for survey key: " + 
                        Integer.toString(surveyKey) + 
                        ". Response will not be shared.");
        view.showErrorMessage("One or more responses may not have been shared.");
        continue;
      }
      dataService.updateSurveyResponse(campaignUrn, 
           surveyKey, 
           Privacy.SHARED, 
           new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
              _logger.severe(caught.getMessage());
              view.showErrorMessage("One or more responses may not have been shared.");
            }

            @Override
            public void onSuccess(String result) {
              view.markShared(surveyKey);
            }
      });
    }
  }

  // Loops through responses, sending a data request to update each one. 
  // Responses in the display are updated one at a time when their request returns.
  private void makeSelectedResponsesPrivate() {
    List<String> responseKeys = this.view.getSelectedSurveyResponseKeys();
    for (String responseKey : responseKeys) {
      final int surveyKey = Integer.parseInt(responseKey);
      String campaignUrn = getCampaignUrnForSurveyKey(surveyKey);
      if (campaignUrn == null) {
        _logger.severe("Could not find campaign urn for survey key: " + 
                        Integer.toString(surveyKey) + 
                        ". Response will not be marked private.");
        view.showErrorMessage("One or more responses may not have been updated.");
        continue;
      }
      dataService.updateSurveyResponse(campaignUrn, 
           surveyKey, 
           Privacy.SHARED, 
           new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
              _logger.severe(caught.getMessage());
              view.showErrorMessage("One or more responses may not have been updated.");
            }

            @Override
            public void onSuccess(String result) {
              view.markPrivate(surveyKey);
            }
      });
    }
  }
  
  // Loops through responses, sending a data request to delete each one. 
  // Responses are removed from the display one at a time as their request returns.
  private void deleteSelectedResponses() {
    List<String> responseKeys = this.view.getSelectedSurveyResponseKeys();
    for (String responseKey : responseKeys) {
      final int surveyKey = Integer.parseInt(responseKey);
      String campaignUrn = getCampaignUrnForSurveyKey(surveyKey);
      if (campaignUrn == null) {
        _logger.severe("Could not find campaign urn for survey key: " + 
                        Integer.toString(surveyKey) + 
                        ". Response will not be deleted.");
        view.showErrorMessage("One or more responses may not have been deleted.");
        continue;
      }
      dataService.deleteSurveyResponse(campaignUrn, 
           surveyKey, 
           new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
              _logger.severe(caught.getMessage());
              view.showErrorMessage("One or more responses may not have been deleted.");
            }

            @Override
            public void onSuccess(String result) {
              view.removeResponse(surveyKey);
            }
      });
    }
  }
  
  private void fetchAndShowFilteredResponses() {
    String participantName = view.getSelectedParticipant();
    String campaignId = view.getSelectedCampaign();
    String surveyName = view.getSelectedSurvey();
    Privacy privacy = view.getSelectedPrivacyState();
    fetchAndShowResponses(participantName, campaignId, surveyName, privacy);
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
    campaignReadParams.userRole_opt = RoleCampaign.PARTICIPANT;
    
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
  
  // Fetch responses for just one campaign, add them to existing list and refresh display.
  // NOTE: This method exists because api only lets you query for responses for one 
  //   campaign at a time. When showing responses for all campaigns, the app makes
  //   multiple calls to this method.
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
            view.showErrorMessage("There was a problem loading responses for campaign: " + campaignName);
            _logger.severe(caught.getMessage());
          }
          
          @Override
          public void onSuccess(List<SurveyResponse> result) {
            // fill in campaign name before displaying
            for (SurveyResponse response : result) {
              response.setCampaignName(campaignName);
            }
            // add to responses already fetched from other campaigns
            responses.addAll(result);
            // sort by date, newest first
            Collections.sort(responses, responseDateComparator); 
            view.renderAll(responses);
          }
    });

  }

  // for sorting
  private Comparator<SurveyResponse> responseDateComparator = new Comparator<SurveyResponse>() {
    @Override
    public int compare(SurveyResponse arg0, SurveyResponse arg1) {
      return arg1.getResponseDate().compareTo(arg0.getResponseDate()); // recent dates first
    }
  };
  
}
