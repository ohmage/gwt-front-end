package edu.ucla.cens.mobilize.client.presenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;

import edu.ucla.cens.mobilize.client.AwConstants;
import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.PlotType;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.PromptType;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.SurveyResponseReadParams;
import edu.ucla.cens.mobilize.client.event.CampaignInfoUpdatedEvent;
import edu.ucla.cens.mobilize.client.event.CampaignInfoUpdatedEventHandler;
import edu.ucla.cens.mobilize.client.event.UserInfoUpdatedEvent;
import edu.ucla.cens.mobilize.client.event.UserInfoUpdatedEventHandler;
import edu.ucla.cens.mobilize.client.model.AppConfig;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;
import edu.ucla.cens.mobilize.client.model.MobilityChunkedInfo;
import edu.ucla.cens.mobilize.client.model.MobilityInfo;
import edu.ucla.cens.mobilize.client.model.PromptInfo;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.model.UserParticipationInfo;
import edu.ucla.cens.mobilize.client.ui.ErrorDialog;
import edu.ucla.cens.mobilize.client.utils.AwErrorUtils;
import edu.ucla.cens.mobilize.client.utils.DateUtils;
import edu.ucla.cens.mobilize.client.view.ExploreDataView;

@SuppressWarnings("deprecation")
public class ExploreDataPresenter implements Presenter {
  
  // data that's shared across presenters
  UserInfo userInfo;
  private List<CampaignShortInfo> campaigns;
  DataService dataService;
  EventBus eventBus;
  
  ExploreDataView view;

  private static Logger _logger = Logger.getLogger(ExploreDataPresenter.class.getName());

  private String selectedParticipant;
  
  private static List<PromptType> supportedUnivariate = Arrays.asList( 
      PromptType.HOURS_BEFORE_NOW,
      PromptType.MULTI_CHOICE,
      PromptType.MULTI_CHOICE_CUSTOM,
      PromptType.NUMBER, 
      PromptType.REMOTE_ACTIVITY,
      PromptType.SINGLE_CHOICE, 
      PromptType.SINGLE_CHOICE_CUSTOM,
      PromptType.TEXT, 
      PromptType.TIMESTAMP);
  private static List<PromptType> supportedBivariate = Arrays.asList(
      PromptType.NUMBER,
      PromptType.SINGLE_CHOICE);
  
  public ExploreDataPresenter(UserInfo userInfo, 
                              DataService dataService, 
                              EventBus eventBus,
                              List<CampaignShortInfo> campaigns) {
    this.userInfo = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
    this.campaigns = campaigns;
    bind();
  }
  
  private void bind() {
    this.eventBus.addHandler(UserInfoUpdatedEvent.TYPE, new UserInfoUpdatedEventHandler() {
      @Override
      public void onUserInfoChanged(UserInfoUpdatedEvent event) {
        userInfo = event.getUserInfo();
      }
    });
    
    this.eventBus.addHandler(CampaignInfoUpdatedEvent.TYPE, new CampaignInfoUpdatedEventHandler() {
      @Override
      public void onCampaignInfoUpdated(CampaignInfoUpdatedEvent event) {
        campaigns = event.getCampaigns();
      }
    });
  }
  
