package edu.ucla.cens.mobilize.client.view;


import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SourcesTreeEvents;

import edu.ucla.cens.mobilize.client.common.PlotType;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.model.UserParticipationInfo;

public interface ExploreDataView extends IsWidget {
  
  // campaign drop down
  void setCampaignList(Map<String, String> campaignIdToNameMap);
  void setSelectedCampaign(String campaignId);
  String getSelectedCampaign(); // returns id

  // participant drop down
  void setParticipantList(List<String> participants);
  void setSelectedParticipant(String participantName);
  String getSelectedParticipant();
  
  // prompt x
  void clearPromptXList();
  void addPromptX(String promptId, String displayString, boolean isSupported);
  void setSelectedPromptX(String prompt);
  String getSelectedPromptX(); // returns prompt_id
  
  // prompt y
  void clearPromptYList();
  void addPromptY(String promptId, String displayString, boolean isSupported);
  void setSelectedPromptY(String prompt);
  String getSelectedPromptY(); // returns prompt_id

	// from date
	void selectFromDate(Date fromDate);
	Date getFromDate();
	
	// to date
	void selectToDate(Date toDate);
	Date getToDate();

  // plot type tree
  PlotType getSelectedPlotType();
  void setSelectedPlotType(PlotType plotType);
  
  // TODO: add start/end dates
  
  int getPlotPanelWidth();
  int getPlotPanelHeight();
  
  void setPlotUrl(String url);
  void setPlotUrl(String url, ErrorHandler errorHandler);
  void clearPlot();
  // FIXME: what if there are multiple responses from the same location?
  void showResponsesOnMap(List<SurveyResponse> responses);
  void showResponseDetail(Marker location);
  void renderLeaderBoard(List<UserParticipationInfo> participationInfo);
  void setInfoText(String string);
  
  // methods for enabling/disabling form fields
  void setCampaignDropDownEnabled(boolean isEnabled);
  void setParticipantDropDownEnabled(boolean isEnabled);
  void setPromptXDropDownEnabled(boolean isEnabled);
  void setPromptYDropDownEnabled(boolean isEnabled);
	void setDateRangeEnabled(boolean isEnabled);
	void setStartDateRangeEnabled(boolean isEnabled);
	void setEndDateRangeEnabled(boolean isEnabled);
  void setDataButtonsEnabled(boolean isEnabled);
  void disableAllDataControls();
  
  void showWaitIndicator();
  void hideWaitIndicator();
  
  // methods for event handling
  @SuppressWarnings("deprecation")
  SourcesTreeEvents getPlotTypeTree();
  HasChangeHandlers getCampaignDropDown();
  HasClickHandlers getDrawPlotButton();
  //HasClickHandlers getPdfButton();
  HasClickHandlers getExportDataButton();
  
  
  // validation helpers
  boolean isMissingRequiredField();
  void clearMissingFieldMarkers();
  
  void doExportCsvFormPost(String url, Map<String, String> params);

}
