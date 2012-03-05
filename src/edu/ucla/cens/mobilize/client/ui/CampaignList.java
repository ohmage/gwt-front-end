package edu.ucla.cens.mobilize.client.ui;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import edu.ucla.cens.mobilize.client.AwConstants;
import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.RunningState;
import edu.ucla.cens.mobilize.client.common.RoleCampaign;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;
import edu.ucla.cens.mobilize.client.utils.DateUtils;
import edu.ucla.cens.mobilize.client.utils.MapUtils;

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
    String detailsLink();
    String editLink();
    String exportLink();
  }

  private static Logger _logger = Logger.getLogger(CampaignList.class.getName());

  private DataService dataService; // needed for csv export
  
  private static CampaignListWidgetUiBinder uiBinder = GWT
      .create(CampaignListWidgetUiBinder.class);

  @UiTemplate("CampaignList.ui.xml")
  interface CampaignListWidgetUiBinder extends
      UiBinder<Widget, CampaignList> {
  }
  
  // declare uibinder fields
  @UiField HTMLPanel mainPanel;
  @UiField ListBox stateListBox;
  @UiField ListBox userRoleListBox;
  @UiField DateBox fromDateBox;
  @UiField DateBox toDateBox;
  @UiField Grid campaignGrid;
  @UiField CampaignListStyle style;
  @UiField Button goButton;
  
  // table columns 
  private enum Column { NAME, CREATED_ON, RUNNING_STATE, PRIVACY, ACTIONS };
  
  private DateTimeFormat dateTimeFormat = DateUtils.getTableDisplayFormat();
  
  public CampaignList() {
    initWidget(uiBinder.createAndBindUi(this));
    initComponents();
  }

  private void initComponents() {
    // whether campaign is running. 
    stateListBox.addItem("Any", "");
    stateListBox.addItem(RunningState.RUNNING.toUserFriendlyString(),  
                         RunningState.RUNNING.toServerString());      
    stateListBox.addItem(RunningState.STOPPED.toUserFriendlyString(), 
                         RunningState.STOPPED.toServerString());      
    
    // current user's role in the campaign
    // FIXME: only show roles from user's roles list
    userRoleListBox.addItem("Any", "");
    userRoleListBox.addItem(RoleCampaign.PARTICIPANT.toUserFriendlyString(),
                            RoleCampaign.PARTICIPANT.toServerString());
    userRoleListBox.addItem(RoleCampaign.ANALYST.toUserFriendlyString(),
                            RoleCampaign.ANALYST.toServerString());
    userRoleListBox.addItem(RoleCampaign.AUTHOR.toUserFriendlyString(),
                            RoleCampaign.AUTHOR.toServerString());
    userRoleListBox.addItem(RoleCampaign.SUPERVISOR.toUserFriendlyString(),
                            RoleCampaign.SUPERVISOR.toServerString());
    userRoleListBox.setSelectedIndex(0);
    
    // start and end dates
    DateBox.Format fmt = new DateBox.DefaultFormat(DateUtils.getDateBoxDisplayFormat());
    fromDateBox.setFormat(fmt);
    toDateBox.setFormat(fmt);
    
    // set grid size. rows = # campaigns (guess) + 1 for header, cols comes from enum
    int maxCampaignsGuess = 20; // more row are added in addCampaign if needed
    campaignGrid.resize(maxCampaignsGuess + 1, Column.values().length); 
    
    // set up table heading
    campaignGrid.getRowFormatter().setStyleName(0, style.campaignGridHeader());
    campaignGrid.setText(0, Column.NAME.ordinal(), "Campaign name");
    campaignGrid.setText(0, Column.CREATED_ON.ordinal(), "Created on");
    campaignGrid.setText(0, Column.RUNNING_STATE.ordinal(), "Running state");
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
    
    bind(); // wire up event handlers
  }

  private void bind() {
    this.goButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
    	  Date s = getSelectedStartDate();
    	  Date e = getSelectedEndDate();
    	  
    	  if (s != null && e != null && s.after(e))
    		  ErrorDialog.show("Invalid date range", "Starting date must be before or equal to the end date.");
    	  else if ((s != null && e == null) || (s == null && e != null))
    		  ErrorDialog.show("Invalid date range", "You must provide both a start and end date, or leave both fields empty");
    	  else
    		  fireHistoryTokenToMatchFilterValues();
      }
    });
  }
  
  private void fireHistoryTokenToMatchFilterValues() {
    History.newItem(HistoryTokens.campaignList(getSelectedRunningState(), 
                                               getSelectedRole(),
                                               getSelectedStartDate(), 
                                               getSelectedEndDate()));
  }
  
  public void setDataService(DataService dataService) {
    this.dataService = dataService;
  }
  
  // returns selected enum or null if no match
  public RunningState getSelectedRunningState() {
    RunningState retval = null;
    String selectedString = stateListBox.getValue(stateListBox.getSelectedIndex());
    for (RunningState state : RunningState.values()) {
      if (state.toServerString().equals(selectedString)) {
        retval = state;
        break;
      }
    }
    return retval;
  }
  
  // returns selected enum or null if no match
  public RoleCampaign getSelectedRole() {
    RoleCampaign retval = null;
    String selectedString = userRoleListBox.getValue(userRoleListBox.getSelectedIndex());
    for (RoleCampaign role : RoleCampaign.values()) {
      if (role.toServerString().equals(selectedString)) {
        retval = role;
        break;
      }
    }
    return retval;
  }
  
  public Date getSelectedStartDate() {
    return fromDateBox.getValue();
  }
  
  public Date getSelectedEndDate() {
    return toDateBox.getValue();
  }
  
  public void setSelectedRunningState(RunningState stateToSelect) {
    if (stateToSelect == null) { stateListBox.setSelectedIndex(0); return; }
    String stateStringToSelect = stateToSelect.toServerString();
    for (int i = 0; i < stateListBox.getItemCount(); i++) {
      if (stateListBox.getValue(i).equals(stateStringToSelect)) {
        stateListBox.setSelectedIndex(i);
        break;
      }
    }
  }
  
  public void setSelectedRole(RoleCampaign roleToSelect) {
    if (roleToSelect == null) { userRoleListBox.setSelectedIndex(0); return;}
    String roleStringToSelect = roleToSelect.toServerString();
    for (int i = 0; i < userRoleListBox.getItemCount(); i++) {
      if (userRoleListBox.getValue(i).equals(roleStringToSelect)) {
        userRoleListBox.setSelectedIndex(i);
        break;
      }
    }
  }
  
  public void setSelectedStartDate(Date fromDate) {
    fromDateBox.setValue(fromDate);
  }
  
  public void setSelectedEndDate(Date toDate) {
    toDateBox.setValue(toDate);
  }
  
  /**
   * renders a list of campaigns in a table
   * @param campaigns
   */
  public void setCampaigns(List<CampaignShortInfo> campaigns) {
    this.campaignGrid.resizeRows(campaigns.size() + 1); // one extra row for header
    int row = 1; // 0th row is header
    for (CampaignShortInfo campaignInfo : campaigns) {
      addCampaign(row++, campaignInfo);
    }
  }
  
  private void addCampaign(int row, CampaignShortInfo campaignInfo) {
    // stripe odd rows
    if (row % 2 != 0) {
      this.campaignGrid.getRowFormatter().addStyleName(row, style.oddRow());
    }
    
    // campaign name links to campaign detail page
    // truncate the name with ellipses if too long
    final int MAX_NAME_LENGTH = 80;
    String truncatedName = campaignInfo.getCampaignName();
    if (truncatedName.length() > MAX_NAME_LENGTH)
    	truncatedName = truncatedName.substring(0, MAX_NAME_LENGTH-3) + "...";
    
    Hyperlink campaignDetailLink = 
      new Hyperlink(truncatedName, 
                    HistoryTokens.campaignDetail(campaignInfo.getCampaignId()));
    campaignDetailLink.setTitle(campaignInfo.getCampaignName());
    
    this.campaignGrid.setWidget(row, Column.NAME.ordinal(), campaignDetailLink); 
    this.campaignGrid.getCellFormatter().setStyleName(row, 
                                                      Column.NAME.ordinal(), 
                                                      style.campaignGridNameColumn());
    
    // creation date
    String dateString = this.dateTimeFormat.format(campaignInfo.getCreationTime());
    this.campaignGrid.setText(row, Column.CREATED_ON.ordinal(), dateString);
    
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
  
  private Widget getActionsWidget(CampaignShortInfo campaign) {
    Panel panel = new FlowPanel();
    final String campaignId = campaign.getCampaignId();
    if (campaign.userCanViewDetails()) {
      InlineHyperlink detailsLink = 
        new InlineHyperlink("view", HistoryTokens.campaignDetail(campaignId));
      detailsLink.setStyleName(style.detailsLink());
      panel.add(detailsLink);
    }
    /*
    // FIXME: hidden until explore data tab is done
    if (campaign.userCanAnalyze()) {
      InlineHyperlink analyzeLink = 
        new InlineHyperlink("analyze", HistoryTokens.campaignAnalyze(campaignId));
      analyzeLink.setStyleName(style.analyzeLink());
      panel.add(analyzeLink);
    }*/
    if (campaign.userCanEdit()) {
      InlineHyperlink editLink = 
        new InlineHyperlink("edit", HistoryTokens.campaignEdit(campaignId));
      editLink.setStyleName(style.editLink());
      panel.add(editLink);
    }
    if (campaign.userCanAnalyze()) {
      Anchor exportLink = new Anchor("export");
      exportLink.setStyleName(style.exportLink());
      panel.add(exportLink);
      exportLink.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          exportCsv(campaignId);
        }
      });
    }
    
    return panel.asWidget();
  }
  
  // FIXME: good way to handle this from CampaignPresenter so CampaignList widget
  // doesn't need access to DataService?
  private void exportCsv(String campaignId) {
    assert dataService != null : "DataService is null. Did you forget to call CampaignList.setDataService?";
    FormPanel exportForm = new FormPanel("_blank"); // target="_blank" to open new window
    exportForm.setAction(AwConstants.getSurveyResponseReadUrl());
    exportForm.setMethod(FormPanel.METHOD_POST);
    FlowPanel innerContainer = new FlowPanel();
    
    Map<String, String> params = dataService.getSurveyResponseExportParams(campaignId);
    _logger.fine("Generating FormPanel with hidden fields set to survey response export params: " + 
                  MapUtils.translateToParameters(params));
    for (String paramName : params.keySet()) {
      Hidden field = new Hidden();
      field.setName(paramName);
      field.setValue(params.get(paramName));
      innerContainer.add(field);
    }
    exportForm.add(innerContainer);
    mainPanel.add(exportForm, "formContainer");
    exportForm.submit();
    exportForm.removeFromParent();
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