  @Override
  public void go(Map<String, String> params) {
    
    // clear existing plot, if any
    view.clearPlot();
    view.showStartArrow();
    
    // get plot settings from params
    String selectedPlotTypeString = params.containsKey("plot") ? params.get("plot") : null;
    PlotType selectedPlotType = PlotType.fromHistoryTokenString(selectedPlotTypeString);
    String selectedCampaign = params.containsKey("cid") ? params.get("cid") : null;
    String selectedParticipant = params.containsKey("uid") ? params.get("uid") : null;
    String selectedX = params.containsKey("x") ? params.get("x") : null;
    String selectedY = params.containsKey("y") ? params.get("y") : null;
    
    Date startDate = view.getFromDate();
    Date endDate = view.getToDate();
    
    // fill campaign choices based on user's role and campaign privacy
    fillCampaignChoices(this.campaigns);
    // enable/disable filters based on plot selection
    setEnabledFiltersForPlotType(selectedPlotType);
    // fetch and fill participant choices based on selected campaign (if appropriate for plot)
    fetchAndFillParticipantChoices(selectedCampaign, selectedParticipant);
    // fill prompt choices based on selected campaign (if appropriate for plot)
    fetchAndFillPromptChoices(selectedCampaign, selectedPlotType, selectedX, selectedY, startDate, endDate);
    
    view.setSelectedCampaign(selectedCampaign);
    view.setSelectedParticipant(selectedParticipant);
    view.setSelectedPlotType(selectedPlotType);
    view.setSelectedPromptX(selectedX);
    view.setSelectedPromptY(selectedY);
    
    if (PlotType.MAP.equals(selectedPlotType)) {
      // fetch points to match data filters
      fetchResponseDataAndShowOnMap(selectedCampaign, selectedParticipant, startDate, endDate); // participant, startDate, endDate can be null
    } else if (PlotType.MOBILITY_MAP.equals(selectedPlotType)) {
    	
    	//FIXME: this is a temporary workaround until code reorg for 2.10
        // for 2.9, start date only allows single day of data. since we disable hidden fields, we need to set this
    	endDate = new Date(startDate.getTime());
     
      // fetch points to mobility data
      fetchMobilityDataAndShowOnMap(startDate, endDate);
    } else if (PlotType.MOBILITY_GRAPH.equals(selectedPlotType)) {
      // fetch data for mobility graph
      fetchMobilityDataAndShowOnGraph(startDate, endDate);
    } else if (PlotType.LEADER_BOARD.equals(selectedPlotType)) {
      fetchAndShowLeaderBoard(selectedCampaign, startDate, endDate);
    } else {
      showPlot();
    }
  }
  
  // adds campaign to dropdown if user is allowed to see responses from it
  private void fillCampaignChoices(List<CampaignShortInfo> campaigns) {
    Map<String, String> campaignIdToNameMap = new HashMap<String, String>();
    for (CampaignShortInfo campaign : campaigns) {
      if (campaign.userIsSupervisorOrAdmin() ||                 // supers see everything
          campaign.userIsAuthor() ||                            // authors see shared data
          (campaign.userIsAnalyst() && campaign.isShared()) ||  // analysts see data if campaign is shared
          campaign.userIsParticipant()) {                       // participants see their own data
        campaignIdToNameMap.put(campaign.getCampaignId(), campaign.getCampaignName());
      }
    }
    view.setCampaignList(campaignIdToNameMap);
  }
  
  
  private void fetchResponseDataAndShowOnMap(String campaignId, String participantUsername, Date startDate, Date endDate) {
    final String campaignName = userInfo.getCampaigns().get(campaignId);
    Privacy privacy = AppConfig.exportAndVisualizeSharedResponsesOnly() ? Privacy.SHARED : null; // null shows everything
    view.showWaitIndicator();
    
    if (participantUsername != null && participantUsername.isEmpty())
      participantUsername = null;
    
    dataService.fetchSurveyResponses(
       participantUsername,
       campaignId, 
       null, //surveyName 
       privacy, 
       startDate,
       endDate,
       new AsyncCallback<List<SurveyResponse>>() {
          @Override
          public void onFailure(Throwable caught) {
            _logger.severe(caught.getMessage());
            ErrorDialog.show("We were unable to fetch any geo-location data from the server",
                             caught.getMessage());
          }
      
          @Override
          public void onSuccess(List<SurveyResponse> result) {
            // fill in campaign name since it's not returned with api data
            for (SurveyResponse response : result) {
              response.setCampaignName(campaignName);
            }
            // show responses on map
            view.showResponsesOnMap(result);
            view.hideWaitIndicator();
          }
       });
	}
	
