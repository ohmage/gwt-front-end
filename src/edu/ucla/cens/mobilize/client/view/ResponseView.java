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
import com.google.gwt.view.client.HasRows;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.model.*;

public interface ResponseView extends IsWidget, HasRows {
  
  interface Presenter {
    void setView(ResponseView view);
  }

  void setPresenter(Presenter presenter);
  
  // messaging
  void showInfoMessage(String info);
  void addErrorMessage(String error, String detail);
  void clearErrorMessages();
  void showConfirmDelete(ClickHandler onConfirmDelete);
  void showWaitIndicator();
  void hideWaitIndicator();
  void showNoPhotoResponsesMessage();
  
  // navigation
  Subview getSelectedSubview();
  void setSelectedSubview(Subview subview);
  void setEditMenuItemVisible(boolean isVisible);
  
  // enabled/disable filters
  void showAllFilters();
  void hideOptionalFilters();
  void setPhotoResponsesCheckBoxEnabled(boolean isEnabled);
  
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
  String getSelectedCampaignName();
  String getSelectedSurvey();
  Privacy getSelectedPrivacyState();
  Date getSelectedStartDate();
  Date getSelectedEndDate();
  boolean getOnlyPhotoResponsesFlag();
  
  // response management
  List<String> getSelectedSurveyResponseKeys();
  void clearSelectedSurveyResponseKeys();
  
  // display
  void renderResponses(List<SurveyResponse> responses);
  void clearResponseList();
  void markShared(String responseKey);
  void markPrivate(String responseKey);
  void removeResponse(String responseKey);
  void enableSurveyFilter();
  void disableSurveyFilter();
  void enableParticipantFilter();
  void disableParticipantFilter();
  void enableShowResponsesButton();
  void disableShowResponsesButton();
  void setSectionHeader(String headerText);
  void setSectionHeaderDetail(String detailText);
  void showResponseCountInSectionHeader(String username, int count);
  
  // paging
  int getVisibleRangeStart();
  void setVisibleRangeStart(int start); // no events fired
  int getSelectedPageSize();
  void setSelectedPageSize(int pageSize);
  
  // gui elements needed by presenter for event handling
  HasClickHandlers getViewLinkBrowse();
  HasClickHandlers getViewLinkEdit();
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

  public enum Subview { 
    BROWSE, EDIT;
    public String toHistoryTokenString() {
      return this.toString().toLowerCase(); 
    }
    public static Subview fromHistoryTokenString(String view) {
      Subview retval = null;
      try { retval = Subview.valueOf(view.toUpperCase()); } catch (Exception e) {}
      return retval; // null if unrecognized
    }
  }


}
