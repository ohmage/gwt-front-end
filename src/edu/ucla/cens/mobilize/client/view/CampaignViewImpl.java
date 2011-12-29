package edu.ucla.cens.mobilize.client.view;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.RoleCampaign;
import edu.ucla.cens.mobilize.client.common.RunningState;
import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.ui.CampaignDetail;
import edu.ucla.cens.mobilize.client.ui.CampaignEditFormView;
import edu.ucla.cens.mobilize.client.ui.CampaignList;
import edu.ucla.cens.mobilize.client.ui.ErrorDialog;

/**
 * Everything shown in the campaigns tab is part of the CampaignView.
 * There are several subviews. Only the top level view uses a presenter.
 * The subviews are controlled by swapping out components that can be
 * found in the mobilize.client.ui module.
 * 
 * @author vhajdik
 *
 */
public class CampaignViewImpl extends Composite implements CampaignView {
  
  private static CampaignViewUiBinder uiBinder = GWT
      .create(CampaignViewUiBinder.class);
  
  @UiTemplate("CampaignView.ui.xml")
  interface CampaignViewUiBinder extends UiBinder<Widget, CampaignViewImpl> {
  }

  // flags that control role-specific display
  boolean canCreate = false;

  // declare uibinder elements
  
  @UiField Label leftSideBarMenuTitle;
  @UiField MenuBar leftSideBarMenu;
  @UiField Button campaignCreateButton;
  
  @UiField CampaignList campaignList;
  @UiField CampaignDetail campaignDetail;
  @UiField CampaignEditFormView campaignEdit;
  
  @UiField MenuItem quickFilterAll;
  @UiField MenuItem quickFilterCreatedLastWeek;
  @UiField MenuItem quickFilterCreatedLastMonth;
  @UiField MenuItem quickFilterAuthored;
  
  @UiField HTMLPanel msgBox;
  @UiField Label msgLabel;
  @UiField Anchor closeMsg;
  
  public CampaignViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
    hideMsg();
    showList();
    bind();
  }

  private void bind() {
    // clicking on the "hide" link closes info message box
    closeMsg.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        hideMsg();
      }
    });
    
    // clicking on create button opens campaign/create view
    campaignCreateButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        History.newItem(HistoryTokens.campaignCreate());
      }
    });
    
    quickFilterAll.setCommand(new Command() {
      @Override
      public void execute() {
        History.newItem(HistoryTokens.campaignList());
      }
    });
    
    quickFilterAuthored.setCommand(new Command() {
      @Override
      public void execute() {
        History.newItem(HistoryTokens.campaignList(null, RoleCampaign.AUTHOR, null, null));
      }
    });
    
    final Date today = new Date(); // now
    long millisecsPerDay = 24 * 60 * 60 * 1000;
    final Date lastWeek = new Date(today.getTime() - (7 * millisecsPerDay)); // now minus 7 days
    final Date lastMonth = new Date(today.getTime() - (30 * millisecsPerDay)); // now minus 30 days
    
    quickFilterCreatedLastWeek.setCommand(new Command() {
      @Override
      public void execute() {
        History.newItem(HistoryTokens.campaignList(null, null, lastWeek, today));
      }
    });
    
    quickFilterCreatedLastMonth.setCommand(new Command() {
      @Override
      public void execute() {
        History.newItem(HistoryTokens.campaignList(null, null, lastMonth, today));
      }
    });
  }

  @Override
  public void setCampaignList(List<CampaignShortInfo> campaigns) {
    campaignList.setCampaigns(campaigns);
  }

  @Override
  public void setCampaignDetail(CampaignDetailedInfo campaign) {
    campaignDetail.setCampaign(campaign);
  }
  
  private void hideAllWidgets() {
    campaignList.setVisible(false);
    campaignDetail.setVisible(false);
    campaignEdit.setVisible(false);
  }
  
  @Override
  public void showList() {
    hideAllWidgets();
    campaignList.setVisible(true);
  }
  
  @Override
  public void showDetail() {
    hideAllWidgets();
    campaignDetail.setVisible(true);
  }
  
  @Override
  public void showEditForm() {
    hideAllWidgets();
    campaignEdit.setVisible(true); 
  }

  @Override
  public void setCampaignEdit(CampaignDetailedInfo campaign) {
    this.campaignEdit.setCampaignUrn(campaign.getCampaignId());
    this.campaignEdit.setCampaignName(campaign.getCampaignName());
    this.campaignEdit.setDescription(campaign.getDescription());
    this.campaignEdit.setPrivacy(campaign.getPrivacy());
    this.campaignEdit.setRunningState(campaign.getRunningState());
    this.campaignEdit.setSelectedAuthors(campaign.getAuthors());
    // FIXME: get class names from somewhere
    Map<String, String> classUrnToClassNameMap = new HashMap<String, String>();
    for (String classUrn : campaign.getClasses()) {
      classUrnToClassNameMap.put(classUrn, classUrn);
    }
    this.campaignEdit.setSelectedClasses(classUrnToClassNameMap);
    this.campaignEdit.setHeader("Editing " + campaign.getCampaignName());
  }


  @Override
  public void setCanCreate(boolean canCreate) {
    this.canCreate = canCreate;
    updateRoleSpecificDisplay();
  }
  
  private void updateRoleSpecificDisplay() {
    this.campaignCreateButton.setVisible(this.canCreate);
    this.quickFilterAuthored.setVisible(this.canCreate);
  }

  @Override
  public void showError(String msg, Throwable caught) {
    String detail = (caught != null) ? caught.getMessage() : null;
    ErrorDialog.show(msg, detail);
  }

  @Override
  public void showMsg(String msgText) {
    msgLabel.setText(msgText);
    msgBox.setVisible(true);
  }
  
  @Override
  public void hideMsg() {
    msgLabel.setText("");
    msgBox.setVisible(false);
  }

  @Override
  public CampaignEditFormView getCampaignEditForm() {
    // FIXME: how can this be hooked to presenter without needing to expose form?
    return this.campaignEdit;
  }

  @Override
  public CampaignList getCampaignList() {
    return this.campaignList;
  }
  
  @Override
  public CampaignDetail getCampaignDetail() {
    return this.campaignDetail;
  }

  @Override
  public void setCampaignListFilters(RunningState state, 
                                     RoleCampaign role,
                                     Date fromDate, 
                                     Date toDate) {
    this.campaignList.setSelectedRunningState(state);
    this.campaignList.setSelectedRole(role);
    this.campaignList.setSelectedStartDate(fromDate);
    this.campaignList.setSelectedEndDate(toDate);
  }
}
