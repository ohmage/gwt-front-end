package edu.ucla.cens.mobilize.client.view;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SourcesTreeEvents;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.PlotType;

@SuppressWarnings("deprecation")
public class ExploreDataViewImpl extends Composite implements ExploreDataView {

  private static ExploreDataViewUiBinder uiBinder = GWT
      .create(ExploreDataViewUiBinder.class);

  @UiTemplate("ExploreDataView.ui.xml")
  interface ExploreDataViewUiBinder extends UiBinder<Widget, ExploreDataViewImpl> {
  }

  public interface ExploreDataStyles extends CssResource {
    String treeItemCategory();
    String treeItemPlotType();
    String treeItemMap();
    String treeItemHist();
    String treeItemTimeseries();
    String treeItemTable();
    String waiting();
  }
  
  @UiField ExploreDataStyles style;
  @UiField Tree plotTypeTree;
  @UiField VerticalPanel rightSideBar;
  @UiField DockLayoutPanel layoutPanel;
  @UiField FlowPanel plotContainer;
  @UiField ListBox campaignListBox;
  @UiField ListBox participantListBox;
  @UiField ListBox promptXListBox;
  @UiField ListBox promptYListBox;
  @UiField Button drawPlotButton;
  @UiField Button pdfButton;
  @UiField Button exportButton;
  
  public ExploreDataViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
    loadPlotTypeTree();
    
    // make data filter panel stick to the bottom of the page
    rightSideBar.setCellVerticalAlignment(rightSideBar.getWidget(1), VerticalPanel.ALIGN_BOTTOM);

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

    // response count 
    TreeItem responseCount = getTreeItem("ResponseCount", style.treeItemCategory()); // category
    TreeItem surveyResponseCount = getTreeItem("Survey Response Count", PlotType.SURVEY_RESPONSE_COUNT, style.treeItemPlotType());
    
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
    plotTypeTree.addItem(responseCount);
    plotTypeTree.addItem(univariate);
    plotTypeTree.addItem(multivariate);
    plotTypeTree.addItem(geographic);
    responseCount.addItem(surveyResponseCount);
    univariate.addItem(userTimeseries);
    univariate.addItem(promptTimeseries);
    univariate.addItem(promptDistribution);
    multivariate.addItem(scatterplot);
    multivariate.addItem(density);
    geographic.addItem(googleMap);
  }

  @Override
  public void setCampaignList(Map<String, String> campaignIdToNameMap) {
    campaignListBox.clear();
    if (campaignIdToNameMap == null) return;
    for (String campaignId : campaignIdToNameMap.keySet()) {
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
    int index = participantListBox.getSelectedIndex();
    return (index > -1) ? participantListBox.getValue(index) : null;
  }


  @Override
  public void setPromptXList(Map<String, String> promptIdToNameMap) {
    promptXListBox.clear();
    if (promptIdToNameMap == null) return;
    for (String promptId : promptIdToNameMap.keySet()) {
      promptXListBox.addItem(promptIdToNameMap.get(promptId), promptId);
    }
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
    int index = promptXListBox.getSelectedIndex();
    return (index > -1) ? promptXListBox.getValue(index) : null;
  }


  @Override
  public void setPromptYList(Map<String, String> promptIdToNameMap) {
    promptYListBox.clear();
    if (promptIdToNameMap == null) return;
    for (String promptId : promptIdToNameMap.keySet()) {
      promptYListBox.addItem(promptId, promptIdToNameMap.get(promptId));
    }    
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
    clearPlot();
    final Image loading = new Image();
    loading.setStyleName(style.waiting());
    plotContainer.add(loading);
    Image plot = new Image(url);
    plot.addLoadHandler(new LoadHandler() {
      @Override
      public void onLoad(LoadEvent event) {
        plotContainer.remove(loading);
      }
    });
    plot.addErrorHandler(new ErrorHandler() {
      @Override
      public void onError(ErrorEvent event) {
        plotContainer.remove(loading);
      }
    });
    plotContainer.add(plot);
    
  }


  @Override
  public void clearPlot() {
    plotContainer.clear();
  }

  @Override
  public void setCampaignDropDownEnabled(boolean isEnabled) {
    campaignListBox.setEnabled(true);
  }

  @Override
  public void setParticipantDropDownEnabled(boolean isEnabled) {
    participantListBox.setEnabled(isEnabled);
  }


  @Override
  public void setPromptXDropDownEnabled(boolean isEnabled) {
    promptXListBox.setEnabled(isEnabled);
  }


  @Override
  public void setPromptYDropDownEnabled(boolean isEnabled) {
    promptYListBox.setEnabled(isEnabled);    
  }

  @Override
  public void disableAllDataControls() {
    campaignListBox.clear();
    participantListBox.clear();
    promptXListBox.clear();
    promptYListBox.clear();
    campaignListBox.setEnabled(false);
    participantListBox.setEnabled(false);
    promptXListBox.setEnabled(false);
    promptYListBox.setEnabled(false);
    drawPlotButton.setEnabled(false);
    pdfButton.setEnabled(false);
    exportButton.setEnabled(false);
  }
  
  @Override
  public void setDataButtonsEnabled(boolean isEnabled) {
    drawPlotButton.setEnabled(isEnabled);
    pdfButton.setEnabled(isEnabled);
    exportButton.setEnabled(isEnabled);
  }
  
  @Override
  public HasClickHandlers getCampaignDropDown() {
    return campaignListBox;
  }


  @Override
  public HasClickHandlers getParticipantDropDown() {
    return participantListBox;
  }


  @Override
  public HasClickHandlers getPromptXDropDown() {
    return promptXListBox;
  }


  @Override
  public HasClickHandlers getPromptYDropDown() {
    return promptYListBox;
  }


  @Override
  public HasClickHandlers getDrawPlotButton() {
    return drawPlotButton;
  }


  @Override
  public HasClickHandlers getPdfButton() {
    return pdfButton;
  }


  @Override
  public HasClickHandlers getExportDataButton() {
    return exportButton;
  }

  @SuppressWarnings("deprecation")
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
  
}
