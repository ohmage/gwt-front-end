/**
 * 
 */
package edu.ucla.cens.mobilize.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.RoleCampaign;
import edu.ucla.cens.mobilize.client.common.RunningState;

/**
 * @author vhajdik
 *
 */
public class DashboardViewImpl extends Composite implements DashboardView {

  private static DashboardUiBinder uiBinder = GWT
      .create(DashboardUiBinder.class);

  @UiTemplate("DashboardView.ui.xml")
  interface DashboardUiBinder extends UiBinder<Widget, DashboardViewImpl> {
  }

  @UiField HTMLPanel notificationResponses;
  @UiField HTMLPanel notificationParticipant;
  @UiField HTMLPanel notificationAuthor;
  @UiField SpanElement privateResponseCount;
  @UiField SpanElement campaignParticipantRoleCount;
  @UiField SpanElement campaignAuthorRoleCount;
  @UiField InlineHyperlink privateResponsesLink;
  @UiField InlineHyperlink participantCampaignsLink;
  @UiField InlineHyperlink authorCampaignsLink;
  @UiField Hyperlink quickLinkCreate;
  @UiField Hyperlink quickLinkEdit;
  @UiField Hyperlink quickLinkBrowse;
  @UiField Hyperlink quickLinkResponses;
  @UiField Hyperlink quickLinkUpload;
  
  boolean canEdit = false;
  boolean canUpload = false;  
  

  public DashboardViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
    initComponents();
  }
  
  private void initComponents() {
    this.quickLinkBrowse.setTargetHistoryToken(HistoryTokens.campaignList());
    this.quickLinkCreate.setTargetHistoryToken(HistoryTokens.campaignCreate());
    this.quickLinkEdit.setTargetHistoryToken(HistoryTokens.campaignList());
    this.quickLinkResponses.setTargetHistoryToken(HistoryTokens.responseList());
    this.quickLinkUpload.setTargetHistoryToken(HistoryTokens.documentCreate());
  }
  
  private void updateRoleSpecificDisplay() {
    this.quickLinkCreate.setVisible(this.canEdit);
    this.quickLinkEdit.setVisible(this.canEdit);
    this.quickLinkUpload.setVisible(this.canUpload);
    this.privateResponsesLink.setTargetHistoryToken(HistoryTokens.responseList("edit", null, null, null, Privacy.PRIVATE, false, null, null, null, null));
    this.authorCampaignsLink.setTargetHistoryToken(HistoryTokens.campaignList(RunningState.RUNNING, RoleCampaign.AUTHOR, null, null));
    this.participantCampaignsLink.setTargetHistoryToken(HistoryTokens.campaignList(RunningState.RUNNING, RoleCampaign.PARTICIPANT, null, null));
  }
  
  @Override
  public void setPermissions(boolean canEdit, boolean canUpload) {
    this.canEdit = canEdit;
    this.canUpload = canUpload;
    updateRoleSpecificDisplay();
  }

  @Override
  public void setNumUnreadSurveyResponses(int num) {
    this.privateResponseCount.setInnerText(Integer.toString(num));    
  }

  @Override
  public void setNumActiveParticipantCampaigns(int num) {
    this.campaignParticipantRoleCount.setInnerText(Integer.toString(num));    
  }

  @Override
  public void setNumActiveAuthorCampaigns(int num) {
    this.campaignAuthorRoleCount.setInnerText(Integer.toString(num));
  }

  @Override
  public void showParticipantRoleCount(int count) {
    this.notificationParticipant.setVisible(true);
    this.campaignParticipantRoleCount.setInnerText(Integer.toString(count));
  }

  @Override
  public void showAuthorRoleCount(int count) {
    this.notificationAuthor.setVisible(true);
    this.campaignAuthorRoleCount.setInnerText(Integer.toString(count));    
  }

  @Override
  public void showPrivateResponseCount(int count) {
    this.notificationResponses.setVisible(true);
    this.privateResponseCount.setInnerText(Integer.toString(count));
  }

  @Override
  public void hideParticipantRoleCount() {
    this.notificationParticipant.setVisible(false);
  }

  @Override
  public void hideAuthorRoleCount() {
    this.notificationAuthor.setVisible(false);
  }

  @Override
  public void hidePrivateResponseCount() {
    this.notificationResponses.setVisible(false);
  }

  
}

