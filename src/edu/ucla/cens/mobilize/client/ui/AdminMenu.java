package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;

public class AdminMenu extends Composite {

  private static AdminMenuUiBinder uiBinder = GWT
      .create(AdminMenuUiBinder.class);

  interface AdminMenuUiBinder extends UiBinder<Widget, AdminMenu> {
  }

  interface AdminMenuStyles extends CssResource {
    String menuItemContainerSelected();
  }

  @UiField AdminMenuStyles style;
  @UiField Hyperlink adminHomeHyperlink;
  @UiField Hyperlink auditLogHyperlink;
  @UiField Hyperlink createUserHyperlink;
  @UiField Hyperlink manageUsersHyperlink;
  @UiField Hyperlink createClassHyperlink;
  @UiField Hyperlink manageClassesHyperlink;
  @UiField Hyperlink createCampaignHyperlink;
  @UiField Hyperlink manageCampaignsHyperlink;

  @UiField HTMLPanel adminHomeContainer;
  @UiField HTMLPanel auditLogContainer;
  @UiField HTMLPanel createUserContainer;
  @UiField HTMLPanel manageUsersContainer;
  @UiField HTMLPanel createClassContainer;
  @UiField HTMLPanel manageClassesContainer;
  @UiField HTMLPanel createCampaignContainer;
  @UiField HTMLPanel manageCampaignsContainer;
  
  public AdminMenu() {
    initWidget(uiBinder.createAndBindUi(this));
    setLinkTargetUrls();
  }
  
  // done here instead of in template so they can use HistoryToken constants
  private void setLinkTargetUrls() {
    adminHomeHyperlink.setTargetHistoryToken(HistoryTokens.admin());
    auditLogHyperlink.setTargetHistoryToken(HistoryTokens.auditLog());
    createUserHyperlink.setTargetHistoryToken(HistoryTokens.adminUserCreate());
    manageUsersHyperlink.setTargetHistoryToken(HistoryTokens.adminUserList());
    createClassHyperlink.setTargetHistoryToken(HistoryTokens.adminClassCreate());
    manageClassesHyperlink.setTargetHistoryToken(HistoryTokens.adminClassList());
    createCampaignHyperlink.setTargetHistoryToken(HistoryTokens.campaignCreate());
    manageCampaignsHyperlink.setTargetHistoryToken(HistoryTokens.campaignList());
  }
  
  public void clearSelectedItem() {
    adminHomeContainer.removeStyleName(style.menuItemContainerSelected());
    auditLogContainer.removeStyleName(style.menuItemContainerSelected());
    createUserContainer.removeStyleName(style.menuItemContainerSelected());
    manageUsersContainer.removeStyleName(style.menuItemContainerSelected());
    createClassContainer.removeStyleName(style.menuItemContainerSelected());
    manageClassesContainer.removeStyleName(style.menuItemContainerSelected());
    createCampaignContainer.removeStyleName(style.menuItemContainerSelected());
    manageCampaignsContainer.removeStyleName(style.menuItemContainerSelected());
  }
  
  public void selectAdminHome() {
    clearSelectedItem();
    adminHomeContainer.addStyleName(style.menuItemContainerSelected());
  }
  
  public void selectAuditLog() {
    clearSelectedItem();
    auditLogContainer.addStyleName(style.menuItemContainerSelected());
  }

  public void selectManageUsers() {
    clearSelectedItem();
    manageUsersContainer.addStyleName(style.menuItemContainerSelected());
  }

  public void selectManageClasses() {
    clearSelectedItem();
    manageClassesContainer.addStyleName(style.menuItemContainerSelected());
  }
  
  public void selectManageCampaigns() {
    clearSelectedItem();
    manageCampaignsContainer.addStyleName(style.menuItemContainerSelected());
  }
  
  public void selectCreateUser() {
    clearSelectedItem();
    createUserContainer.addStyleName(style.menuItemContainerSelected());
  }

  public void selectCreateClasses() {
    clearSelectedItem();
    createClassContainer.addStyleName(style.menuItemContainerSelected());
  }
  
  public void selectCreateCampaigns() {
    clearSelectedItem();
    createCampaignContainer.addStyleName(style.menuItemContainerSelected());
  }
}
