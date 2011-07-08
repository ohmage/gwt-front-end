package edu.ucla.cens.mobilize.client.presenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.PlotType;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
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
  
  public ExploreDataPresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.userInfo = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
  }
  
  @Override
  public void go(Map<String, String> params) {
    String selectedPlotTypeString = params.containsKey("plot") ? params.get("plot") : null;
    PlotType selectedPlotType = PlotType.fromHistoryTokenString(selectedPlotTypeString);
    String selectedCampaign = params.containsKey("cid") ? params.get("cid") : null;
    String selectedParticipant = params.containsKey("uid") ? params.get("uid") : null;
    String selectedX = params.containsKey("x") ? params.get("x") : null;
    String selectedY = params.containsKey("y") ? params.get("y") : null;

    // enable/disable filters based on plot selection
    setEnabledFiltersForPlotType(selectedPlotType);
    // fill campaign choices from userInfo
    view.setCampaignList(userInfo.getCampaigns());
    // fetch and fill participant choices based on selected campaign (if appropriate for plot)
    fetchAndFillParticipantChoices(selectedCampaign, selectedParticipant);
    // fill prompt choices based on selected campaign (if appropriate for plot)
    fetchAndFillPromptChoices(selectedCampaign, selectedX, selectedY);
    
    view.setSelectedCampaign(selectedCampaign);
    view.setSelectedParticipant(selectedParticipant);
    view.setSelectedPlotType(selectedPlotType);
    view.setSelectedPromptX(selectedX);
    view.setSelectedPromptY(selectedY);
    
    showPlot();
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
        fetchAndFillPromptChoices(campaignId, null, null);
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
                                         final String promptToSelectX, 
                                         final String promptToSelectY) {
    if (campaignId == null) return;
    dataService.fetchCampaignDetail(campaignId, new AsyncCallback<CampaignDetailedInfo>() {
      @Override
      public void onFailure(Throwable caught) {
        // TODO
      }

      @Override
      public void onSuccess(CampaignDetailedInfo result) {
        List<String> promptIds = result.getPromptIds();
        Map<String, String> promptIdToNameMap = new HashMap<String, String>();
        for (String promptId : promptIds) {
          promptIdToNameMap.put(promptId, promptId);
          // FIXME: display prompt text here instead of id?
        }
        view.setPromptXList(promptIdToNameMap);
        view.setPromptYList(promptIdToNameMap);
        view.setSelectedPromptX(promptToSelectX);
        view.setSelectedPromptY(promptToSelectY);
      }
    });
  }
  
  private void showPlot() {
    PlotType plotType = view.getSelectedPlotType();
    String campaignId = view.getSelectedCampaign();
    if (plotType == null || campaignId == null) return; 
    String participantId = view.getSelectedParticipant();
    String promptX = view.getSelectedPromptX();
    String promptY = view.getSelectedPromptY();
    int width = view.getPlotPanelWidth();
    int height = view.getPlotPanelHeight();
    String url = dataService.getPlotUrl(plotType,
                                        width,
                                        height,
                                        campaignId,
                                        participantId,
                                        promptX,
                                        promptY);
    _logger.fine("Displaying plot url: " + url);
    view.setPlotUrl(url);
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

}
