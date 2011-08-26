package edu.ucla.cens.mobilize.client.view;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
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

import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.overlay.Marker;

import edu.ucla.cens.mobilize.client.AwConstants;
import edu.ucla.cens.mobilize.client.common.PlotType;
import edu.ucla.cens.mobilize.client.model.AppConfig;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.ui.ResponseWidgetPopup;
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
  @UiField Button drawPlotButton;
  //@UiField Button pdfButton;
  @UiField Button exportButton;
  @UiField HTMLPanel hiddenFormContainer;
  @UiField FlowPanel plotContainer;
  
  private List<ListBox> requiredFields = new ArrayList<ListBox>();
  private MapWidget mapWidget;
  private Map<LatLng, SurveyResponse> locationToResponseMap = new HashMap<LatLng,SurveyResponse>();
  private Image spinner; 
  
  public ExploreDataViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
    loadPlotTypeTree();
    
    // make data filter panel stick to the bottom of the page
    sideBar.setCellVerticalAlignment(sideBar.getWidget(1), VerticalPanel.ALIGN_BOTTOM);

    // these are required when enabled
    requiredFields = Arrays.asList(campaignListBox, participantListBox, promptXListBox, promptYListBox);
    
    // set up image to use as wait indicator
    spinner = new Image();
    spinner.setStyleName(style.waiting());
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
    
    // response count 
    TreeItem surveyResponseCounts = getTreeItem("Survey Response Counts", style.treeItemCategory()); // category
    TreeItem totalResponses = AppConfig.exportAndVisualizeSharedResponsesOnly() ?  
      getTreeItem("Shared Responses", PlotType.SURVEY_RESPONSE_COUNT, style.treeItemPlotType()) :
      getTreeItem("Total Responses", PlotType.SURVEY_RESPONSE_COUNT, style.treeItemPlotType());
    TreeItem responsesByPrivacy = getTreeItem("Responses By Privacy", PlotType.SURVEY_RESPONSES_PRIVACY_STATE, style.treeItemPlotType());
    TreeItem responseTimeseries = getTreeItem("Response Timeseries", PlotType.SURVEY_RESPONSES_PRIVACY_STATE_TIME, style.treeItemPlotType());
    
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
  
  private void setResponsesOnMap(List<SurveyResponse> responses) {
    
    // Clear any previous data points
    mapWidget.clearOverlays();
    locationToResponseMap.clear();
    
    if (responses == null || responses.isEmpty()) return;
    
    LatLngBounds bounds = LatLngBounds.newInstance();
    
    // Add new data points 
    for (SurveyResponse response : responses) {
      if (response.hasLocation()) {
        LatLng location = LatLng.newInstance(response.getLatitude(), response.getLongitude());
        locationToResponseMap.put(location, response);
        mapWidget.addOverlay(new Marker(location));
        bounds.extend(location);
      }
    }    

    // Attach map before calculating zoom level or it might be incorrectly set to 0 (?)
    if (!mapWidget.isAttached()) plotContainer.add(mapWidget);
    
    // Zoom and center the map to the new bounds
    mapWidget.setCenter(bounds.getCenter());
    mapWidget.setZoomLevel(mapWidget.getBoundsZoomLevel(bounds));
    
  }
  
  
  private void initMap(final Runnable actionToTakeWhenDone) {
    Maps.loadMapsApi(AwConstants.getGoogleMapsApiKey(), "2", false, new Runnable() {
      public void run() {
        mapWidget = new MapWidget();
        mapWidget.setSize("100%", "100%");
        mapWidget.addControl(new LargeMapControl());
        mapWidget.setScrollWheelZoomEnabled(true);
        
        // if user clicks on a marker, show details for the response at that location
        mapWidget.addMapClickHandler(new MapClickHandler() {
          @Override
          public void onClick(MapClickEvent event) {
            if (event.getOverlay() != null && !event.getOverlay().equals(mapWidget.getInfoWindow())) {
              showResponseDetail(event.getOverlayLatLng());
            }
          } 
        }); 
        
        // map is initialized. now run the code
        if (actionToTakeWhenDone != null) actionToTakeWhenDone.run();
      } // end public void run()
    }); // end Maps.loadMapsApi
  }
  
  @Override
  public void showResponseDetail(LatLng location) {
    if (locationToResponseMap.containsKey(location)) {
      SurveyResponse response = locationToResponseMap.get(location);
      ResponseWidgetPopup displayWidget = new ResponseWidgetPopup();
      displayWidget.setResponse(response);
      InfoWindowContent content = new InfoWindowContent(displayWidget);
      mapWidget.getInfoWindow().open(location, content);
    }
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
    
}
