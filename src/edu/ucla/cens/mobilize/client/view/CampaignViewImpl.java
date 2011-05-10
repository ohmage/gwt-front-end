package edu.ucla.cens.mobilize.client.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.ui.CampaignDetail;
import edu.ucla.cens.mobilize.client.ui.CampaignEditFormView;
import edu.ucla.cens.mobilize.client.ui.CampaignList;

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

  // member vars
  CampaignView.Presenter presenter;
  
  // flags that control role-specific display
  boolean canCreate = false;

  // declare uibinder elements
  
  @UiField Label leftSideBarMenuTitle;
  @UiField MenuBar leftSideBarMenu;
  @UiField Button campaignCreateButton;
  
  @UiField CampaignList campaignList;
  @UiField CampaignDetail campaignDetail;
  @UiField CampaignEditFormView campaignEdit;
  
  @UiField Label plotSideBarTitle;
  @UiField VerticalPanel plotPanel;

  @UiField MenuItem authorMenuItem; 
  
  @UiField MenuItem quickFilterAll;
  @UiField MenuItem quickFilterActiveLastWeek;
  @UiField MenuItem quickFilterActiveLastTwoWeeks;
  @UiField MenuItem quickFilterActiveLastMonth;
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
        if (presenter != null) {
          presenter.onCampaignCreate();
        }
      }
    });
  }
  
  @Override
  public void setPresenter(Presenter p) {
    this.presenter = p;
  }

  @Override
  public void setCampaignList(List<CampaignShortInfo> campaigns) {
    campaignList.setCampaigns(campaigns);
  }

  @Override
  public void setCampaignDetail(CampaignDetailedInfo campaign, boolean canEdit) {
    campaignDetail.setCampaign(campaign, canEdit);
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
    // TODO: quick filters in left sidebar
  }
  
  @Override
  public void showDetail() {
    hideAllWidgets();
    campaignDetail.setVisible(true);
    // TODO: campaign actions in left sidebar
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
  public void setPlotSideBarTitle(String title) {
    this.plotSideBarTitle.setText(title);
  }
  
  @Override
  public void clearPlots() {
    this.plotSideBarTitle.setText("");
    this.plotPanel.clear();
  }

  @Override
  public void addPlot(String imgUrl) {
    this.plotPanel.add(new Image(imgUrl));
  }

  @Override
  public void showPlots() {
    this.plotPanel.setVisible(true);
  }

  @Override
  public void showError(String msg) {
    final DialogBox errorDialog = new DialogBox();
    errorDialog.setGlassEnabled(true);
    errorDialog.setText(msg);
    Button dismissButton = new Button("OK");
    dismissButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        errorDialog.hide(); 
      }
    });
    errorDialog.add(dismissButton);
    // TODO: add style
    errorDialog.center();
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

}
