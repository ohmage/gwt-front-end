package edu.ucla.cens.mobilize.client.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SourcesTreeEvents;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.MapTypeId;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.InfoWindow;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.base.LatLngBounds;
import com.google.gwt.maps.client.event.Event;
import com.google.gwt.maps.client.event.EventCallback;
import com.google.gwt.maps.client.event.HasMapsEventListener;
import com.google.gwt.maps.client.overlay.Marker;

import edu.ucla.cens.mobilize.client.common.PlotType;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.model.AppConfig;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.model.UserParticipationInfo;
import edu.ucla.cens.mobilize.client.ui.ResponseWidgetPopup;
import edu.ucla.cens.mobilize.client.utils.DateUtils;
import edu.ucla.cens.mobilize.client.utils.MapUtils;

@SuppressWarnings("deprecation")
public class ExploreDataViewImpl extends Composite implements ExploreDataView {

  private static ExploreDataViewUiBinder uiBinder = GWT
      .create(ExploreDataViewUiBinder.class);

  @UiTemplate("ExploreDataView.ui.xml")
  interface ExploreDataViewUiBinder extends UiBinder<Widget, ExploreDataViewImpl> {
  }

  public interface ExploreDataStyles extends CssResource {
    String disabled();
    String leaderBoardHeaderRow();
    String leaderBoardTotalsRow();
    String requiredField();
    String requiredFieldMissing();
    String treeItemCategory();
    String treeItemPlotType();
    String treeItemMap();
    String treeItemHist();
    String treeItemTimeseries();
    String treeItemTable();
    String waiting();
  }
  
  @UiField ExploreDataStyles style;
  @UiField DockLayoutPanel layoutPanel;
  @UiField VerticalPanel sideBar;
  @UiField Tree plotTypeTree;
  @UiField CaptionPanel dataControls;
  @UiField Label requiredFieldMissingMsg;
  @UiField ListBox campaignListBox;
  @UiField ListBox participantListBox;
  @UiField ListBox promptXListBox;
  @UiField ListBox promptYListBox;
  @UiField DateBox fromDateBox;
  @UiField DateBox toDateBox;
  @UiField Button drawPlotButton;
  //@UiField Button pdfButton;
  @UiField Button exportButton;
  @UiField HTMLPanel hiddenFormContainer;
  @UiField FlowPanel plotContainer;
  
  private List<ListBox> requiredFields = new ArrayList<ListBox>();
  private MapWidget mapWidget;
  private final InfoWindow infoWindow;
  private List<HasMapsEventListener> clickHandlers;
  private Map<Marker, SurveyResponse> markerToResponseMap = new HashMap<Marker, SurveyResponse>();
  private Image spinner; 
  
  public ExploreDataViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
    loadPlotTypeTree();
    
    // make data filter panel stick to the bottom of the page
    sideBar.setCellVerticalAlignment(sideBar.getWidget(1), VerticalPanel.ALIGN_BOTTOM);

    // these are required when enabled
    requiredFields = Arrays.asList(campaignListBox, participantListBox, promptXListBox, promptYListBox);
    
    // set up date pickers
    DateBox.Format fmt = new DateBox.DefaultFormat(DateUtils.getDateBoxDisplayFormat());
    fromDateBox.setFormat(fmt);
    toDateBox.setFormat(fmt);
    
    // set up image to use as wait indicator
    spinner = new Image();
    spinner.setStyleName(style.waiting());
    
