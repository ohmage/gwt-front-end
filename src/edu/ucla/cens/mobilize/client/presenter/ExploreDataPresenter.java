package edu.ucla.cens.mobilize.client.presenter;

import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;

import edu.ucla.cens.mobilize.client.common.PlotType;
import edu.ucla.cens.mobilize.client.view.ExploreDataView;

public class ExploreDataPresenter implements Presenter {
  
  ExploreDataView view;

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
    // fetch and fill participant choices based on selected campaign (if appropriate for plot)
    // fill prompt choices based on selected campaign (if appropriate for plot)
    // if plot type is selected, fetch and display it
    
    view.setSelectedCampaign(selectedCampaign);
    view.setSelectedParticipant(selectedParticipant);
    //view.setSelectedPlotType(PlotType.fromServerString(selectdPlotType));
    view.setSelectedPromptX(selectedX);
    view.setSelectedPromptY(selectedY);
  }
    
  public void setView(ExploreDataView view) {
    this.view = view;
    addEventHandlersToView();
  }
  
  @SuppressWarnings("deprecation")
  private void addEventHandlersToView() {
    assert view != null : "Attempted to add event handlers to view before calling setView()";
    
    // changing selection in plot type tree enables/disables data controls as appropriate
    view.getPlotTypeTree().addTreeListener(new TreeListener() {
      @Override
      public void onTreeItemSelected(TreeItem item) {
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
        case SCATTERPLOT:
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
  
  private void fetchAndFillParticipantChoices(String campaignId, String participantToSelect) {
  }
  
  private void fetchAndFillPromptChoices(String campaignId, 
                                          String promptToSelectX, 
                                          String promptToSelectY) {
  }



}
