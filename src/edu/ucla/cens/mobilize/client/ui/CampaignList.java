package edu.ucla.cens.mobilize.client.ui;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.RunningState;
import edu.ucla.cens.mobilize.client.common.UserRole;
import edu.ucla.cens.mobilize.client.model.CampaignConciseInfo;

public class CampaignList extends Composite {

  // expose css styles from the uibinder template
  public interface CampaignListStyle extends CssResource {
    String campaignGrid();
    String campaignGridNameColumn();
    String campaignGridHeader();
    String campaignGridRunningStateHeader();
    String campaignGridPrivacyHeader();
    String oddRow();
    String privacyShared();
    String privacyPrivate();
    String running();
    String stopped();
    // action links
    String analyzeLink();
    String deleteLink();
    String detailsLink();
    String editLink();
  }
  
  private static CampaignListWidgetUiBinder uiBinder = GWT
      .create(CampaignListWidgetUiBinder.class);

  interface CampaignListWidgetUiBinder extends
      UiBinder<Widget, CampaignList> {
  }
  
  Logger _logger = Logger.getLogger(CampaignList.class.getName());
  int maxCampaigns = 100;
  String[] rowIndexToCampaignId = new String[maxCampaigns];
  
  // declare uibinder fields
  @UiField ListBox stateListBox;
  @UiField ListBox userRoleListBox;
  @UiField DateBox fromDateBox;
  @UiField DateBox toDateBox;
  @UiField Grid campaignGrid;
  @UiField CampaignListStyle style;
  
  // table columns (id column is invisible)
  private enum Column { NAME, RUNNING_STATE, PRIVACY, ACTIONS };
  
  public CampaignList() {
    initWidget(uiBinder.createAndBindUi(this));
    initComponents();
  }
  
  private void initComponents() {
    // whether campaign is running
    stateListBox.addItem("Any");
    stateListBox.addItem("Running");
    stateListBox.addItem("Stopped");

    // current user's role in the campaign
    // FIXME: only show roles from user's roles list
    userRoleListBox.addItem("Any");
    userRoleListBox.addItem(UserRole.ANALYST.toString());
    userRoleListBox.addItem(UserRole.PARTICIPANT.toString());
    userRoleListBox.addItem(UserRole.AUTHOR.toString());
    userRoleListBox.addItem(UserRole.SUPERVISOR.toString());
    userRoleListBox.setSelectedIndex(0);
    
    // start and end dates
    @SuppressWarnings("deprecation")
    DateBox.Format fmt = new DateBox.DefaultFormat(DateTimeFormat.getShortDateFormat());
    fromDateBox.setFormat(fmt);
    toDateBox.setFormat(fmt);
    
    // set grid size. rows = # campaigns (guess) + 1 for header, cols comes from enum
    int maxCampaignsGuess = 20; // more row are added in addCampaign if needed
    campaignGrid.resize(maxCampaignsGuess + 1, Column.values().length); 
    
    // set up table heading
    campaignGrid.getRowFormatter().setStyleName(0, style.campaignGridHeader());
    campaignGrid.setText(0, Column.NAME.ordinal(), "Campaign Name");
    campaignGrid.setText(0, Column.RUNNING_STATE.ordinal(), "Running State");
    campaignGrid.setText(0, Column.PRIVACY.ordinal(), "Privacy");
    campaignGrid.setText(0, Column.ACTIONS.ordinal(), "Actions");
    
    // css styles
    campaignGrid.addStyleName(style.campaignGrid());
    campaignGrid.setCellSpacing(0);
    campaignGrid.setCellPadding(4);
    campaignGrid.getCellFormatter().setStyleName(0, 
                                                 Column.NAME.ordinal(), 
                                                 style.campaignGridNameColumn());
    campaignGrid.getCellFormatter().setStyleName(0, 
                                                 Column.RUNNING_STATE.ordinal(), 
                                                 style.campaignGridRunningStateHeader());
    campaignGrid.getCellFormatter().setStyleName(0, 
                                                 Column.PRIVACY.ordinal(), 
                                                 style.campaignGridPrivacyHeader());
  }
  
