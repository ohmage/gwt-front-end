package edu.ucla.cens.mobilize.client.presenter;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.ui.ErrorDialog;
import edu.ucla.cens.mobilize.client.utils.DateUtils;
import edu.ucla.cens.mobilize.client.view.ResponseView;
import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.exceptions.ApiException;
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
  
  private void fetchAndFillParticipantChoices(final String participantToSelect) {
    if (userInfo.isPrivileged()) { // privileged users see all members of classes
      List<String> userClassIds = new ArrayList<String>(userInfo.getClassIds());
      dataService.fetchClassList(userClassIds, new AsyncCallback<List<ClassInfo>>() {
        @Override
        public void onFailure(Throwable caught) {
          _logger.fine("There was a problem loading participants for filter. " + 
                       "Defaulting to show only logged in user.");
          view.addErrorMessage("There was a problem loading response data.",
                               caught.getMessage());
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
          if (participantToSelect != null) {
            view.selectParticipant(participantToSelect);
          }
        }
      });
    } else {
      participants.clear();
      participants.add(userInfo.getUserName());
      view.setParticipantList(participants);
    }
  }
  
  private void fetchAndFillSurveyChoicesForSelectedCampaign(String campaignId,
                                                            final String surveyToSelectWhenDone) {
    dataService.fetchCampaignDetail(campaignId, new AsyncCallback<CampaignDetailedInfo>() {
      @Override
      public void onFailure(Throwable caught) {
        _logger.severe("Failed to load campaign filter. Error was: " + caught.getMessage());
        view.addErrorMessage("Failed to load campaign filter.", caught.getMessage());
      }

      @Override
      public void onSuccess(CampaignDetailedInfo result) {
        view.enableSurveyFilter();
        view.setSurveyList(result.getSurveyIds()); // FIXME: ok to have ids here instead of names?
        if (surveyToSelectWhenDone != null) view.selectSurvey(surveyToSelectWhenDone);
      }
    });
  }
  
  @Override
  public void go(Map<String, String> params) {
    assert view != null : "ResponsePresenter.go() called before view was set";
 
    // clear any leftover error messages
    //view.clearErrorMessages();
    
    // check history token (url) params for value that should be selected in filters
    String selectedParticipant = params.containsKey("uid") ? params.get("uid") : userInfo.getUserName();
    String selectedCampaign = params.containsKey("cid") ? params.get("cid") : null;
    String selectedSurvey = params.containsKey("sid") ? params.get("sid") : null;
    String selectedPrivacyString = params.containsKey("privacy") ? params.get("privacy") : null;
    String startDateString = params.containsKey("from") ? params.get("from") : null;
    String endDateString = params.containsKey("to") ? params.get("to") : null;
    
    // set up participant filter
    fetchAndFillParticipantChoices(selectedParticipant);
    
    // set up campaign filter
    view.setCampaignList(userInfo.getCampaigns()); 
    if (selectedCampaign != null) view.selectCampaign(selectedCampaign);
    
    // set up survey filter (contents depend on selected campaign filter)
    view.disableSurveyFilter(); // disabled if campaign not selected 
    if (selectedCampaign != null) {
      // re-enables survey filter on success
      fetchAndFillSurveyChoicesForSelectedCampaign(selectedCampaign, selectedSurvey);
    }
    
    // set up privacy filter
    List<Privacy> privacyChoices = new ArrayList<Privacy>();
    privacyChoices.add(Privacy.PRIVATE);
    privacyChoices.add(Privacy.SHARED);
    // TODO: check to see if INVISIBLE is allowed in this installation and add it too
    view.setPrivacyStates(privacyChoices);
    Privacy selectedPrivacy = Privacy.fromServerString(selectedPrivacyString);
    view.selectPrivacyState(selectedPrivacy);
    
    // set up date filters
    Date startDate = null;
    Date endDate = null;
    if (startDateString != null && endDateString != null) {
      startDate = DateUtils.translateFromHistoryTokenFormat(startDateString);
      endDate = DateUtils.translateFromHistoryTokenFormat(endDateString);
    } 
    view.selectStartDate(startDate);
    view.selectEndDate(endDate);

    // fetch responses filtered by selected values
    fetchAndShowResponses(selectedParticipant, 
                          selectedCampaign, 
                          selectedSurvey, 
                          selectedPrivacy,
                          startDate,
                          endDate);
  }

  @Override
  public void setView(ResponseView view) {
    this.view = view;
    this.view.setPresenter(this);
    setViewEventHandlers();
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
    
    this.view.getApplyFiltersButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (validateDateFilters()) {
          fireHistoryTokenToMatchFilterValues();
        }
      }
    });
    
    // when user selects a campaign, survey choice list is filled with names
    // of surveys from that campaign
    this.view.getCampaignFilter().addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        String selectedCampaign = view.getSelectedCampaign();
        fetchAndFillSurveyChoicesForSelectedCampaign(selectedCampaign, null);
      }
    });
    
    this.view.getStartDateFilter().addValueChangeHandler(new ValueChangeHandler<Date>() {
      @Override
      public void onValueChange(ValueChangeEvent<Date> event) {
        // if end date is empty or earlier than selected date, change it to selected date
        Date newStartDate = view.getSelectedStartDate();
        Date endDate = view.getSelectedEndDate();
        if (endDate != null && endDate.before(event.getValue())) { 
          view.selectEndDate(newStartDate);
        }
      }
    });
  }
  
  // returns true if dates are ok, displays error dialog if not valid
  private boolean validateDateFilters() {
    // check that dates make sense
    Date startDate = view.getSelectedStartDate();
    Date endDate = view.getSelectedEndDate();
    boolean isValid = true;
    if (startDate != null && endDate == null) {
      isValid = false;
      ErrorDialog.show("Please select an end date.");
    } else if (endDate != null && startDate == null) {
      isValid = false;
      ErrorDialog.show("Please select a start date");
    } else if (startDate != null && endDate != null && endDate.before(startDate)) {
      isValid = false;
      ErrorDialog.show("End date must be later than start date.");
    }
    return isValid;
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
        view.addErrorMessage("Response share failed.",
                             "Could not find campaign urn for survey key: " + Integer.toString(surveyKey));
        continue;
      }
      dataService.updateSurveyResponse(campaignUrn, 
           surveyKey, 
           Privacy.SHARED, 
           new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
              _logger.severe(caught.getMessage());
              view.addErrorMessage("Response share failed.", caught.getMessage());
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
        view.addErrorMessage("There was a problem updating the response(s)",
                             "Could not find campaign urn for survey key: " + 
                             Integer.toString(surveyKey));
        continue;
      }
      dataService.updateSurveyResponse(campaignUrn, 
           surveyKey, 
           Privacy.PRIVATE, 
           new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
              _logger.severe(caught.getMessage());
              view.addErrorMessage("There was a problem updating the response(s).",
                                    caught.getMessage());
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
        view.addErrorMessage("There was a problem deleting the response(s).",
                             "Could not find campaign urn for survey key: " + Integer.toString(surveyKey));
        continue;
      }
      dataService.deleteSurveyResponse(campaignUrn, 
           surveyKey, 
           new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
              _logger.severe(caught.getMessage());
              view.addErrorMessage("There was a problem deleting the response(s)", 
                                    caught.getMessage());
            }

            @Override
            public void onSuccess(String result) {
              view.removeResponse(surveyKey);
            }
      });
    }
  }
  
  private void fireHistoryTokenToMatchFilterValues() {
    String participantName = view.getSelectedParticipant();
    if (participantName == null || participantName.isEmpty()) {
      participantName = userInfo.getUserName(); // default to logged in user
    }
    String campaignId = view.getSelectedCampaign();
    String surveyName = view.getSelectedSurvey();
    Privacy privacy = view.getSelectedPrivacyState();
    Date startDate = view.getSelectedStartDate();
    Date endDate = view.getSelectedEndDate();
    History.newItem(HistoryTokens.responseList(participantName, 
                                               campaignId, 
                                               surveyName, 
                                               privacy,
                                               startDate,
                                               endDate));
  }
  
  // fetches list of campaigns, then fetches responses for each
  // args are values to filter by. any arg set to null or "" is ignored
  private void fetchAndShowResponses(final String userName,
                                     final String campaignId, 
                                     final String surveyName,
                                     final Privacy privacy,
                                     final Date startDate,
                                     final Date endDate) {
    this.responses.clear();
    
    CampaignReadParams campaignReadParams = new CampaignReadParams();
    
    // filter by campaign, if applicable
    if (campaignId != null && !campaignId.isEmpty()) {
      campaignReadParams.campaignUrns_opt.add(campaignId);
    }
    
    this.dataService.fetchCampaignListShort(campaignReadParams, new AsyncCallback<List<CampaignShortInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        view.addErrorMessage("There was a problem loading campaigns.", caught.getMessage());
      }

      @Override
      public void onSuccess(List<CampaignShortInfo> result) { 
        for (CampaignShortInfo campaignInfo : result) {
          fetchAndShowResponsesForCampaign(userName,
                                           campaignInfo.getCampaignId(), 
                                           campaignInfo.getCampaignName(),
                                           surveyName,
                                           privacy,
                                           startDate,
                                           endDate);
        }
      }
    });
  }
  
  // Fetch responses for just one campaign, add them to existing list and refresh display.
  // NOTE: This method exists because api only lets you query for responses for one 
  //   campaign at a time. When showing responses for all campaigns, the app makes
  //   multiple calls to this method.
  private void fetchAndShowResponsesForCampaign(String participantName,
                                                String campaignId, 
                                                final String campaignName,
                                                String surveyName,
                                                Privacy privacy,
                                                Date startDate,
                                                Date endDate) {
    // GOTCHA: when logged in user != selected participant, this only shows
    //   responses from campaigns that both participant and logged in user belong to
    this.dataService.fetchSurveyResponses(participantName,
        campaignId,
        surveyName,
        privacy,
        startDate,
        endDate,
        new AsyncCallback<List<SurveyResponse>>() {
          @Override
          public void onFailure(Throwable caught) {
            // WARNING: hack! We don't have a list of campaign ids for users other than the
            //   logged in user, so when participant != logged in user, we query all
            //   campaigns the logged in user belongs to and throw away any responses that
            //   return "0701-invalid user" error. 
            // NOTE: ideally there would be an api call to fetch list of campaign ids for
            //   any user instead.
            if (caught.getClass().equals(ApiException.class) && 
                ((ApiException)caught).getErrorCode().equals("0701")) {
              _logger.fine("Intentionally ignoring invalid user error on API call. (See warning in ResponsePresenter.)");
            } else {
              view.addErrorMessage("There was a problem loading responses for campaign: " + campaignName, 
                                   caught.getMessage());
              _logger.severe(caught.getMessage());
            }

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