    // Single info window instance used by all markers
    infoWindow = InfoWindow.newInstance();
    clickHandlers = new ArrayList<HasMapsEventListener>();
  }

  
  // display text, value associated with item, css class name for wrapper span
  private TreeItem getTreeItem(String text, PlotType plotType, String cssStyle) {
    StringBuilder sb = new StringBuilder();
    sb.append("<span class=").append(cssStyle).append(">").append(text).append("</span>");
    TreeItem treeItem = new TreeItem(sb.toString());
    if (plotType != null) treeItem.setUserObject(plotType);
    return treeItem;    
  }

  // use for tree item that won't have a value (i.e., category/parent nodes)
  private TreeItem getTreeItem(String text, String cssStyle) {
    return getTreeItem(text, null, cssStyle);
  }
  
  private void loadPlotTypeTree() {
    // NOTE(8/4/2011): plotType enum is converted to all lowercase and becomes the plot type argument
    // in the api call. So SURVEY_RESPONSE_COUNT becomes "/app/viz/survey_response_count/read..."
    // (See AndWellnessDataService.getVisualizationUrl())
    // NOTE(8/27/2011): LEADER_BOARD is a special case that does not query the api
    
    // response count 
    TreeItem surveyResponseCounts = getTreeItem("Survey Response Counts", style.treeItemCategory()); // category
    TreeItem totalResponses = AppConfig.exportAndVisualizeSharedResponsesOnly() ?  
      getTreeItem("Shared Responses", PlotType.SURVEY_RESPONSE_COUNT, style.treeItemPlotType()) :
      getTreeItem("Total Responses", PlotType.SURVEY_RESPONSE_COUNT, style.treeItemPlotType());
    TreeItem responsesByPrivacy = getTreeItem("Responses By Privacy", PlotType.SURVEY_RESPONSES_PRIVACY_STATE, style.treeItemPlotType());
    TreeItem responseTimeseries = getTreeItem("Response Timeseries", PlotType.SURVEY_RESPONSES_PRIVACY_STATE_TIME, style.treeItemPlotType());
    TreeItem leaderBoard = getTreeItem("Leader Board", PlotType.LEADER_BOARD, style.treeItemPlotType());
    
    // univariate 
    TreeItem univariate = getTreeItem("Univariate", style.treeItemCategory());
    TreeItem userTimeseries = getTreeItem("User Timeseries", PlotType.USER_TIMESERIES, style.treeItemTimeseries());
    TreeItem promptTimeseries = getTreeItem("Prompt Timeseries", PlotType.PROMPT_TIMESERIES, style.treeItemTimeseries());
    TreeItem promptDistribution = getTreeItem("Prompt Distribution", PlotType.PROMPT_DISTRIBUTION, style.treeItemHist());
    
    // multivariate 
    TreeItem multivariate = getTreeItem("Multivariate", style.treeItemCategory()); // category
    TreeItem scatterplot = getTreeItem("Scatterplot", PlotType.SCATTER_PLOT, style.treeItemTable());
    TreeItem density = getTreeItem("2D Density Plot", PlotType.DENSITY_PLOT, style.treeItemTable());
    
    // geographic 
    TreeItem geographic = getTreeItem("Geographical", style.treeItemCategory()); // category
    TreeItem googleMap = getTreeItem("Google Map", PlotType.MAP, style.treeItemMap());
    
    // build the tree
    plotTypeTree.addItem(surveyResponseCounts);
    plotTypeTree.addItem(univariate);
    plotTypeTree.addItem(multivariate);
    plotTypeTree.addItem(geographic);
    surveyResponseCounts.addItem(totalResponses);
    surveyResponseCounts.addItem(responsesByPrivacy);
    surveyResponseCounts.addItem(responseTimeseries);
    surveyResponseCounts.addItem(leaderBoard);
    univariate.addItem(userTimeseries);
    univariate.addItem(promptTimeseries);
    univariate.addItem(promptDistribution);
    multivariate.addItem(scatterplot);
    multivariate.addItem(density);
    geographic.addItem(googleMap);
  }

  @Override
  public void setCampaignList(Map<String, String> campaignIdToNameMap) {
    if (campaignIdToNameMap == null) return;
    campaignListBox.clear();
    List<String> idsSortedByName = MapUtils.getKeysSortedByValues(campaignIdToNameMap);
    for (String campaignId : idsSortedByName) {
      campaignListBox.addItem(campaignIdToNameMap.get(campaignId), campaignId);
    }
  }


  @Override
  public void setSelectedCampaign(String campaignId) {
    campaignListBox.setSelectedIndex(-1);
    for (int i = 0; i < campaignListBox.getItemCount(); i++) {
      if (campaignListBox.getValue(i).equals(campaignId)) {
        campaignListBox.setSelectedIndex(i);
        break;
      }
    }
  }


  @Override
  public String getSelectedCampaign() {
    if (!campaignListBox.isEnabled()) return null;
    int index = campaignListBox.getSelectedIndex();
    return (index > -1) ? campaignListBox.getValue(index) : null;
    
  }


  @Override
  public void setParticipantList(List<String> participants) {
    participantListBox.clear();
    if (participants == null) return;
    for (String username : participants) {
      participantListBox.addItem(username, username);
    }
  }


  @Override
  public void setSelectedParticipant(String participantUsername) {
    participantListBox.setSelectedIndex(-1);
    for (int i = 0; i < participantListBox.getItemCount(); i++) {
      if (participantListBox.getValue(i).equals(participantUsername)) {
        participantListBox.setSelectedIndex(i);
        break;
      }
    }    
  }


  @Override
  public String getSelectedParticipant() {
    if (!participantListBox.isEnabled()) return null;
    int index = participantListBox.getSelectedIndex();
    return (index > -1) ? participantListBox.getValue(index) : null;
  }

  @Override
  public void setSelectedPromptX(String promptId) {
    promptXListBox.setSelectedIndex(-1);
    for (int i = 0; i < promptXListBox.getItemCount(); i++) {
      if (promptXListBox.getValue(i).equals(promptId)) {
        promptXListBox.setSelectedIndex(i);
        break;
      }
    }    
  }


  @Override
  public String getSelectedPromptX() {
    if (!promptXListBox.isEnabled()) return null;
    int index = promptXListBox.getSelectedIndex();
    return (index > -1) ? promptXListBox.getValue(index) : null;
  }

  @Override
  public void setSelectedPromptY(String promptId) {
    promptYListBox.setSelectedIndex(-1);
    for (int i = 0; i < promptYListBox.getItemCount(); i++) {
      if (promptYListBox.getValue(i).equals(promptId)) {
        promptYListBox.setSelectedIndex(i);
        break;
      }
    }       
  }


  @Override
  public String getSelectedPromptY() {
    if (!promptYListBox.isEnabled()) return null;
    int index = promptYListBox.getSelectedIndex();
    return (index > -1) ? promptYListBox.getValue(index) : null;
  }


  @Override
  public PlotType getSelectedPlotType() {
    TreeItem selected = plotTypeTree.getSelectedItem();
    return (selected != null) ? (PlotType)selected.getUserObject() : null;
  }


  @Override
  public void setSelectedPlotType(PlotType plotType) {
    plotTypeTree.setSelectedItem(null);
    Iterator<TreeItem> iter = plotTypeTree.treeItemIterator();
    while (iter.hasNext()) {
      TreeItem curr = iter.next();
      PlotType treeItemPlotType = (PlotType)curr.getUserObject();
      if (treeItemPlotType != null && treeItemPlotType.equals(plotType)) {
        plotTypeTree.setSelectedItem(curr);
        break;
      }
    }
    plotTypeTree.ensureSelectedItemVisible();
  }

  @Override
  public void setPlotUrl(String url) {
    setPlotUrl(url, null); // no custom error handler
  }

  @Override
  public void setPlotUrl(String url, final ErrorHandler errorHandler) {
    clearPlot();
    //final Image loading = new Image();
    //loading.setStyleName(style.waiting());
    //plotContainer.add(loading);
    showWaitIndicator();
    Image plot = new Image(url);
    plot.addLoadHandler(new LoadHandler() {
      @Override
      public void onLoad(LoadEvent event) {
        hideWaitIndicator();
      }
    });
    plot.addErrorHandler(new ErrorHandler() {
      @Override
      public void onError(ErrorEvent event) {
        // get rid of the loading indicator and broken image
        plotContainer.clear();
        // also call custom error handler, if given
        if (errorHandler != null) errorHandler.onError(event);
      }
    });
    
    plotContainer.add(plot);
    
  }
  
  @Override
  public void showWaitIndicator() {
     clearPlot();
     plotContainer.add(spinner);
  }
  
  @Override
  public void hideWaitIndicator() {
    plotContainer.remove(spinner);
  }

  @Override
  public void clearPlot() {
    plotContainer.clear();
  }

  @Override
  public void setCampaignDropDownEnabled(boolean isEnabled) {
    campaignListBox.setEnabled(true);
    setRequiredFlag(campaignListBox, isEnabled);
  }

  @Override
  public void setParticipantDropDownEnabled(boolean isEnabled) {
    participantListBox.setEnabled(isEnabled);
    setRequiredFlag(participantListBox, isEnabled);
  }


  @Override
  public void setPromptXDropDownEnabled(boolean isEnabled) {
    promptXListBox.setEnabled(isEnabled);
    setRequiredFlag(promptXListBox, isEnabled);
  }


  @Override
  public void setPromptYDropDownEnabled(boolean isEnabled) {
    promptYListBox.setEnabled(isEnabled);
    setRequiredFlag(promptYListBox, isEnabled);
  }

  @Override
  public void disableAllDataControls() {
    campaignListBox.setSelectedIndex(-1); // campaigns never change, just deselect
    participantListBox.clear();
    promptXListBox.clear();
    promptYListBox.clear();
    
    // disable control
    campaignListBox.setEnabled(false);
    participantListBox.setEnabled(false);
    promptXListBox.setEnabled(false);
    promptYListBox.setEnabled(false);
    drawPlotButton.setEnabled(false);
    //pdfButton.setEnabled(false);
    exportButton.setEnabled(false);
    
    // remove style name that marks control as required
    clearMissingFieldMarkers();
    for (ListBox listBox : requiredFields) {
      setRequiredFlag(listBox, false);
    }
    
    dataControls.addStyleName(style.disabled());
  }
  
  @Override
  public void setDataButtonsEnabled(boolean isEnabled) {
    dataControls.removeStyleName(style.disabled());
    drawPlotButton.setEnabled(isEnabled);
    //pdfButton.setEnabled(isEnabled);
    exportButton.setEnabled(isEnabled);
  }

  @Override 
  public HasChangeHandlers getCampaignDropDown() {
    return campaignListBox;
  }


  @Override
  public HasClickHandlers getDrawPlotButton() {
    return drawPlotButton;
  }

