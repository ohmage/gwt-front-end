package edu.ucla.cens.mobilize.client.view;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.model.*;

public interface ResponseView extends IsWidget {
  
  interface Presenter {
    void setView(ResponseView view);
  }

  void setPresenter(Presenter presenter);
  
  // messaging
  void showInfoMessage(String info);
  void addErrorMessage(String error, String detail);
  void clearErrorMessages();
  void showConfirmDelete(ClickHandler onConfirmDelete);
  
  // navigation
  String getSelectedSubView();
  void setSelectedSubView(String subView);
  
  
  // load values in filters
  void setParticipantList(SortedSet<String> participantNames, boolean makeFirstItemAll);
  void setCampaignList(Map<String, String> campaignIdToNameMap);
  void setSurveyList(List<String> surveyNames);
  void setPrivacyStates(List<Privacy> privacyStates);
  void clearSurveyList();
  void clearParticipantList();
  
  // set selected filters
  void selectParticipant(String participant);
  void selectCampaign(String campaign);
  void selectSurvey(String survey);
  void selectPrivacyState(Privacy privacy);
  void selectStartDate(Date fromDate);
  void selectEndDate(Date toDate);
  void setPhotoFilter(boolean showOnlyResponsesWithPhotos);

  // get selected filters
  String getSelectedParticipant();
  String getSelectedCampaign();
  String getSelectedSurvey();
  Privacy getSelectedPrivacyState();
  Date getSelectedStartDate();
  Date getSelectedEndDate();
  boolean getHasPhotoToggleValue();
  
  // response management
  List<String> getSelectedSurveyResponseKeys();
  void clearSelectedSurveyResponseKeys();
  
  // display
  void renderResponses(List<SurveyResponse> responses);
  void clearResponseList();
  void markShared(int responseKey);
  void markPrivate(int responseKey);
  void removeResponse(int responseKey);
  void enableSurveyFilter();
  void disableSurveyFilter();
  void setSectionHeader(String headerText);
  
  // gui elements needed by presenter for event handling
  HasClickHandlers getViewLinkQuick();
  HasClickHandlers getViewLinkFull();
  HasClickHandlers getViewLinkPhoto();
  List<HasClickHandlers> getShareButtons();
  List<HasClickHandlers> getMakePrivateButtons();
  List<HasClickHandlers> getDeleteButtons();
  HasClickHandlers getApplyFiltersButton();
  HasChangeHandlers getCampaignFilter();
  HasChangeHandlers getSurveyFilter();
  HasChangeHandlers getParticipantFilter();
  HasChangeHandlers getPrivacyFilter();
  HasValueChangeHandlers<Date> getStartDateFilter();
  HasValueChangeHandlers<Date> getEndDateFilter();
  
}