  /**
   * renders a list of campaigns in a table
   * @param campaigns
   */
  public void setCampaigns(List<CampaignConciseInfo> campaigns) {
    if (this.campaignGrid.getRowCount() < (campaigns.size() + 1)) {
      this.campaignGrid.resizeRows(campaigns.size() + 1); // one extra row for header
    }
    
    int row = 1; // 0th row is header
    for (CampaignConciseInfo campaignInfo : campaigns) {
      addCampaign(row++, campaignInfo);
    }
  }
  
  private void addCampaign(int row, CampaignConciseInfo campaignInfo) {
    // stripe odd rows
    if (row % 2 != 0) {
      this.campaignGrid.getRowFormatter().addStyleName(row, style.oddRow());
    }
    
    // campaign name links to campaign detail page
    Hyperlink campaignDetailLink = 
      new Hyperlink(campaignInfo.getCampaignName(), 
                    HistoryTokens.campaignDetail(campaignInfo.getCampaignId()));
    this.campaignGrid.setWidget(row, Column.NAME.ordinal(), campaignDetailLink); 
    this.campaignGrid.getCellFormatter().setStyleName(row, 
                                                      Column.NAME.ordinal(), 
                                                      style.campaignGridNameColumn());
    
    // running state 
    RunningState state = campaignInfo.getRunningState();
    this.campaignGrid.setText(row, Column.RUNNING_STATE.ordinal(), state.toString());
    this.campaignGrid.getCellFormatter().setStyleName(row, 
                                                      Column.RUNNING_STATE.ordinal(), 
                                                      getRunningStateStyle(state));
    
    // privacy column
    Privacy privacy = campaignInfo.getPrivacy();
    this.campaignGrid.setText(row, Column.PRIVACY.ordinal(), privacy.toString());
    this.campaignGrid.getCellFormatter().setStyleName(row, 
                                                      Column.PRIVACY.ordinal(), 
                                                      getPrivacyStyle(privacy));


    // actions column
    this.campaignGrid.setWidget(row, Column.ACTIONS.ordinal(), getActionsWidget(campaignInfo));
    
    
  }
  
  private Widget getActionsWidget(CampaignConciseInfo campaign) {
    Panel panel = new FlowPanel();
    String campaignId = campaign.getCampaignId();
    if (campaign.userCanViewDetails()) {
      InlineHyperlink detailsLink = 
        new InlineHyperlink("view", HistoryTokens.campaignDetail(campaignId));
      detailsLink.setStyleName(style.detailsLink());
      panel.add(detailsLink);
    }
    if (campaign.userCanAnalyze()) {
      InlineHyperlink analyzeLink = 
        new InlineHyperlink("analyze", HistoryTokens.campaignAnalyze(campaignId));
      analyzeLink.setStyleName(style.analyzeLink());
      panel.add(analyzeLink);
    }
    if (campaign.userCanEdit()) {
      InlineHyperlink editLink = 
        new InlineHyperlink("edit", HistoryTokens.campaignEdit(campaignId));
      editLink.setStyleName(style.editLink());
      panel.add(editLink);
    }
    // TODO: get xml
    // TODO: export csv
    
    return panel.asWidget();
  }
  
  private String getPrivacyStyle(Privacy privacy) {
    String styleName = "";
    switch (privacy) {
      case PRIVATE: styleName = style.privacyPrivate(); break;
      case SHARED: styleName = style.privacyShared(); break;
      case INVISIBLE: break;
      default: break;
    }
    return styleName;
  }
  
  private String getRunningStateStyle(RunningState runningState) {
    String styleName = "";
    switch (runningState) {
      case RUNNING: styleName = style.running(); break;
      case STOPPED: styleName = style.stopped(); break;
      default: break;
    }
    return styleName;
  }

}
