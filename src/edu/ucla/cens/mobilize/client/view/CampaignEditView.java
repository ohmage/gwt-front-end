package edu.ucla.cens.mobilize.client.view;

import java.util.List;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.IsWidget;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.RunningState;

public interface CampaignEditView extends IsWidget {
  
  // getters for form elements that need event handlers
  HasClickHandlers getSaveButton();
  HasClickHandlers getCancelButton();
  HasValueChangeHandlers getClassList();
  HasValueChangeHandlers getAuthorList();
  
  // form field getters
  String getCampaignUrn();
  String getDescription();
  List<String> getClassUrns();
  List<String> getAuthorIds();
  String getXmlFileName();
  Privacy getPrivacy();
  RunningState getRunningState();
  
  // setters
  void setCampaignUrn(String urn);
  void setDescription(String description);
  void setClassUrns(List<String> urns);
  void setAuthorIds(List<String> authorIds);
  void setPrivacy(Privacy privacy);
  void setRunningState(RunningState runningState);
  
  // list manipulation
  void addClassToList(String classUrn, String className);
  void removeClassFromList(String classUrn);
  void clearClassList();
  void addAuthorToList(String authorId);
  void removeAuthorFromList(String authorId);
  void clearAuthorList();
  
  // form management
  void initializeForm(String targetUrl, String authToken, String campaignUrn);
  void addSubmitHandler(SubmitHandler onSubmit);
  void addSubmitCompleteHandler(SubmitCompleteHandler onSubmitComplete);
  
}
