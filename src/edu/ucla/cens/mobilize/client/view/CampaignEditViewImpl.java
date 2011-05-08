package edu.ucla.cens.mobilize.client.view;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.RunningState;

public class CampaignEditViewImpl extends Composite implements CampaignEditView {

  private static CampaignEditViewUiBinder uiBinder = GWT
      .create(CampaignEditViewUiBinder.class);

  @UiTemplate("CampaignEditView.ui.xml")
  interface CampaignEditViewUiBinder extends UiBinder<Widget, CampaignEditViewImpl> {
  }

  public CampaignEditViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public HasClickHandlers getSaveButton() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public HasClickHandlers getCancelButton() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public HasValueChangeHandlers getClassList() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public HasValueChangeHandlers getAuthorList() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getCampaignUrn() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<String> getClassUrns() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<String> getAuthorIds() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getXmlFileName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Privacy getPrivacy() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public RunningState getRunningState() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setCampaignUrn(String urn) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setDescription(String description) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setClassUrns(List<String> urns) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setAuthorIds(List<String> authorIds) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setPrivacy(Privacy privacy) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setRunningState(RunningState runningState) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void addClassToList(String classUrn, String className) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void removeClassFromList(String classUrn) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void clearClassList() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void addAuthorToList(String authorId) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void removeAuthorFromList(String authorId) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void clearAuthorList() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void initializeForm(String targetUrl, String authToken,
      String campaignUrn) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void addSubmitHandler(SubmitHandler onSubmit) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void addSubmitCompleteHandler(SubmitCompleteHandler onSubmitComplete) {
    // TODO Auto-generated method stub
    
  }

}
