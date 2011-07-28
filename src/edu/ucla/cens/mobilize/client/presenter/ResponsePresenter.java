package edu.ucla.cens.mobilize.client.presenter;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
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

import edu.ucla.cens.mobilize.client.AwConstants;
import edu.ucla.cens.mobilize.client.ui.ErrorDialog;
import edu.ucla.cens.mobilize.client.utils.AwErrorUtils;
import edu.ucla.cens.mobilize.client.utils.DateUtils;
import edu.ucla.cens.mobilize.client.view.ResponseView;
import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.CampaignReadParams;
import edu.ucla.cens.mobilize.client.event.UserInfoUpdatedEvent;
import edu.ucla.cens.mobilize.client.event.UserInfoUpdatedEventHandler;
import edu.ucla.cens.mobilize.client.exceptions.ApiException;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.model.UserInfo;

public class ResponsePresenter implements ResponseView.Presenter, Presenter {
  
  ResponseView view;
  EventBus eventBus;
  DataService dataService;
  
  SortedSet<String> participants = new TreeSet<String>();
  List<String> campaignIds = new ArrayList<String>();
  List<String> surveys = new ArrayList<String>();
  List<SurveyResponse> responses = new ArrayList<SurveyResponse>();
  
  UserInfo userInfo;
  List<CampaignShortInfo> campaigns;

  private static Logger _logger = Logger.getLogger(ResponsePresenter.class.getName());
  
