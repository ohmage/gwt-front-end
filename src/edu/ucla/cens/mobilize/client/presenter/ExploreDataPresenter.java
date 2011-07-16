package edu.ucla.cens.mobilize.client.presenter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
import edu.ucla.cens.mobilize.client.common.PromptType;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.PromptInfo;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.ui.ErrorDialog;
import edu.ucla.cens.mobilize.client.utils.AwErrorUtils;
import edu.ucla.cens.mobilize.client.view.ExploreDataView;

@SuppressWarnings("deprecation")
public class ExploreDataPresenter implements Presenter {
  
  UserInfo userInfo;
  DataService dataService;
  EventBus eventBus;
  ExploreDataView view;

  private static Logger _logger = Logger.getLogger(ExploreDataPresenter.class.getName());
  
  private static List<PromptType> supportedUnivariate = Arrays.asList(PromptType.NUMBER, 
                                                                      PromptType.TEXT, 
                                                                      PromptType.SINGLE_CHOICE, 
                                                                      PromptType.REMOTE_ACTIVITY, 
                                                                      PromptType.MULTI_CHOICE);
  private static List<PromptType> supportedBivariate = Arrays.asList(PromptType.NUMBER,
                                                                      PromptType.SINGLE_CHOICE);
  
  public ExploreDataPresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.userInfo = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
  }
  
  @Override
  public void go(Map<String, String> params) {
    
    // clear existing plot, if any
    view.clearPlot(); 
    
    // get plot settings from params
    String selectedPlotTypeString = params.containsKey("plot") ? params.get("plot") : null;
    PlotType selectedPlotType = PlotType.fromHistoryTokenString(selectedPlotTypeString);
    String selectedCampaign = params.containsKey("cid") ? params.get("cid") : null;
    String selectedParticipant = params.containsKey("uid") ? params.get("uid") : null;
    String selectedX = params.containsKey("x") ? params.get("x") : null;
    String selectedY = params.containsKey("y") ? params.get("y") : null;
    
    // fill campaign choices from userInfo
    view.setCampaignList(userInfo.getCampaigns());
    // enable/disable filters based on plot selection
    setEnabledFiltersForPlotType(selectedPlotType);
    // fetch and fill participant choices based on selected campaign (if appropriate for plot)
    fetchAndFillParticipantChoices(selectedCampaign, selectedParticipant);
    // fill prompt choices based on selected campaign (if appropriate for plot)
    fetchAndFillPromptChoices(selectedCampaign, selectedPlotType, selectedX, selectedY);
    
    view.setSelectedCampaign(selectedCampaign);
    view.setSelectedParticipant(selectedParticipant);
    view.setSelectedPlotType(selectedPlotType);
    view.setSelectedPromptX(selectedX);
    view.setSelectedPromptY(selectedY);
    
    if (PlotType.MAP.equals(selectedPlotType)) {
      // fetch points to match data filters
      fetchResponseDataAndShowOnMap(selectedCampaign, selectedParticipant); // participant can be null
    } else {
      showPlot();
    }
  }
    
  private void fetchResponseDataAndShowOnMap(String campaignId, String participantUsername) {
    final String campaignName = userInfo.getCampaigns().get(campaignId);
    view.showWaitIndicator();
    dataService.fetchSurveyResponses(participantUsername, campaignId, 
       null, //surveyName 
       null, //privacy
       null, //startDate 
       null, //endDate 
       new AsyncCallback<List<SurveyResponse>>() {
          @Override
          public void onFailure(Throwable caught) {
            _logger.severe(caught.getMessage());
            ErrorDialog.show("There was a problem fetching data points for the geo visualization",
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
      }

      @Override
      public void onTreeItemStateChanged(TreeItem item) {
      }
    });
    
    view.getCampaignDropDown().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        String campaignId = view.getSelectedCampaign();
        PlotType plotType = view.getSelectedPlotType();
        fetchAndFillPromptChoices(campaignId, plotType, null, null);
        fetchAndFillParticipantChoices(campaignId, null);
      }
    });
    
    view.getDrawPlotButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.clearPlot();
        if (!view.isMissingRequiredField()) {
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

  private void setEnabledFiltersForPlotType(PlotType plotType) {
    if (plotType == null) { // no plot selected
      view.disableAllDataControls();
      view.setSelectedPlotType(null); // don't allow "selection" of category nodes
    } else {
      view.setDataButtonsEnabled(true);
      switch (plotType) {
        case SURVEY_RESPONSE_COUNT:
          view.setCampaignDropDownEnabled(true);
          view.setParticipantDropDownEnabled(false);
          view.setPromptXDropDownEnabled(false);
          view.setPromptYDropDownEnabled(false);
          break;
        case USER_TIMESERIES:
          view.setCampaignDropDownEnabled(true);
          view.setParticipantDropDownEnabled(true);
          view.setPromptXDropDownEnabled(true);
          view.setPromptYDropDownEnabled(false);
          break;
        case PROMPT_TIMESERIES:
        case PROMPT_DISTRIBUTION:
          view.setCampaignDropDownEnabled(true);
          view.setParticipantDropDownEnabled(false);
          view.setPromptXDropDownEnabled(true);
          view.setPromptYDropDownEnabled(false);
          break;
        case SCATTER_PLOT:
        case DENSITY_PLOT:
          view.setCampaignDropDownEnabled(true);
          view.setParticipantDropDownEnabled(false);
          view.setPromptXDropDownEnabled(true);
          view.setPromptYDropDownEnabled(true);
          break;
        case MAP:
          view.setCampaignDropDownEnabled(true);
          view.setParticipantDropDownEnabled(false);
          view.setPromptXDropDownEnabled(false);
          view.setPromptYDropDownEnabled(false);
          break;
        default:
          break;
      }
    }
  }
  
  private void fetchAndFillParticipantChoices(String campaignId, final String participantToSelect) {
    if (campaignId == null) return;
    dataService.fetchParticipantsWithResponses(campaignId, new AsyncCallback<List<String>>() {
      @Override
      public void onFailure(Throwable caught) {
        ErrorDialog.show("Could not load participant list for campaign.", caught.getMessage());
        AwErrorUtils.logoutIfAuthException(caught);
      }

      @Override
      public void onSuccess(List<String> result) {
        view.setParticipantList(result);
        view.setSelectedParticipant(participantToSelect); // can be null
      }
    });
  }
  
  private void fetchAndFillPromptChoices(String campaignId, 
                                         final PlotType plotType,
                                         final String promptToSelectX, 
                                         final String promptToSelectY) {
    if (campaignId == null || plotType == null) return;
    dataService.fetchCampaignDetail(campaignId, new AsyncCallback<CampaignDetailedInfo>() {
      @Override
      public void onFailure(Throwable caught) {
        // TODO
      }

      @Override
      public void onSuccess(CampaignDetailedInfo result) {
        Map<String, String> promptIdToNameMap = new HashMap<String, String>();
        List<PromptInfo> prompts = result.getPrompts();
        for (PromptInfo prompt : prompts) {
          if (promptTypeIsSupported(plotType, prompt.getPromptType())) {
            String promptId = prompt.getPromptId();
            promptIdToNameMap.put(promptId, promptId); // name==id for now
          }
        }     
        view.setPromptXList(promptIdToNameMap);
        view.setPromptYList(promptIdToNameMap);
        view.setSelectedPromptX(promptToSelectX);
        view.setSelectedPromptY(promptToSelectY);
      }
    });
  }
  
  private boolean promptTypeIsSupported(PlotType plotType, PromptType promptType) {
    boolean isSupported = false;
    switch (plotType) {
    case SURVEY_RESPONSE_COUNT:
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
    final int width = view.getPlotPanelWidth();
    final int height = view.getPlotPanelHeight();
    
    
    String url = dataService.getVisualizationUrl(plotType,
                                                 width,
                                                 height,
                                                 campaignId,
                                                 participantId,
                                                 promptX,
                                                 promptY);
    _logger.fine("Displaying plot url: " + url);
    view.setPlotUrl(url, new ErrorHandler() {
      @Override
      public void onError(ErrorEvent event) {
        // if the image doesn't load, make an ajax call with the same params to retrieve the error message
        dataService.fetchVisualizationError(plotType, width, height, campaignId, participantId, promptX, promptY,
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
  

}
