package edu.ucla.cens.mobilize.client.presenter;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Date;
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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RangeChangeEvent.Handler;

import edu.ucla.cens.mobilize.client.AwConstants;
import edu.ucla.cens.mobilize.client.ui.ErrorDialog;
import edu.ucla.cens.mobilize.client.utils.AwErrorUtils;
import edu.ucla.cens.mobilize.client.utils.DateUtils;
import edu.ucla.cens.mobilize.client.view.ResponseView;
import edu.ucla.cens.mobilize.client.view.ResponseView.Subview;
import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.CampaignReadParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.SurveyResponseReadParams;
import edu.ucla.cens.mobilize.client.event.ResponseDataChangedEvent;
import edu.ucla.cens.mobilize.client.event.ResponseDataChangedEventHandler;
import edu.ucla.cens.mobilize.client.event.UserInfoUpdatedEvent;
import edu.ucla.cens.mobilize.client.event.UserInfoUpdatedEventHandler;
import edu.ucla.cens.mobilize.client.model.AppConfig;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.model.SurveyResponseData;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.model.UserParticipationInfo;

public class ResponsePresenter implements ResponseView.Presenter, Presenter {
  
  ResponseView view;
  EventBus eventBus;
  DataService dataService;
  
  SortedSet<String> participants = new TreeSet<String>();
  List<String> campaignIds = new ArrayList<String>();
  List<String> surveys = new ArrayList<String>();
  SurveyResponseData surveyResponseData = new SurveyResponseData();
  List<UserParticipationInfo> participationInfo;
  
  UserInfo userInfo;
  String campaignName;
  CampaignDetailedInfo selectedCampaignInfo;

  // when forceRefetch is true, every history token change causes data to be refetched
  private boolean forceReload = false; // (useful for troubleshooting)

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
    