/*
  @Override
  public HasClickHandlers getPdfButton() {
    return pdfButton;
  }
*/

  @Override
  public HasClickHandlers getExportDataButton() {
    return exportButton;
  }

  @Override
  public SourcesTreeEvents getPlotTypeTree() {
    return plotTypeTree;
  }


  @Override
  public int getPlotPanelWidth() {
    return plotContainer.getElement().getClientWidth();
  }


  @Override
  public int getPlotPanelHeight() {
    return plotContainer.getElement().getClientHeight();
  }


  @Override
  public boolean isMissingRequiredField() { 
    clearMissingFieldMarkers(); // clear any left over from last validation
    boolean atLeastOneFieldIsMissing = false;
    for (ListBox listBox : requiredFields) {
      if (listBox.getSelectedIndex() == -1 && isRequired(listBox)) {
        markMissing(listBox);
        atLeastOneFieldIsMissing = true;
      }
    }
    
    if (atLeastOneFieldIsMissing) {
      requiredFieldMissingMsg.setVisible(true);      
    }
    
    return atLeastOneFieldIsMissing;
  }
  
  @Override
  public void clearMissingFieldMarkers() {
    for (ListBox listBox : requiredFields) {
      clearMissingMarker(listBox);
    }
    requiredFieldMissingMsg.setVisible(false);      
  }
  
  private boolean isRequired(UIObject field) {
    return field.getStyleName().contains(style.requiredField());
  }
  
  private void markMissing(UIObject field) {
    field.addStyleName(style.requiredFieldMissing());
  }
  
  private void clearMissingMarker(UIObject field) {
    field.removeStyleName(style.requiredFieldMissing());
  }
  
  // used for both styling and validation
  private void setRequiredFlag(UIObject field, boolean isRequired) {
    if (isRequired) {
      field.addStyleName(style.requiredField());
    } else {
      field.removeStyleName(style.requiredField());
    }
  }


  @Override
  public void showResponsesOnMap(final List<SurveyResponse> responses) {
    // hide previous plot, if any
    clearPlot(); 

    // add responses to map, attach it to the document to make it visible
    if (mapWidget == null) { // lazy init map, add responses when done
      initMap(new Runnable() {
        @Override
        public void run() {
          setResponsesOnMap(responses);
          hideWaitIndicator();
        }
      });
    } else { // map already initialized
      setResponsesOnMap(responses); 
    }
  }
  
  @Override
  public void renderLeaderBoard(List<UserParticipationInfo> participationInfo) {
    clearPlot();
    Grid leaderBoard = new Grid();
    int numRows = participationInfo.size() + 2; // + 2 for header row at top + totals row at bottom
    int numCols = 4; // username, total, private, shared // FIXME: invisible?
    leaderBoard.resize(numRows, numCols);
    leaderBoard.setText(0, 0, "Username");
    leaderBoard.setText(0, 1, "Total Responses");
    leaderBoard.setText(0, 2, "Private Responses");
    leaderBoard.setText(0, 3, "Shared Responses");
    leaderBoard.getRowFormatter().addStyleName(0, style.leaderBoardHeaderRow());
    int row = 1; // first row is header
    int totalResponsesFromAllUsers = 0;
    int totalPrivateResponsesFromAllUsers = 0;
    int totalSharedResponsesFromAllUsers = 0;
    for (UserParticipationInfo info : participationInfo) {
      // get response counts for this user
      int totalResponseCount = info.getTotalResponseCount();
      int privateResponseCount = info.getResponseCount(Privacy.PRIVATE);
      int sharedResponseCount = info.getResponseCount(Privacy.SHARED);
      // fill in user info row in leader board
      leaderBoard.setText(row, 0, info.getUsername());
      leaderBoard.setText(row, 1, Integer.toString(totalResponseCount));
      leaderBoard.setText(row, 2, Integer.toString(privateResponseCount));
      leaderBoard.setText(row, 3, Integer.toString(sharedResponseCount));
      // add this user's counts to running totals
      totalResponsesFromAllUsers += totalResponseCount;
      totalPrivateResponsesFromAllUsers += privateResponseCount;
      totalSharedResponsesFromAllUsers += sharedResponseCount;
      // increment row
      row++;
    }
    // insert row of totals at the end
    leaderBoard.setText(row, 0, "Total (All Users)");
    leaderBoard.setText(row, 1, Integer.toString(totalResponsesFromAllUsers));
    leaderBoard.setText(row, 2, Integer.toString(totalPrivateResponsesFromAllUsers));
    leaderBoard.setText(row, 3, Integer.toString(totalSharedResponsesFromAllUsers));
    leaderBoard.getRowFormatter().addStyleName(row, style.leaderBoardTotalsRow());
    
    // add widget to the display
    plotContainer.add(leaderBoard);
  }
  
  @Override
  public void setInfoText(String text) {
    // TODO: display info about current plot
  }
  
  private void setResponsesOnMap(List<SurveyResponse> responses) {
    
    // Clear any previous data points    
    clearOverlays();
    
    if (responses == null || responses.isEmpty()) return;
    
    LatLngBounds bounds = LatLngBounds.newInstance();    
    // Add new data points 
    for (SurveyResponse response : responses) {
      if (response.hasLocation()) {
        final LatLng location = LatLng.newInstance(response.getLatitude(), response.getLongitude());
        bounds.extend(location);
        final Marker marker = Marker.newInstance();
        marker.setPosition(location);
        marker.setMap(mapWidget.getMap());
        markerToResponseMap.put(marker, response);
        
        Event.addListener(marker, "click", new EventCallback() {
          @Override
          public void callback() {
            showResponseDetail(marker);
          }
        });
      }
    }    

    // Attach map before calculating zoom level or it might be incorrectly set to 0 (?)
    if (!mapWidget.isAttached()) plotContainer.add(mapWidget);
    
    // Zoom and center the map to the new bounds
    mapWidget.getMap().fitBounds(bounds); 
  }
  
  /**
   * Clears the markers one by one from the map.
   */
  private void clearOverlays() {
    for (final Marker marker: markerToResponseMap.keySet()) {
      // Remove from map
      marker.setMap(null);
      // Remove the event listener
      Event.clearInstanceListeners(marker);
    }
    markerToResponseMap.clear();
  }
  
  private void initMap(final Runnable actionToTakeWhenDone) {
    final MapOptions options = new MapOptions();
    options.setMapTypeControl(true);
    options.setZoom(8);
    options.setCenter(LatLng.newInstance(39.509, -98.434));
    options.setMapTypeId(new MapTypeId().getRoadmap());
    options.setDraggable(true);
    options.setScaleControl(true);
    options.setNavigationControl(true);
    options.setScrollwheel(true);
    mapWidget = new MapWidget(options);
    mapWidget.setSize("100%", "100%");
    
    // Close the info window when clicking anywhere
    Event.addListener(mapWidget.getMap(), "click", new EventCallback() {
      @Override
      public void callback() {
        closeInfoWindow();
      }
    });
    
    // Close the info window when clicking close
    Event.addListener(infoWindow, "closeclick", new EventCallback() {
      @Override
      public void callback() {
        closeInfoWindow();
      }
    });
      
    if (actionToTakeWhenDone != null) actionToTakeWhenDone.run();
  }
  
  @Override
  public void showResponseDetail(Marker location) {
    if (markerToResponseMap.containsKey(location)) {
      SurveyResponse response = markerToResponseMap.get(location);
      final ResponseWidgetPopup displayWidget = new ResponseWidgetPopup();
      displayWidget.setResponse(response, new ResponseWidgetPopup.ElementHandlerCallback() {
        @Override
        public void addingElement(com.google.gwt.user.client.Element element,
            final String url) {
          // Save the event listener for later removal
          clickHandlers.add(
            Event.addDomListener(element, "click", new EventCallback() {
              // Pop open a new window when an element is clicked
              @Override
              public void callback() {
                Window.open(url, "_blank", "");
              }
            })
          );
        }
      });
      
      infoWindow.setContent(displayWidget.getElement());
      infoWindow.open(mapWidget.getMap(), location);
    }
  }
  
  /**
   * Cleans up all the event listeners and closes the info window.
   */
  private void closeInfoWindow() {
    for (final HasMapsEventListener event : clickHandlers) {
      Event.removeListener(event);
    }
    clickHandlers.clear();
    infoWindow.close();
  } 

  @Override
  public void doExportCsvFormPost(String url, Map<String, String> params) {
    FormPanel exportForm = new FormPanel("_blank"); // must be _blank for firefox
    exportForm.setAction(url);
    exportForm.setMethod(FormPanel.METHOD_POST);
    FlowPanel innerContainer = new FlowPanel();    
    for (String paramName : params.keySet()) {
      Hidden field = new Hidden();
      field.setName(paramName);
      field.setValue(params.get(paramName));
      innerContainer.add(field);
    }
    exportForm.add(innerContainer);
    hiddenFormContainer.add(exportForm, "innerHiddenFormContainer");
    exportForm.submit();
    exportForm.removeFromParent();
  }


  @Override
  public void clearPromptXList() {
    this.promptXListBox.clear();
  }


  @Override
  public void addPromptX(String promptId, String displayString, boolean isSupported) {
    this.promptXListBox.addItem(displayString, promptId);
    if (!isSupported) { // unsupported prompts are disabled. // FIXME: IE?
      int itemIndex = this.promptXListBox.getItemCount() - 1;
      NodeList<Element> items = this.promptXListBox.getElement().getElementsByTagName("option");
      items.getItem(itemIndex).setAttribute("disabled", "disabled");
    }
  }


  @Override
  public void clearPromptYList() {
    this.promptYListBox.clear();
  }


  @Override
  public void addPromptY(String promptId, String displayString,
      boolean isSupported) {
    this.promptYListBox.addItem(displayString, promptId);
    if (!isSupported) { // unsupported prompts are disabled. // FIXME: IE?
      int itemIndex = this.promptYListBox.getItemCount() - 1;
      NodeList<Element> items = this.promptYListBox.getElement().getElementsByTagName("option");
      items.getItem(itemIndex).setAttribute("disabled", "disabled");
    }    
  }
	
	@Override
	public void selectFromDate(Date fromDate) {
		fromDateBox.setValue(fromDate);
	}
	
	@Override
	public Date getFromDate() {
		return this.fromDateBox.getValue();
	}
	
	@Override
	public void selectToDate(Date toDate) {
		toDateBox.setValue(toDate);
	}
	
	@Override
	public Date getToDate() {
		return this.toDateBox.getValue();
	}
}