  public ResponsePresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.eventBus = eventBus;
    this.dataService = dataService;
    this.userInfo = userInfo;
    bind();
  }

  private void bind() {
    eventBus.addHandler(UserInfoUpdatedEvent.TYPE, new UserInfoUpdatedEventHandler() {
      @Override
      public void onUserInfoChanged(UserInfoUpdatedEvent event) {
        userInfo = event.getUserInfo();
      }
    });
  }

  
  @Override
  public void go(Map<String, String> params) {
    assert view != null : "ResponsePresenter.go() called before view was set";
    
    // get params from history tokens
    String selectedSubView = params.containsKey("v") ? params.get("v") : "browse";
    String selectedParticipant = params.containsKey("uid") ? params.get("uid") : null;
    String selectedCampaign = params.containsKey("cid") ? params.get("cid") : null;
    String selectedSurvey = params.containsKey("sid") ? params.get("sid") : null;
    String selectedPrivacyString = params.containsKey("privacy") ? params.get("privacy") : null;
    boolean onlyPhotoResponses = params.containsKey("photo") ? params.get("photo").equals("true") : false;
    String startDateString = params.containsKey("from") ? params.get("from") : null;
    String endDateString = params.containsKey("to") ? params.get("to") : null;
    
    Date startDate = null;
    Date endDate = null;
    if (startDateString != null && endDateString != null) {
      startDate = DateUtils.translateFromHistoryTokenFormat(startDateString);
      endDate = DateUtils.translateFromHistoryTokenFormat(endDateString);
    } 
    
    Privacy selectedPrivacy = Privacy.fromServerString(selectedPrivacyString);
    // NOTE: If subview is browse, privacy should always be set to shared. If it's
    // not (would happen if user tried to edit the url param by hand) back end
    // should reject the request.
    
    view.setSelectedSubView(selectedSubView);
    selectedSubView = view.getSelectedSubView(); // in case string was unrecognized and changed to default
    
    // set up campaign filter
    view.setCampaignList(userInfo.getCampaigns()); 
    if (selectedCampaign != null) view.selectCampaign(selectedCampaign);
    selectedCampaign = view.getSelectedCampaign();
    
    // set up date filters
    if (startDateString != null && endDateString != null) {
      startDate = DateUtils.translateFromHistoryTokenFormat(startDateString);
      endDate = DateUtils.translateFromHistoryTokenFormat(endDateString);
    } 
    view.selectStartDate(startDate);
    view.selectEndDate(endDate);
    
    if (selectedSubView.equals("edit")) {
      view.setSectionHeaderDetail("Campaign participants may share or delete their responses " +
          "while the campaign is still running. Once a campaign has been stopped, only " +
          "supervisors may change responses.");
      fetchAndDisplayDataForEditView(selectedParticipant,
                                     selectedCampaign, 
                                     selectedSurvey, 
                                     selectedPrivacy,
                                     onlyPhotoResponses,
                                     startDate,
                                     endDate);
      // when editing, user can see both shared and private responses
      List<Privacy> privacyChoices = new ArrayList<Privacy>();
      privacyChoices.add(Privacy.PRIVATE);
      privacyChoices.add(Privacy.SHARED);
      // TODO: check to see if INVISIBLE is allowed in this installation and add it too
      view.setPrivacyStates(privacyChoices);
      view.selectPrivacyState(selectedPrivacy);
      
    } else {
      view.setSectionHeaderDetail("Shared responses can be exported and analyzed by other members of the campaign.");
      // when browsing, user can only see shared responses
      view.setPrivacyStates(Arrays.asList(Privacy.SHARED));
      view.selectPrivacyState(Privacy.SHARED);
      selectedPrivacy = Privacy.SHARED;
      fetchAndDisplayDataForBrowseView(selectedParticipant,
                                       selectedCampaign, 
                                       selectedSurvey, 
                                       selectedPrivacy,
                                       onlyPhotoResponses,
                                       startDate,
                                       endDate);
    }         

  }

  // GOTCHA: make sure the drop downs are populated the same in this function 
  // as in fetchAndFillFiltersForEditView() which is called when user makes
  // a selection in the campaign drop down
  void fetchAndDisplayDataForEditView(final String selectedParticipant, 
                                      final String selectedCampaign, 
                                      final String selectedSurvey, 
                                      final Privacy selectedPrivacy,
                                      final boolean onlyPhotoResponses,
                                      final Date startDate,
                                      final Date endDate) {
    
    // clear previous data, if any
    this.responses.clear();
    this.view.clearResponseList();
    this.participants.clear();
    this.view.clearParticipantList();
    this.surveys.clear();
    this.view.clearSurveyList();
    this.view.disableSurveyFilter(); // disabled if campaign not selected

    // campaign must be selected in edit view
    if (selectedCampaign == null || selectedCampaign.isEmpty()) return;
    
    // fetch info about selected campaign - user's role and campaign running state
    CampaignReadParams params = new CampaignReadParams();
    params.campaignUrns_opt.add(selectedCampaign);

    dataService.fetchCampaignDetail(selectedCampaign, new AsyncCallback<CampaignDetailedInfo>() {
        @Override
        public void onFailure(Throwable caught) {
          AwErrorUtils.logoutIfAuthException(caught);
          ErrorDialog.show("Could not load data for campaign: " + selectedCampaign); 
        }
  
        @Override
        public void onSuccess(CampaignDetailedInfo campaignInfo) {
          // set up survey filter with survey ids from campaign info (comes from xml config)
          view.enableSurveyFilter();
          view.setSurveyList(campaignInfo.getSurveyIds());
          if (selectedSurvey != null) view.selectSurvey(selectedSurvey);
          
          if (campaignInfo.userIsSupervisorOrAdmin()) { 
            // supervisors can edit responses from any participant for any campaign
            boolean includeAllChoice = true; 
            fetchParticipantsWithResponsesAndAddToList(selectedCampaign, 
                                                       selectedParticipant, 
                                                       includeAllChoice);
            fetchAndShowResponses(selectedParticipant, 
                                  selectedCampaign, 
                                  selectedSurvey, 
                                  selectedPrivacy,
                                  onlyPhotoResponses,
                                  startDate,
                                  endDate);
          } else if (campaignInfo.userIsParticipant()) {
            if (campaignInfo.isRunning()) {
              // participants can edit their own responses if the campaign is running
              String currentUser = userInfo.getUserName();
              participants.add(currentUser);
              view.setParticipantList(participants, false);
              view.selectParticipant(currentUser);
              fetchAndShowResponses(currentUser,
                                    selectedCampaign, 
                                    selectedSurvey, 
                                    selectedPrivacy,
                                    onlyPhotoResponses,
                                    startDate,
                                    endDate);
            } else {
              // user is participant but campaign is stopped - show an error message
              view.showInfoMessage(campaignInfo.getCampaignName() + " is stopped. " +
                  "Only supervisors can edit responses when campaign is not running.");
            }
          } else { 
            // if not supervisor or participant, set empty participant list and do not show responses.
            view.setParticipantList(participants, false);
          }
        }
    });    
  }
  
  // GOTCHA: make sure the drop downs are populated the same in this function 
  // as in fetchAndFillFiltersForBrowseView() which is called when user makes
  // a selection in the campaign drop down
  void fetchAndDisplayDataForBrowseView(final String selectedParticipant, 
                                        final String selectedCampaign, 
                                        final String selectedSurvey, 
                                        final Privacy selectedPrivacy,
                                        final boolean onlyPhotoResponses,
                                        final Date startDate,
                                        final Date endDate) {
    // clear previous data, if any
    this.responses.clear();
    this.view.clearResponseList();
    this.participants.clear();
    this.view.clearParticipantList();
    this.surveys.clear();
    this.view.clearSurveyList();
    this.view.disableSurveyFilter(); // disabled if campaign not selected
    
    // campaign must be selected in browse view
    if (selectedCampaign == null || selectedCampaign.isEmpty()) return;
    
    assert selectedPrivacy.equals(Privacy.SHARED) : "Privacy should always be shared in browse view";

    // fetch info about campaign: surveys, user's roles, campaign privacy setting
    dataService.fetchCampaignDetail(selectedCampaign, new AsyncCallback<CampaignDetailedInfo>() {
        @Override
        public void onFailure(Throwable caught) {
          AwErrorUtils.logoutIfAuthException(caught);
          ErrorDialog.show("Could not load data for campaign: " + selectedCampaign); 
        }
  
        @Override
        public void onSuccess(CampaignDetailedInfo campaignInfo) {
          if (campaignInfo.userCanSeeSharedResponses()) {
            // populate the participant list with only those users that have shared responses
            boolean includeAllChoice = true;
            fetchParticipantsWithSharedResponsesAndAddToList(selectedCampaign,
                                                             selectedParticipant,
                                                             includeAllChoice);
            // fill responses            
            fetchAndShowResponses(selectedParticipant, 
                                  selectedCampaign, 
                                  selectedSurvey, 
                                  selectedPrivacy,
                                  onlyPhotoResponses,
                                  startDate,
                                  endDate);
            // fill survey filter with survey ids from campaign info (comes from xml config)
            view.enableSurveyFilter();
            view.setSurveyList(campaignInfo.getSurveyIds()); 
            if (selectedSurvey != null) view.selectSurvey(selectedSurvey);
          } else {
            view.showInfoMessage("Responses not browsable due to campaign privacy settings.");
            view.setParticipantList(participants, false); // empty list
          }
        }
    });    
  }

  // Fetches a list of all participants in one campaign that have submitted at least
  // one response to the campaign, adds the participants to this.participants internal
  // data structure, and updates the view to match the data structure.
  private void fetchParticipantsWithResponsesAndAddToList(String campaignId, 
                                                          final String participantToSelect,
                                                          final boolean includeAllChoice) {
    if (campaignId == null) return;
    dataService.fetchParticipantsWithResponses(campaignId, new AsyncCallback<List<String>>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
      }

      @Override
      public void onSuccess(List<String> result) {
        if (result != null && !result.isEmpty()) {
          // add participants to those already in list and update display
          participants.addAll(result); // sorted participants
          view.setParticipantList(participants, includeAllChoice);
          if (participantToSelect != null) view.selectParticipant(participantToSelect);
        } else {
          // set the list anyway. if it already contained participants from a previous fetch,
          // there will be no effect. if not, the view will update display to indicate no participants
          view.setParticipantList(participants, includeAllChoice);
        }
      }
    });
  }
  
  // Fetches a list of all participants in one campaign that have submitted (and shared) 
  // at least one response, adds the participants to this.participants internal
  // data structure, and updates the view to match the data structure.
  private void fetchParticipantsWithSharedResponsesAndAddToList(String campaignId, 
                                                           final String participantToSelect,
                                                           final boolean includeAllChoice) {
    if (campaignId == null) return;
    dataService.fetchParticipantsWithResponses(campaignId, new AsyncCallback<List<String>>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
      }

      @Override
      public void onSuccess(List<String> result) {
        if (result != null && !result.isEmpty()) {
          // add participants to those already in list and update display
          participants.addAll(result); // sorted participants
          view.setParticipantList(participants, includeAllChoice);
          if (participantToSelect != null) view.selectParticipant(participantToSelect);
        } else {
          // set the list anyway. if it already contained participants from a previous fetch,
          // there will be no effect. if not, the view will update display to indicate no participants
          view.setParticipantList(participants, includeAllChoice);
        }
      }
    });
  }

  // GOTCHA: make sure the dropdowns are populated the same in this function
  // as in fetchAndDisplayDataForEditView() which is called when the page is loaded
  void fetchAndFillFiltersForEditView() {
    this.participants.clear();
    this.view.clearParticipantList();
    this.surveys.clear();
    this.view.clearSurveyList();
    final String selectedCampaign = view.getSelectedCampaign();
    dataService.fetchCampaignDetail(selectedCampaign, new AsyncCallback<CampaignDetailedInfo>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        ErrorDialog.show("There was a problem loading campaign data for " + selectedCampaign,
                         caught.getMessage());
      }

      @Override
      public void onSuccess(CampaignDetailedInfo campaignInfo) {
        view.enableSurveyFilter();
        view.setSurveyList(campaignInfo.getSurveyIds());
        if (campaignInfo.userIsSupervisorOrAdmin()) { 
          // supervisors can edit responses from any participant for any campaign
          boolean includeAllChoice = true; 
          fetchParticipantsWithResponsesAndAddToList(selectedCampaign, 
                                                     null, // selectedParticipant 
                                                     includeAllChoice);
        } else if (campaignInfo.userIsParticipant() && campaignInfo.isRunning()) {
          // participants can edit their own responses if the campaign is running
          participants.add(userInfo.getUserName());
          view.setParticipantList(participants, false);
        } else { 
          // if not a supervisor or participant, set participant list to empty
          view.setParticipantList(participants, false);
        }
      }
    });
  }
  
  // GOTCHA: make sure the dropdowns are populated the same in this function
  // as in fetchAndDisplayDataForBrowseView() which is called when the page is loaded
  void fetchAndFillFiltersForBrowseView() {
    this.participants.clear();
    this.view.clearParticipantList();
    this.surveys.clear();
    this.view.clearSurveyList();
    final String selectedCampaign = view.getSelectedCampaign();
    dataService.fetchCampaignDetail(selectedCampaign, new AsyncCallback<CampaignDetailedInfo>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        ErrorDialog.show("There was a problem loading campaign data for " + selectedCampaign,
                         caught.getMessage());
      }

      @Override
      public void onSuccess(CampaignDetailedInfo campaignInfo) {
        if (campaignInfo.userCanSeeSharedResponses()) {
          // populate the participant list with only those users that have shared responses
          boolean includeAllChoice = true;
          fetchParticipantsWithSharedResponsesAndAddToList(selectedCampaign,
                                                           null,
                                                           includeAllChoice);
          view.enableSurveyFilter();
          view.setSurveyList(campaignInfo.getSurveyIds());
        } else {
          view.setParticipantList(participants, false);
        }
      }
    });
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
    
    // set up subview menu
    view.getViewLinkEdit().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.setSelectedSubView("edit");
        view.selectPrivacyState(null); // so "All" will be selected when view changes
        fireHistoryTokenToMatchFilterValues();
      }
    });

    view.getViewLinkBrowse().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.setSelectedSubView("browse");
        fireHistoryTokenToMatchFilterValues();
      }
    });
    
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
    
    // When user selects a campaign, survey choice list is filled with names
    // of surveys from that campaign. Participant list filled with a list of 
    // participants that have responses visible in the current view.
    this.view.getCampaignFilter().addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        String selectedSubView = view.getSelectedSubView();
        if ("browse".equals(selectedSubView)) {
          fetchAndFillFiltersForBrowseView();
        } else if ("edit".equals(selectedSubView)) {
          fetchAndFillFiltersForEditView();
        } else { // default is browse
          fetchAndFillFiltersForBrowseView();
        }
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
              AwErrorUtils.logoutIfAuthException(caught);
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
              AwErrorUtils.logoutIfAuthException(caught);
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
              AwErrorUtils.logoutIfAuthException(caught);
            }

            @Override
            public void onSuccess(String result) {
              view.removeResponse(surveyKey);
            }
      });
    }
  }
  
  private void fireHistoryTokenToMatchFilterValues() {
    String selectedSubView = view.getSelectedSubView();
    String participantName = view.getSelectedParticipant();
    String campaignId = view.getSelectedCampaign();
    String surveyName = view.getSelectedSurvey();
    Privacy privacy = view.getSelectedPrivacyState();
    boolean onlyPhotoResponses = view.getHasPhotoToggleValue();
    Date startDate = view.getSelectedStartDate();
    Date endDate = view.getSelectedEndDate();
    History.newItem(HistoryTokens.responseList(selectedSubView,
                                               participantName, 
                                               campaignId, 
                                               surveyName, 
                                               privacy,
                                               onlyPhotoResponses,
                                               startDate,
                                               endDate));
  }
  
  // Fetches list of campaigns, then fetches responses for each. 
  // Args are values to filter by. Any arg set to null or "" is ignored.
  // If campaignIdOrNull is set to null, query is done against all user's campaigns.
  private void fetchAndShowResponses(final String userName,
                                     final String campaignIdOrNull, 
                                     final String surveyName,
                                     final Privacy privacy,
                                     final boolean onlyPhotoResponses,
                                     final Date startDate,
                                     final Date endDate) {
    
    // clear previous display so app will show appropriate message if all
    // the async requests return 0 responses
    this.responses.clear();
    view.clearResponseList();
    
    if (userName == null || userName.isEmpty()) return;

    // set header that will be shown if all requests return 0 responses
    String userDisplayName = userName.equals(AwConstants.specialAllValuesToken) ? "all users" : userName;
    view.setSectionHeader("Showing 0 responses from " + userDisplayName);
    
    Map<String, String> campaignsToQuery = new HashMap<String, String>();
    boolean suppressCampaignErrors; 
    if (campaignIdOrNull != null && userInfo.getCampaigns().containsKey(campaignIdOrNull)) {
      campaignsToQuery.put(campaignIdOrNull, userInfo.getCampaigns().get(campaignIdOrNull));
      suppressCampaignErrors = false;
    } else {
      campaignsToQuery.putAll(userInfo.getCampaigns());
      suppressCampaignErrors = true; 
    }
    
    for (String campaignId : campaignsToQuery.keySet()) {
      fetchAndShowResponsesForCampaign(userName,
          campaignId, 
          campaignsToQuery.get(campaignId),
          surveyName,
          privacy,
          onlyPhotoResponses,
          startDate,
          endDate,
          suppressCampaignErrors);
      
    }
  }
  
  // Fetch responses for just one campaign, add them to existing list and refresh display.
  // NOTE: This method exists because api only lets you query for responses for one 
  //   campaign at a time. When showing responses for all campaigns, the app makes
  //   multiple calls to this method.
  private void fetchAndShowResponsesForCampaign(final String participantName,
                                                final String campaignId, 
                                                final String campaignName,
                                                final String surveyName,
                                                final Privacy privacy,
                                                final boolean onlyPhotoResponses,
                                                final Date startDate,
                                                final Date endDate, 
                                                final boolean suppressCampaignErrors) {
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
            // NOTE: When fetching all responses, we don't know ahead of time which campaigns
            //   it makes sense to query, so we query all the user's campaigns and ignore 
            //   certain errors  :(
            // NOTE: if you add an error to this list you should also add it to the ErrorCode
            //   enum and make sure it's defined as an ApiException in AndWellnessDataService
            //   (search the file for "E0701" and add the new error in the same places)
            if (suppressCampaignErrors && caught.getClass().equals(ApiException.class)) {
              String errorCode = ((ApiException)caught).getErrorCode();
              // 0701 - invalid user in query
              // 0717 - analyst queried private campaign
              // 0716 - participant trying to view a stopped campaign
              if ("0701".equals(errorCode) || "0717".equals(errorCode) || "0716".equals(errorCode)) { 
                return; // silently ignore the error
              }
            }
            
            view.addErrorMessage("There was a problem loading responses for campaign: " + campaignName, 
                                 caught.getMessage());
            _logger.severe(caught.getMessage());
            AwErrorUtils.logoutIfAuthException(caught);
          }
          
          @Override
          public void onSuccess(List<SurveyResponse> result) {
            if (result == null || result.isEmpty()) return; // avoid unnecessary work 
            
            // if successful, add the result to list of responses already
            // fetched from other campaigns
            
            // fill in campaign name before displaying
            for (SurveyResponse response : result) {
              response.setCampaignName(campaignName);
            }
            
            // if photo flag is set, keep only responses with images
            if (onlyPhotoResponses) {
              for (SurveyResponse response : result) {
                if (response.hasImage()) responses.add(response);
              }
            } else { // otherwise, keep them all
              responses.addAll(result);
            }
            
            // sort by date, newest first
            Collections.sort(responses, responseDateComparator);
            String numResponses = Integer.toString(responses.size());
            String displayName = participantName.equals(AwConstants.specialAllValuesToken) ? " all users " : participantName; 
            view.setSectionHeader("Showing " + numResponses + " responses from " + displayName);
            view.renderResponses(responses);
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