    eventBus.addHandler(ResponseDataChangedEvent.TYPE, new ResponseDataChangedEventHandler() {
      @Override
      public void onSurveyResponseDataChanged(ResponseDataChangedEvent event) {
        // GOTCHA: drop down contents might be wrong if different responses are shared now
        fetchAndShowResponses(surveyResponseData.getParams(), 
                              view.getVisibleRangeStart(),
                              view.getSelectedPageSize());
      }
    });
  }

  
  @Override
  public void go(Map<String, String> params) {
    assert view != null : "ResponsePresenter.go() called before view was set";
    
    boolean refireTokenWithDefaultValues = false;
    
    // remove leftover error messages, if any
    view.clearErrorMessages();
    
    // set default section header
    view.setSectionHeader("Please make a selection from the filters on the left.");
    view.setSectionHeaderDetail("");

    // some app installations do not allow editing responses
    view.setEditMenuItemVisible(AppConfig.responsePrivacyIsEditable());
    
    // get params from history tokens
    String selectedSubViewString = params.containsKey("v") ? params.get("v") : null;
    Subview selectedSubview = Subview.fromHistoryTokenString(selectedSubViewString);
    String selectedParticipant = params.containsKey("uid") ? params.get("uid") : null;
    String selectedCampaign = params.containsKey("cid") ? params.get("cid") : null;
    String selectedSurvey = params.containsKey("sid") ? params.get("sid") : null;
    String selectedPrivacyString = params.containsKey("privacy") ? params.get("privacy") : null;
    boolean onlyPhotoResponses = params.containsKey("photo") ? params.get("photo").equals("true") : false;
    String startDateString = params.containsKey("from") ? params.get("from") : null;
    String endDateString = params.containsKey("to") ? params.get("to") : null;
    
    // Get start index from params if available, or refire history token which will
    //   set start index to what's shown in the pager
    int startIndex = -1;
    if (params.containsKey("start")) {
      try {
        startIndex = Integer.parseInt(params.get("start"));
      } catch (NumberFormatException e) {
        refireTokenWithDefaultValues = true;
      } 
    } else {
      refireTokenWithDefaultValues = true;
    }
    
    // Get page size from params if available, or refire history token, which will
    //   set page size to what's shown in the view
    int pageSize = 10;
    if (params.containsKey("page_size")) {
      try {
        pageSize = Integer.parseInt(params.get("page_size"));
      } catch (NumberFormatException e) {
        refireTokenWithDefaultValues = true;
      }
    } else {
      refireTokenWithDefaultValues = true;
    }
    
    // if true, app will always refetch data instead of attempting to use already loaded data
    forceReload = params.containsKey("force_reload");
    
    // if no username given, default to all
    if (selectedParticipant == null || selectedParticipant.isEmpty()) {
      selectedParticipant = AwConstants.specialAllValuesToken;
    }
    
    Date startDate = null;
    Date endDate = null;
    if (startDateString != null && endDateString != null) {
      startDate = DateUtils.translateFromHistoryTokenFormat(startDateString);
      endDate = DateUtils.translateFromHistoryTokenFormat(endDateString);
    } 
    
    Privacy selectedPrivacy = Privacy.fromServerString(selectedPrivacyString);
    
    view.setSelectedSubview(selectedSubview);
    selectedSubview = view.getSelectedSubview(); // in case string was unrecognized and changed to default
    
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
    
    view.setSelectedPageSize(pageSize);
    pageSize = view.getSelectedPageSize(); // view might have rounded it
    view.setVisibleRangeStart(startIndex);
    
    // if any missing values were set to defaults above, refire history token to make them explicit
    if (refireTokenWithDefaultValues) {
      this.fireHistoryTokenToMatchFilterValues();
      return;
    }
    
    switch (selectedSubview) {
    case BROWSE:
      view.setSectionHeaderDetail("Private responses are visible only to the participant and supervisors. " +
      "Shared responses are visible to anyone in the campaign.");
      view.setSectionHeaderDetail("Shared responses can be viewed by anyone in the campaign. " +
        "Private responses are visible only to the responder and campaign supervisors.");
      view.showAllFilters();
      fetchAndDisplayDataForBrowseView(selectedParticipant,
                                       selectedCampaign, 
                                       selectedSurvey, 
                                       selectedPrivacy,
                                       onlyPhotoResponses,
                                       startDate,
                                       endDate,
                                       startIndex,
                                       pageSize);
      break;
    case EDIT:
      // if editing isn't allowed for this installation, redirect to browse
      if (!AppConfig.responsePrivacyIsEditable()) { // user edited url by hand?
        view.setSelectedSubview(Subview.BROWSE);
        fireHistoryTokenToMatchFilterValues();
        break;
      }
      // editing is allowed. set up edit view.
      view.setSectionHeaderDetail("Campaign participants may share or delete their responses " +
          "while the campaign is still running. Once a campaign has been stopped, only " +
          "supervisors may change responses.");
      view.showAllFilters();
      fetchAndDisplayDataForEditView(selectedParticipant,
                                     selectedCampaign, 
                                     selectedSurvey, 
                                     selectedPrivacy,
                                     onlyPhotoResponses,
                                     startDate,
                                     endDate,
                                     startIndex,
                                     pageSize);
      break;
    default: // unrecognized view: redirect to browse
      view.setSelectedSubview(Subview.BROWSE);
      fireHistoryTokenToMatchFilterValues();
      break;
    }

  }

  // GOTCHA: make sure the drop downs are populated the same in this function 
  // as in fetchAndFillFiltersForEditView() which is called when user makes
  // a selection in the campaign drop down
  void fetchAndDisplayDataForEditView(final String selectedParticipant, 
                                      final String selectedCampaignId, 
                                      final String selectedSurvey, 
                                      final Privacy selectedPrivacy,
                                      final boolean onlyPhotoResponses,
                                      final Date startDate,
                                      final Date endDate,
                                      final int startIndex,
                                      final int pageSize) {    
    // Clear previous data, if any. Note: do not clear surveyResponseData b/c it can be re-used 
    this.view.clearResponseList();
    this.participants.clear();
    this.view.clearParticipantList();
    this.surveys.clear();
    this.view.clearSurveyList();
    this.view.disableSurveyFilter(); // disabled if campaign not selected
    if (view.getSelectedCampaign() != surveyResponseData.getCampaignUrn())
    	surveyResponseData.clear();
    
    // campaign must be selected in edit view
    if (selectedCampaignId == null || selectedCampaignId.isEmpty()) return;
    
    // fetch info about selected campaign - user's role and campaign running state
    CampaignReadParams params = new CampaignReadParams();
    params.campaignUrns_opt.add(selectedCampaignId);

    dataService.fetchCampaignDetail(selectedCampaignId, new AsyncCallback<CampaignDetailedInfo>() {
        @Override
        public void onFailure(Throwable caught) {
          campaignName = null;
          AwErrorUtils.logoutIfAuthException(caught);
          ErrorDialog.show("Could not load data for campaign: " + selectedCampaignId); 
        }
  
        @Override
        public void onSuccess(CampaignDetailedInfo campaignInfo) {
          // save campaign data
          selectedCampaignInfo = campaignInfo;
          // set up survey filter with survey ids from campaign info (comes from xml config)
          view.enableSurveyFilter();
          view.setSurveyList(campaignInfo.getSurveyIds());
          fillPrivacyFilter(selectedPrivacy, campaignInfo.userIsSupervisorOrAdmin());
          if (selectedSurvey != null) view.selectSurvey(selectedSurvey);
          
          if (campaignInfo.userIsSupervisorOrAdmin()) { 
            // supervisors can edit responses from any participant for any campaign
            boolean includeAllChoice = true; 
            fetchParticipantsWithResponsesAndAddToList(selectedCampaignId, 
                                                       selectedParticipant, 
                                                       includeAllChoice);
            fetchAndShowResponses(selectedParticipant, 
                                  selectedCampaignId, 
                                  campaignInfo.getCampaignName(),
                                  selectedSurvey, 
                                  selectedPrivacy,
                                  onlyPhotoResponses,
                                  startDate,
                                  endDate,
                                  startIndex,
                                  pageSize);
            
          } else if (campaignInfo.userIsParticipant()) {
            if (campaignInfo.isRunning()) {
              // participants can edit their own responses if the campaign is running
              String currentUser = userInfo.getUserName();
              participants.add(currentUser);
              view.setParticipantList(participants, false);
              view.selectParticipant(currentUser);
              fetchAndShowResponses(currentUser,
                                    selectedCampaignId, 
                                    campaignInfo.getCampaignName(),
                                    selectedSurvey, 
                                    selectedPrivacy,
                                    onlyPhotoResponses,
                                    startDate,
                                    endDate,
                                    startIndex,
                                    pageSize);
            } else {
              // user is participant but campaign is stopped - show an error message
              view.setSectionHeader(campaignInfo.getCampaignName() + " is stopped. " +
                  "Only supervisors can edit responses when campaign is not running.");
              view.setSectionHeaderDetail("");
              view.setParticipantList(participants, false);
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
                                        final Date endDate,
                                        final int startIndex,
                                        final int pageSize) {    
    // Clear previous data, if any. Note: do not clear surveyResponseData b/c it can be re-used
    this.view.clearResponseList();
    this.participants.clear();
    this.view.clearParticipantList();
    this.surveys.clear();
    this.view.clearSurveyList();
    this.view.disableSurveyFilter(); // disabled if campaign not selected
    this.view.disableShowResponsesButton();
    if (view.getSelectedCampaign() != surveyResponseData.getCampaignUrn())
    	surveyResponseData.clear();
    
    // campaign must be selected in browse view
    if (selectedCampaign == null || selectedCampaign.isEmpty()) return;
    
    // fetch info about campaign: surveys, user's roles, campaign privacy setting
    dataService.fetchCampaignDetail(selectedCampaign, new AsyncCallback<CampaignDetailedInfo>() {
        @Override
        public void onFailure(Throwable caught) {
          AwErrorUtils.logoutIfAuthException(caught);
          ErrorDialog.show("Could not load data for campaign: " + selectedCampaign); 
        }
  
        @Override
        public void onSuccess(CampaignDetailedInfo campaignInfo) {
          selectedCampaignInfo = campaignInfo;
          boolean includeAllChoice = true;
          fetchParticipantsWithResponsesAndAddToList(selectedCampaign,
                                                     selectedParticipant,
                                                     includeAllChoice);
          // fill responses            
          fetchAndShowResponses(selectedParticipant, 
                                selectedCampaign,
                                campaignInfo.getCampaignName(),
                                selectedSurvey, 
                                selectedPrivacy,
                                onlyPhotoResponses,
                                startDate,
                                endDate,
                                startIndex,
                                pageSize);
          // fill survey filter with survey ids from campaign info (comes from xml config)
          view.enableSurveyFilter();
          view.setSurveyList(campaignInfo.getSurveyIds()); 
          if (selectedSurvey != null) view.selectSurvey(selectedSurvey);
          // privacy states may be different depending on user's role in campaign
          fillPrivacyFilter(selectedPrivacy, campaignInfo.userIsSupervisorOrAdmin());
        }
    });    
  }
  
  private void fillPrivacyFilter(Privacy selectedPrivacy, boolean userIsSuper) {
    if (userIsSuper) {
      view.setPrivacyStates(AppConfig.getResponsePrivacyStates());
    } else {
      view.setPrivacyStates(Arrays.asList(Privacy.PRIVATE, Privacy.SHARED));
    }
    view.selectPrivacyState(selectedPrivacy);
  }

  // Fetches a list of all participants in one campaign that have submitted at least
  // one response to the campaign, adds the participants to this.participants internal
  // data structure, and updates the view to match the data structure.
  private void fetchParticipantsWithResponsesAndAddToList(String campaignId, 
                                                          final String participantToSelect,
                                                          final boolean includeAllChoice) {
    if (campaignId == null) return;
    dataService.fetchParticipantsWithResponses(campaignId, false, new AsyncCallback<List<String>>() {
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
        view.enableShowResponsesButton();
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
    final Privacy selectedPrivacy = view.getSelectedPrivacyState();
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
        fillPrivacyFilter(selectedPrivacy, campaignInfo.userIsSupervisorOrAdmin());
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
    final Privacy selectedPrivacy = view.getSelectedPrivacyState();
    dataService.fetchCampaignDetail(selectedCampaign, new AsyncCallback<CampaignDetailedInfo>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        ErrorDialog.show("There was a problem loading campaign data for " + selectedCampaign,
                         caught.getMessage());
      }

      @Override
      public void onSuccess(CampaignDetailedInfo campaignInfo) {
          // populate the participant list with only those users that have shared responses
          boolean includeAllChoice = true;
          fetchParticipantsWithResponsesAndAddToList(selectedCampaign,
                                                     null,
                                                     includeAllChoice);
          view.enableSurveyFilter();
          view.setSurveyList(campaignInfo.getSurveyIds());
          fillPrivacyFilter(selectedPrivacy, campaignInfo.userIsSupervisorOrAdmin());
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
        view.setSelectedSubview(Subview.EDIT);
        view.setVisibleRangeStart(0); 
        fireHistoryTokenToMatchFilterValues();
      }
    });

    view.getViewLinkBrowse().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.setSelectedSubview(Subview.BROWSE);
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
          view.setVisibleRangeStart(0); 
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
        Subview selectedSubView = view.getSelectedSubview();
        if (Subview.BROWSE.equals(selectedSubView)) {
          fetchAndFillFiltersForBrowseView();
        } else if (Subview.EDIT.equals(selectedSubView)) {
          fetchAndFillFiltersForEditView();
        } else { // default is browse
          fetchAndFillFiltersForBrowseView();
        }
      }
    });
    
    this.view.getSurveyFilter().addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        // "only photo responses" filter is only valid when "All" is selected in survey dropdown
        view.setPhotoResponsesCheckBoxEnabled("".equals(view.getSelectedSurvey()));
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
   
    // pager next/prev fires history token to reload data. (allows bookmarking a page)
    this.view.addRangeChangeHandler(new RangeChangeEvent.Handler() {
      @Override
      public void onRangeChange(RangeChangeEvent event) {
        // save history token so page can be bookmarked, but don't fire it b/c that would reload everything
        History.newItem(getHistoryTokenToMatchFilterValues(), false);
        // update display, fetching more data only if needed
        fetchAndShowResponses(surveyResponseData.getParams(), 
                              view.getVisibleRangeStart(),
                              view.getSelectedPageSize());
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
  
  private SurveyResponse getSurveyResponse(String surveyKey) {
    return this.surveyResponseData.getSurveyResponse(surveyKey);
  }
  
  // helper method
  private String getCampaignUrnForSurveyKey(String surveyKey) {
    SurveyResponse response = getSurveyResponse(surveyKey);
    return response != null ? response.getCampaignId() : null;
  }
  
  // Loops through responses, sending a data request to update each one. 
  // Responses in the display are updated one at a time when their request returns.
  private void shareSelectedResponses() {
    List<String> responseKeys = this.view.getSelectedSurveyResponseKeys();
    // Update each response, updating display when the data call returns successfully
    for (String responseKey : responseKeys) {
      final String surveyKey = responseKey;
      // get info about the selected response
      SurveyResponse response = getSurveyResponse(surveyKey);
      // if no info was loaded, skip it
      if (response == null) {
        view.addErrorMessage("There was a problem updating the response(s)",
            "Could not find campaign urn for survey key: " + 
            surveyKey);
        continue;
      }
      // if response is already shared, skip it
      if (response.getPrivacyState().equals(Privacy.SHARED)) continue;
      // otherwise, send data request 
      String campaignUrn = response.getCampaignId();
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
              // Update display. If privacy filter is set to show only Private responses, the newly
              //   shared response is removed. Otherwise, it's just marked as shared.
              if (view.getSelectedPrivacyState().equals(Privacy.PRIVATE)) {
                // just remove for now. ResponseDataChangedEvent will cause page to refresh
                surveyResponseData.removeResponse(surveyKey);
                updateResponseDisplay(surveyResponseData, view.getVisibleRangeStart(), view.getSelectedPageSize());
              } else { 
                surveyResponseData.setPrivacyState(surveyKey, Privacy.SHARED);
                view.markShared(surveyKey); 
              }
            }
      });
    }
    // wait a while for requests to complete, then report change
    Timer t = new Timer() {
      @Override
      public void run() {
        eventBus.fireEvent(new ResponseDataChangedEvent());
      }
    };
    t.schedule(2000); // 2 seconds
  }

  // Loops through responses, sending a data request to update each one. 
  // Responses in the display are updated one at a time when their request returns.
  private void makeSelectedResponsesPrivate() {
    List<String> responseKeys = this.view.getSelectedSurveyResponseKeys();
    for (String responseKey : responseKeys) {
      final String surveyKey = responseKey;
      // get info about the selected response
      SurveyResponse response = getSurveyResponse(surveyKey);
      // if no info was loaded, skip it
      if (response == null) {
        view.addErrorMessage("There was a problem updating the response(s)",
            "Could not find campaign urn for survey key: " + 
            surveyKey);
        continue;
      }
      // if response is already private, skip it
      if (response.getPrivacyState().equals(Privacy.PRIVATE)) continue;
      // otherwise, send data request 
      String campaignUrn = response.getCampaignId();
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
              // Update display. If privacy filter is set to show only Shared responses, the newly
              //   Private response is removed. Otherwise, it's just marked as Private.
              if (view.getSelectedPrivacyState().equals(Privacy.SHARED)) {
                // just remove for now. ResponseDataChangedEvent will cause page to refresh
                surveyResponseData.removeResponse(surveyKey);
                updateResponseDisplay(surveyResponseData, view.getVisibleRangeStart(), view.getSelectedPageSize());
              } else { 
                surveyResponseData.setPrivacyState(surveyKey, Privacy.PRIVATE);
                view.markPrivate(surveyKey); 
              }
            }
      });
    }
    // wait a while for requests to complete, then report change
    Timer t = new Timer() {
      @Override
      public void run() {
        eventBus.fireEvent(new ResponseDataChangedEvent());
      }
    };
    t.schedule(2000); // 2 seconds
  }
  
  // Loops through responses, sending a data request to delete each one. 
  // Responses are removed from the display one at a time as their request returns.
  private void deleteSelectedResponses() {
    List<String> responseKeyStrings = this.view.getSelectedSurveyResponseKeys();
    for (String responseKeyString : responseKeyStrings) {
      final String responseKey = responseKeyString;
      String campaignUrn = getCampaignUrnForSurveyKey(responseKey);
      
      if (campaignUrn == null) {
        _logger.severe("Could not find campaign urn for survey key: " + 
                        responseKeyString + 
                        ". Response will not be deleted.");
        view.addErrorMessage("There was a problem deleting the response(s).",
                             "Could not find campaign urn for survey key: " + responseKeyString);
        continue;
      }
      dataService.deleteSurveyResponse(campaignUrn, responseKey, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
              _logger.severe(caught.getMessage());
              view.addErrorMessage("There was a problem deleting the response(s)", 
                                    caught.getMessage());
              AwErrorUtils.logoutIfAuthException(caught);
            }

            @Override
            public void onSuccess(String result) {
              // just remove for now. ResponseDataChangedEvent will cause page to refresh
              surveyResponseData.removeResponse(responseKey);
              updateResponseDisplay(surveyResponseData, view.getVisibleRangeStart(), view.getSelectedPageSize());
            }
      });
    }
    // wait a while for requests to complete, then report change
    Timer t = new Timer() {
      @Override
      public void run() {
        eventBus.fireEvent(new ResponseDataChangedEvent());
      }
    };
    t.schedule(2000); // 2 seconds
  }
  
  private String getHistoryTokenToMatchFilterValues() {
    Subview selectedSubview = view.getSelectedSubview();
    String selectedSubviewString = selectedSubview != null ? selectedSubview.toHistoryTokenString() : null;
    String participantName = view.getSelectedParticipant();
    String campaignId = view.getSelectedCampaign();
    String surveyName = view.getSelectedSurvey();
    Privacy privacy = view.getSelectedPrivacyState();
    boolean onlyPhotoResponses = view.getOnlyPhotoResponsesFlag();
    Date startDate = view.getSelectedStartDate();
    Date endDate = view.getSelectedEndDate();
    int startIndex = view.getVisibleRangeStart();
    int pageSize = view.getSelectedPageSize();
    return HistoryTokens.responseList(selectedSubviewString,
                                      participantName, 
                                      campaignId, 
                                      surveyName, 
                                      privacy,
                                      onlyPhotoResponses,
                                      startDate,
                                      endDate,
                                      startIndex,
                                      pageSize);
  }
  
  private void fireHistoryTokenToMatchFilterValues() {
    History.newItem(getHistoryTokenToMatchFilterValues());
  }
  
  // Renders responses from internal data struct. Assumes the requested data is already 
  //   loaded. If there's a possibility it might not be, use fetchAndShowResponses instead.
  private void updateResponseDisplay(SurveyResponseData responseData, int startIndex, int pageSize) {
    // push response data into display
    view.renderResponses(responseData.getResponses(startIndex, pageSize));
    
    // make sure filter selections match loaded data. (An example of when this might not be true
    //   is when user changes filters but clicks next on the pager instead of "show responses")
    SurveyResponseReadParams params = responseData.getParams();
    String participantName = (params.userList != null && !params.userList.isEmpty()) ?
                              params.userList.get(0) : null;
    view.selectCampaign(params.campaignUrn);
    view.selectEndDate(params.endDate_opt);
    view.selectParticipant(participantName);
    view.selectPrivacyState(params.privacyState_opt);
    view.selectStartDate(params.startDate_opt);
    
    boolean onlyPhotoResponses = params.promptType_opt != null && params.promptType_opt.equals("photo");
    view.setPhotoFilter(onlyPhotoResponses);
    
    // if "only photos" flag is set, then survey id list was chosen programmatically
    //   and "All" should be selected in the survey filter
    view.selectSurvey(onlyPhotoResponses ? null : params.getSurvey());
    
    // photo checkbox is disabled if a specific survey is selected
    String selectedSurvey = view.getSelectedSurvey();
    view.setPhotoResponsesCheckBoxEnabled(selectedSurvey == null || selectedSurvey.isEmpty());
    
    // update counts display
    view.showResponseCountInSectionHeader(participantName, this.surveyResponseData.getTotalResponseCount());
    view.setRowCount(this.surveyResponseData.getTotalResponseCount());
  }
  
  private int calcNumResponsesToFetch(int pageSize) {
    // heuristically chosen. double page size so user can remove up to pageSize 
    // responses and still have data to update the display without going to db
    return pageSize * 2;
  }
  
  /**
   * Use this method to display responses filterd by criteria in paramsArg. If data is not
   *   already loaded, this method will request it from the server and display when done
   * @param paramsArg SurveyResponseReadParams object with query filter info
   * @param startIndex Server-side index of first response to show (range.start)
   * @param pageSize Number of responses to display
   */
  private void fetchAndShowResponses(final SurveyResponseReadParams paramsArg,
                                     final int startIndex,
                                     final int pageSize) {
    
    // check to see if the request can be filled using data that's already loaded
    if (!forceReload && this.surveyResponseData.hasRequestedData(paramsArg, startIndex, pageSize)) {
      updateResponseDisplay(this.surveyResponseData, startIndex, pageSize);
    } else { // otherwise fetch data and show it
      // defensive copy
      final SurveyResponseReadParams params = new SurveyResponseReadParams(paramsArg);
      view.showWaitIndicator();
      params.numToSkip_opt = startIndex;
      params.numToProcess_opt = calcNumResponsesToFetch(pageSize);
      
      surveyResponseData.clear(); // clean up previous data
      this.dataService.fetchSurveyResponseData(params, new AsyncCallback<SurveyResponseData>() {
        @Override
        public void onFailure(Throwable caught) {
          view.hideWaitIndicator();
          view.addErrorMessage("There was a problem loading responses for campaign: " + campaignName, 
                               caught.getMessage());
          _logger.severe(caught.getMessage());
          AwErrorUtils.logoutIfAuthException(caught);        
        }
  
        @Override
        public void onSuccess(SurveyResponseData result) {
          view.hideWaitIndicator();
          // save the result to member variable so it can be used to look up response objects later
          surveyResponseData = result;
          // save filter params used to generate this result; useful for checking whether data can be re-used
          surveyResponseData.setParams(params);
          // fill in campaign name before displaying
          result.setCampaignName(campaignName);
          // update view
          updateResponseDisplay(result, startIndex, pageSize);
        }
      });
    }
  }
  
  /**
   * 
   * @param participantName
   * @param campaignId
   * @param campaignName
   * @param surveyName
   * @param privacy
   * @param onlyPhotoResponses
   * @param startDate
   * @param endDate
   * @param startIndex
   * @param pageSize
   */
  private void fetchAndShowResponses(final String participantName,
                                     final String campaignId, 
                                     final String campaignName,
                                     final String surveyName,
                                     final Privacy privacy,
                                     final boolean onlyPhotoResponses,
                                     final Date startDate,
                                     final Date endDate,
                                     final int startIndex,
                                     final int pageSize) {
    final SurveyResponseReadParams params = new SurveyResponseReadParams();
    if (participantName != null && !participantName.isEmpty()) {
      params.userList.add(participantName);
    } else {
      params.userList.add(AwConstants.specialAllValuesToken);
    }
    params.campaignUrn = campaignId;
    if (surveyName != null && !surveyName.isEmpty()) params.surveyIdList_opt.add(surveyName);
    params.privacyState_opt = privacy;
    params.startDate_opt = startDate;
    params.endDate_opt = endDate;
    params.columnList_opt = Arrays.asList("urn:ohmage:user:id",
                                          "urn:ohmage:survey:id",
                                          "urn:ohmage:survey:title",
                                          "urn:ohmage:survey:description",
                                          "urn:ohmage:survey:privacy_state",
                                          "urn:ohmage:prompt:response",
                                          "urn:ohmage:context:epoch_millis");
    params.outputFormat = SurveyResponseReadParams.OutputFormat.JSON_ROWS;
    params.returnId = true; // include survey key so survey can be updated in edit view
    params.promptType_opt = onlyPhotoResponses ? "photo" : null; // needed to reconstruct filter values
    
    // If a survey is selected, fetch responses from that survey and ignore the onlyPhotoResponses flag
    // (OnlyPhotoResponses is only valid when "All" is selected in the survey dropdown)
    if (surveyName != null && !surveyName.isEmpty()) {
      params.surveyIdList_opt.add(surveyName);
    } else if (onlyPhotoResponses) {
      // There's  no "with photos" option in the surveyresponse/read api, so we fake it by
      //   checking the campaign xml to find out which surveys contain at least one photo
      //   prompt and fetch responses from only those surveys
      List<String> surveyIds = selectedCampaignInfo.getSurveyIdsByPromptType("photo");
      if (surveyIds != null && !surveyIds.isEmpty()) {
        params.surveyIdList_opt.addAll(surveyIds);
      } else {
        // Campaign has no photo responses. No need to query the server, just update display
        //   with current params so filters will be set and show an info message
        this.surveyResponseData.clear();
        this.surveyResponseData.setParams(params);
        updateResponseDisplay(this.surveyResponseData, 0, 0);
        view.showNoPhotoResponsesMessage();
        return;
      }
    }
    fetchAndShowResponses(params, startIndex, pageSize);
  }
}