	private void fetchMobilityDataAndShowOnMap(final Date startDate, final Date endDate) {
		view.showWaitIndicator();
		
		//list for aggregating all the data
		final List<MobilityInfo> mdata = new ArrayList<MobilityInfo>();
		
		for (Date curDate = new Date(startDate.getTime()); curDate.before(endDate) || (curDate.getDate() == endDate.getDate()); curDate = DateUtils.addOneDay(curDate)) {
			dataService.fetchMobilityData(
				curDate,
				null, //TODO: username
				new AsyncCallback<List<MobilityInfo>>() {
					@Override
					public void onFailure(Throwable caught) {
						_logger.severe(caught.getMessage());
						ErrorDialog.show("We were unable to fetch your mobility data from the server", caught.getMessage());
					}
					@Override
					public void onSuccess(List<MobilityInfo> result) {
						mdata.addAll(result);
						
						// show responses on map
						view.showMobilityDataOnMap(mdata);
						view.hideWaitIndicator();
					}
				}
			);
		}
	}
	
	private void fetchMobilityChunkedDataAndShowOnMap(final Date startDate, final Date endDate) {
		view.showWaitIndicator();
		dataService.fetchMobilityDataChunked(
			startDate,
			endDate,
			new AsyncCallback<List<MobilityChunkedInfo>>() {
				@Override
				public void onFailure(Throwable caught) {
					_logger.severe(caught.getMessage());
					ErrorDialog.show("We were unable to fetch your mobility data from the server", caught.getMessage());
				}
				@Override
				public void onSuccess(List<MobilityChunkedInfo> result) {
					// show responses on map
					view.showMobilityChunkedDataOnMap(result);
					view.hideWaitIndicator();
				}
			}
		);
	}

	private void fetchMobilityDataAndShowOnGraph(final Date startDate, final Date endDate) { //FIXME: blah
		view.showWaitIndicator();
		
		dataService.fetchMobilityData(
			startDate,
			null,
			new AsyncCallback<List<MobilityInfo>>() {
				@Override
				public void onFailure(Throwable caught) {
					_logger.severe(caught.getMessage());
					ErrorDialog.show("We were unable to fetch your mobility data from the server", caught.getMessage());
				}
				
				@Override
				public void onSuccess(List<MobilityInfo> result) {	//FIXME
					// show responses on map
					view.showMobilityDataOnGraph(result);
					view.hideWaitIndicator();
				}
			}
		);
	}


