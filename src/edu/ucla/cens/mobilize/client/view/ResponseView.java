package edu.ucla.cens.mobilize.client.view;

import java.util.List;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.model.*;

public interface ResponseView extends IsWidget {
  
  interface Presenter {
    
    void onFilterChange();
    void setView(ResponseView view);
  }

  void setPresenter(Presenter presenter);
  
  // messaging
  void showInfoMessage(String info);
  void showErrorMessage(String error);
  void showConfirmDelete(ClickHandler onConfirmDelete);
  
  // load values in filters
  void setParticipantList(List<String> participantNames);
  void setCampaignList(List<String> campaignNames);
  void setSurveyList(List<String> surveyNames);
  
  // set selected filters
  void selectParticipant(String participant);
  void selectCampaign(String campaign);
  void selectSurvey(String survey);

  // get selected filters
  String getSelectedParticipant();
  String getSelectedCampaign();
  String getSelectedSurvey();
  Privacy getSelectedPrivacyState();
  
  // display
  void renderPrivate(List<SurveyResponse> responses);
  void renderShared(List<SurveyResponse> responses);
  void renderInvisible(List<SurveyResponse> responses);
  void renderAll(List<SurveyResponse> responses);
  void clearResponseList();
  void markShared(int responseKey);
  void markPrivate(int responseKey);
  void removeResponse(int responseKey);
  
  // gui elements needed by presenter for event handling
  List<HasClickHandlers> getShareButtons();
  List<HasClickHandlers> getMakePrivateButtons();
  List<HasClickHandlers> getDeleteButtons();
  List<HasValueChangeHandlers<String>> getFilters();
  List<String> getSelectedSurveyResponseKeys();
  
}
