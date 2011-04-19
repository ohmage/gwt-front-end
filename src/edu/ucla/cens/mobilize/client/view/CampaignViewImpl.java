package edu.ucla.cens.mobilize.client.view;

import java.util.List;

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

import edu.ucla.cens.mobilize.client.model.CampaignConciseInfo;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.ui.CampaignDetail;
import edu.ucla.cens.mobilize.client.ui.CampaignEditForm;
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
  @UiField CampaignEditForm campaignEdit;
  
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
    showList();
    bind();
    msgBox.setVisible(false);
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
    
    // When user clicks delete in the campaign edit form, a dialog pops
    // up asking are you sure. Clicking "delete" in the dialog passes
    // the event to the presenter. ("cancel" closes the dialog with no effect)
    this.campaignEdit.getDeleteButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        final DialogBox dialog = new DialogBox();
        dialog.setGlassEnabled(true);
        dialog.setText("Are you sure you want to delete this campaign?");
        dialog.setModal(true);
        Button deleteButton = new Button("Delete");
        deleteButton.addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            dialog.hide();
            presenter.onCampaignDelete(campaignEdit.getCampaignId());
          }
        });
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            dialog.hide();
          }
        });
        FlowPanel panel = new FlowPanel(); 
        panel.add(deleteButton);
        panel.add(cancelButton);
        dialog.add(panel);
        dialog.center();
      }
    });
  }
  
  @Override
  public void setPresenter(Presenter p) {
    this.presenter = p;
  }

  @Override
  public void setCampaignList(List<CampaignConciseInfo> campaigns) {
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
  public void showCreateForm() {
    hideAllWidgets();
    campaignEdit.setVisible(true);
    campaignEdit.setIsNewCampaignFlag(true);
    // TODO: create actions in left sidebar?
  }
  
  @Override
  public void showEditForm() {
    hideAllWidgets();
    campaignEdit.setVisible(true); 
  }

  @Override
  public void setCampaignEdit(CampaignDetailedInfo campaign) {
    this.campaignEdit.setCampaign(campaign);
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
  public void setClassListToChooseFrom(List<String> classes) {
    this.campaignEdit.setClassListToChooseFrom(classes);
  }
  
  @Override
  public void setAuthorListToChooseFrom(List<String> authors) {
    this.campaignEdit.setAuthorListToChooseFrom(authors);
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
    msgBox.setVisible(false);
  }
  

}