  void fetchAndShowLeaderBoard(final String campaignId, Date startDate, Date endDate) {
    // fetch responses and use them to generate counts
    SurveyResponseReadParams params = new SurveyResponseReadParams();
    params.campaignUrn = campaignId;
    params.columnList_opt.add("urn:ohmage:user:id");
    params.columnList_opt.add("urn:ohmage:survey:privacy_state");
    params.outputFormat = SurveyResponseReadParams.OutputFormat.JSON_ROWS;
    params.userList.add(AwConstants.specialAllValuesToken);
    if (startDate != null && endDate != null) {
	    params.startDate_opt = (Date)startDate.clone();
	    params.endDate_opt = (Date)endDate.clone();
    }
        
    dataService.fetchSurveyResponses(params, new AsyncCallback<List<SurveyResponse>>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        ErrorDialog.show("Could not load leaderboard data for campaign: " + campaignId);         
      }
  
      @Override
      public void onSuccess(List<SurveyResponse> result) {
        // map usernames to info about user participation (e.g., counts of shared, private, etc responses)
        Map<String, UserParticipationInfo> usernameToParticipationInfoMap = new HashMap<String, UserParticipationInfo>();
        for (SurveyResponse response : result) {
          String username = response.getUserName();
          // make sure user has an entry in the data struct
          if (!usernameToParticipationInfoMap.containsKey(username)) {
            usernameToParticipationInfoMap.put(username, new UserParticipationInfo(username));
          }
          // update counts
          usernameToParticipationInfoMap.get(username).countResponse(response);
        }
        List<UserParticipationInfo> participationInfo = new ArrayList<UserParticipationInfo>(usernameToParticipationInfoMap.values());
        Collections.sort(participationInfo, participationUsernameComparator);
        view.renderLeaderBoard(participationInfo);
        view.setInfoText("Showing participation info for " + getCampaignName(campaignId));
      }
    });       
  }
  
  public void setView(ExploreDataView view) {
    this.view = view;
    addEventHandlersToView();
  }
  
  private void addEventHandlersToView() {
    assert view != null : "Attempted to add event handlers to view before calling setView()";
    
    // changing selection in plot type tree enables/disables data controls as appropriate
    view.getPlotTypeTree().addTreeListener(new TreeListener() {
      @Override
      public void onTreeItemSelected(TreeItem item) {
        view.clearMissingFieldMarkers(); // clear any leftover validation errors
        setEnabledFiltersForPlotType(view.getSelectedPlotType());
        
        //preserve old choices, if valid. fetchAndFill... will reset any invalid choices in the list
        String campaignId = view.getSelectedCampaign();
        PlotType plotType = view.getSelectedPlotType();
        fetchAndFillPromptChoices(campaignId, plotType, view.getSelectedPromptX(), view.getSelectedPromptY(), view.getFromDate(), view.getToDate());
        fetchAndFillParticipantChoices(campaignId, view.getSelectedParticipant());
        
        view.isMissingRequiredField();
      }

      @Override
      public void onTreeItemStateChanged(TreeItem item) {
      }
    });
    
    view.getCampaignDropDown().addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        String campaignId = view.getSelectedCampaign();
        PlotType plotType = view.getSelectedPlotType();
        fetchAndFillPromptChoices(campaignId, plotType, null, null, null, null);
        fetchAndFillParticipantChoices(campaignId, null);
      }
    });
    
    view.getDrawPlotButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
    	view.clearPlot(); // prev plot is cleared even if inputs are invalid
        String promptX = view.getSelectedPromptX();
        String promptY = view.getSelectedPromptY();
        Date fromDate = view.getFromDate();
        Date toDate = view.getToDate();
        
        //FIXME: this is a temporary workaround until code reorg for 2.10
        // for 2.9, start date only allows single day of data. since we disable hidden fields, we need to set this
        if (view.getSelectedPlotType().equals(PlotType.MOBILITY_MAP)) {
        	toDate = view.getFromDate();
        }
        
        if (promptX != null && promptX.equals(promptY)) {	//FIXME: clean up this nasty nested 'if'
          ErrorDialog.show("Invalid prompt choice", "X and Y prompts must be different.");
        }
        else if ((fromDate == null && toDate != null) || (fromDate != null && toDate == null)) {
          ErrorDialog.show("Invalid date range", "You must specify both a start date and an end date for date filtering. Otherwise, leave both fields blank.");
        }
        else if (fromDate != null && toDate != null && fromDate.after(toDate)) {	//make sure date range is valid
          ErrorDialog.show("Invalid date range", "Starting date must be before or equal to the end date.");
        }
        else if ((view.getSelectedPlotType().equals(PlotType.MOBILITY_MAP) || view.getSelectedPlotType().equals(PlotType.MOBILITY_GRAPH))
        			&& (fromDate == null || toDate == null)) {
        	ErrorDialog.show("Invalid date range", "You must specify a start and end date range to view your mobility data.");
        }
        else if ((view.getSelectedPlotType().equals(PlotType.MOBILITY_MAP) || view.getSelectedPlotType().equals(PlotType.MOBILITY_GRAPH))
        			&& DateUtils.daysApart(fromDate, toDate) > 7) { //FIXME: quick and dirty date check until server supports longer date range
        	ErrorDialog.show("Invalid date range", "Mobility dates can only be up to a 7 day range.");
      	}
        else if (!view.isMissingRequiredField()) { // view marks missing fields, if any
          fireHistoryTokenToMatchSelectedSettings();
        }
      }
    });
    
    view.getExportDataButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        exportCsv(view.getSelectedCampaign());
      }
    });
    
  }

  private void setEnabledFiltersForPlotType(final PlotType plotType) {
    if (plotType == null) { // no plot selected
      view.disableAllDataControls();
      view.setSelectedPlotType(null); // don't allow "selection" of category nodes
    } else {
      view.setDataButtonsEnabled(true);
      switch (plotType) {
        case SURVEY_RESPONSE_COUNT:
        case SURVEY_RESPONSES_PRIVACY_STATE:
        case SURVEY_RESPONSES_PRIVACY_STATE_TIME:
        case LEADER_BOARD:
          view.setCampaignDropDownEnabled(true);
          view.setParticipantDropDownEnabled(false);
          view.setPromptXDropDownEnabled(false);
          view.setPromptYDropDownEnabled(false);
          view.setDateRangeEnabled(true);
          view.setExportButtonEnabled(true);
          break;
        case USER_TIMESERIES:
          view.setCampaignDropDownEnabled(true);
          view.setParticipantDropDownEnabled(true);
          view.setPromptXDropDownEnabled(true);
          view.setPromptYDropDownEnabled(false);
          view.setDateRangeEnabled(true);
          view.setExportButtonEnabled(true);
          break;
        case PROMPT_TIMESERIES:
        case PROMPT_DISTRIBUTION:
          view.setCampaignDropDownEnabled(true);
          view.setParticipantDropDownEnabled(false);
          view.setPromptXDropDownEnabled(true);
          view.setPromptYDropDownEnabled(false);
          view.setDateRangeEnabled(true);
          view.setExportButtonEnabled(true);
          break;
        case SCATTER_PLOT:
        case DENSITY_PLOT:
          view.setCampaignDropDownEnabled(true);
          view.setParticipantDropDownEnabled(false);
          view.setPromptXDropDownEnabled(true);
          view.setPromptYDropDownEnabled(true);
          view.setDateRangeEnabled(true);
          view.setExportButtonEnabled(true);
          break;
        case MAP:
          view.setCampaignDropDownEnabled(true);
          view.setParticipantDropDownEnabled(true);
          view.setPromptXDropDownEnabled(false);
          view.setPromptYDropDownEnabled(false);
          view.setDateRangeEnabled(true);
          view.setExportButtonEnabled(false);
          break;
        case MOBILITY_MAP:
          view.setCampaignDropDownEnabled(false);
          view.setParticipantDropDownEnabled(false);
          view.setPromptXDropDownEnabled(false);
          view.setPromptYDropDownEnabled(false);
          view.setStartDateRangeEnabled(true);
          view.setEndDateRangeEnabled(false);
          view.setExportButtonEnabled(false);
          break;
        case MOBILITY_GRAPH:
          view.setCampaignDropDownEnabled(false);
          view.setParticipantDropDownEnabled(false);
          view.setPromptXDropDownEnabled(false);
          view.setPromptYDropDownEnabled(false);
          view.setDateRangeEnabled(true);
          view.setExportButtonEnabled(false);
          break;
        default:
          break;
      }
    }
  }
  
  // Returns info about campaign with given id or null if that campaign is not loaded.
  private CampaignShortInfo getCampaignInfo(String campaignId) {
    CampaignShortInfo retval = null;
    for (CampaignShortInfo campaign : this.campaigns) {
      if (campaign.getCampaignId().equals(campaignId)) {
        retval = campaign; // gotcha: not a defensive copy
        break;
      }
    }
    return retval;
  }
  
  private void fetchAndFillParticipantChoices(String campaignId, String participantToSelect) {
    if (campaignId == null) return;
    this.selectedParticipant = participantToSelect; // participantFetchCallback needs this
    CampaignShortInfo campaign = getCampaignInfo(campaignId);
    if (campaign == null) {
      // show all participants. user will see error if they try to plot something they can't see
      dataService.fetchParticipantsWithResponses(campaignId, false, this.participantFetchCallback);
    } else if (campaign.userIsSupervisorOrAdmin()) {
      // supers can see everyone's responses
      dataService.fetchParticipantsWithResponses(campaignId, false, this.participantFetchCallback);
    } else if (campaign.userIsAuthor()) {
      // campaign author can only see shared responses // FIXME: true for all installations?
      dataService.fetchParticipantsWithResponses(campaignId, true, this.participantFetchCallback);
    } else {
      // participants can only see themselves.
      view.setParticipantList(Arrays.asList(userInfo.getUserName()));
    }
  }
  
  
  // callback that populates participant dialog
  private AsyncCallback<List<String>> participantFetchCallback = new AsyncCallback<List<String>>() {
    @Override
    public void onFailure(Throwable caught) {
      ErrorDialog.show("Could not load participant list for campaign.", caught.getMessage());
      AwErrorUtils.logoutIfAuthException(caught);
    }

    @Override
    public void onSuccess(List<String> result) {
      Collections.sort(result);
      view.setParticipantList(result);
      view.setSelectedParticipant(selectedParticipant);
      // class member var selectedParticipant should have been set just before fetch
    }
  };
  
  private void fetchAndFillPromptChoices(String campaignId, 
                                         final PlotType plotType,
                                         final String promptToSelectX, 
                                         final String promptToSelectY,
                                         final Date startDate,
                                         final Date endDate) {
    if (campaignId == null || plotType == null) return;
    dataService.fetchCampaignDetail(campaignId, new AsyncCallback<CampaignDetailedInfo>() {
      @Override
      public void onFailure(Throwable caught) {
        _logger.severe("Could not load prompt list: " + caught.getMessage());
      }

      @Override
      public void onSuccess(CampaignDetailedInfo result) {	//TODO: filter by date
        view.clearPromptXList();
        view.clearPromptYList();
        List<PromptInfo> prompts = result.getPrompts();
        for (PromptInfo prompt : prompts) {
          boolean isSupported = promptTypeIsSupported(plotType, prompt.getPromptType());
          String promptId = prompt.getPromptId();
          view.addPromptX(promptId, promptId, isSupported); // display text == id for now
          view.addPromptY(promptId, promptId, isSupported); // display text == id for now
        }
        view.setSelectedPromptX(promptToSelectX);
        view.setSelectedPromptY(promptToSelectY);
      }
    });
  }
  
  private boolean promptTypeIsSupported(PlotType plotType, PromptType promptType) {
    boolean isSupported = false;
    switch (plotType) {
    case SURVEY_RESPONSE_COUNT:
    case SURVEY_RESPONSES_PRIVACY_STATE:
    case SURVEY_RESPONSES_PRIVACY_STATE_TIME:
    case USER_TIMESERIES:
    case PROMPT_DISTRIBUTION:
    case PROMPT_TIMESERIES:
      isSupported = supportedUnivariate.contains(promptType);
      break;
    case SCATTER_PLOT:
    case DENSITY_PLOT:
      isSupported = supportedBivariate.contains(promptType);
      break;
    case MAP:
      isSupported = true; // all prompt types supported for geo
      break;
    default:
      break;
    }
    return isSupported;
  }
  
  
  private void showPlot() {
    final PlotType plotType = view.getSelectedPlotType();
    final String campaignId = view.getSelectedCampaign();
    if (plotType == null || campaignId == null) return; 
    final String participantId = view.getSelectedParticipant();
    final String promptX = view.getSelectedPromptX();
    final String promptY = view.getSelectedPromptY();
    final int px_margin = 5; //for reducing pixel width/height to avoid scrollbars on browsers with thicker chromes
    final int width = view.getPlotPanelWidth() - px_margin;
    final int height = view.getPlotPanelHeight() - px_margin;
    final Date startDate = view.getFromDate();
    final Date endDate = view.getToDate();
    
    // mobilize only shows shared responses for most plots but some installations 
    // may want to include private ones.
    boolean includePrivateResponsesInAllPlots = !AppConfig.exportAndVisualizeSharedResponsesOnly();
    final boolean includePrivateResponses = includePrivateResponsesInAllPlots ||
      plotType.equals(PlotType.SURVEY_RESPONSES_PRIVACY_STATE) || // always include private responses for this plot type 
      plotType.equals(PlotType.SURVEY_RESPONSES_PRIVACY_STATE_TIME); // always include private responses for this plot type
    
    String url = dataService.getVisualizationUrl(plotType,
                                                 width,
                                                 height,
                                                 campaignId,
                                                 participantId,
                                                 promptX,
                                                 promptY,
                                                 startDate,
                                                 endDate,
                                                 includePrivateResponses);
    _logger.fine("Displaying plot url: " + url);
    view.setPlotUrl(url, new ErrorHandler() {
      @Override
      public void onError(ErrorEvent event) {
        // if the image doesn't load, make an ajax call with the same params to retrieve the error message
        dataService.fetchVisualizationError(plotType, width, height, campaignId, 
            participantId, promptX, promptY, startDate, endDate, includePrivateResponses,
          new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
              ErrorDialog.show("There was a problem loading the visualization.", caught.getMessage());
            }
            
            @Override
            public void onSuccess(String result) {
              // must have been a transient error. try loading it again
              view.setPlotUrl(result); // no error handler this time
            }
          }); // new AsyncCallback<String>() {  
      }
    });
  }

  
  private void fireHistoryTokenToMatchSelectedSettings() {
    PlotType selectedPlotType = view.getSelectedPlotType();
    String selectedCampaign = view.getSelectedCampaign();
    String selectedParticipant = view.getSelectedParticipant();
    String selectedPromptX = view.getSelectedPromptX();
    String selectedPromptY = view.getSelectedPromptY();
    
    String token = HistoryTokens.exploreData(selectedPlotType, 
                                             selectedCampaign,  
                                             selectedParticipant, 
                                             selectedPromptX, 
                                             selectedPromptY);
    
    // NOTE: forces plot to be redrawn even if the history token params haven't
    // changed. This way the user can keep clicking Draw Plot to refresh. 
    History.newItem(token, false); // false arg suppresses value change event
    History.fireCurrentHistoryState(); // calls value change handler manually
  }
  
  private void exportCsv(String campaignId) {
    if (campaignId != null) {
      view.doExportCsvFormPost(AwConstants.getSurveyResponseReadUrl(), 
                               dataService.getSurveyResponseExportParams(campaignId));
    }
  }

  // helper method
  private String getCampaignName(String campaignId) {
    String retval = null;
    Map<String, String> campaignIdToNameMap = userInfo.getCampaigns();
    if (campaignIdToNameMap.containsKey(campaignId)) {
      retval = campaignIdToNameMap.get(campaignId);
    } else {
      // would happen if user queried for a campaign that's not in userInfo - 
      // maybe he edited url params by hand or was using an old link for a campaign
      // to which he no longer belongs
      retval = "(unknown campaign)";
    }
    return retval;
  }
  
  // for sorting participation info by username (in leader board)
  private Comparator<UserParticipationInfo> participationUsernameComparator = new Comparator<UserParticipationInfo>() {
    @Override
    public int compare(UserParticipationInfo arg0, UserParticipationInfo arg1) {
      return arg0.getUsername().compareTo(arg1.getUsername());
    }
  };
  
}
