package edu.ucla.cens.mobilize.client.view;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.ui.ErrorDialog;
import edu.ucla.cens.mobilize.client.ui.PasswordChangeForm;
import edu.ucla.cens.mobilize.client.utils.MapUtils;

public class AdminUserDetailView extends Composite {

  private static AdminUserDetailViewUiBinder uiBinder = GWT
      .create(AdminUserDetailViewUiBinder.class);

  interface AdminUserDetailViewUiBinder extends
      UiBinder<Widget, AdminUserDetailView> {
  }
  
  interface AdminUserDetailViewStyle extends CssResource {
    String missing();
  }

  @UiField AdminUserDetailViewStyle style;
  @UiField Anchor backLink;
  @UiField InlineHyperlink actionLinkEditUser;
  @UiField Anchor actionLinkEnableUser;
  @UiField Anchor actionLinkDisableUser;
  @UiField Anchor actionLinkDeleteUser;
  @UiField Anchor actionLinkChangePassword;
  @UiField Label usernameField;
  @UiField Label firstNameField;
  @UiField Label lastNameField;
  @UiField Label organizationField;
  @UiField Label personalIdField;
  @UiField Label emailField;
  @UiField Label enabledField;
  @UiField Label canCreateCampaignsField;
  @UiField Label adminField;
  @UiField FlexTable classListFlexTable;
  @UiField FlexTable campaignListFlexTable;
  
  private final String MISSING_FIELD_STRING = "Not Available";

  private PasswordChangeForm passwordChangeForm = new PasswordChangeForm();
  
  public AdminUserDetailView() {
    initWidget(uiBinder.createAndBindUi(this));
    initComponents();
  }
  
  private void initComponents() {
    this.backLink.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        History.back();
      }
    });
  }
  
  public void setUsername(String username) {
    this.usernameField.setText(username);
    this.actionLinkEditUser.setTargetHistoryToken(HistoryTokens.adminUserEdit(username));
  }

  // If valueOrNull param is a string, this method sets it in the corresponding label.
  // If it is null, meaning the optional value is not present, it sets text and style
  //   that shows the field is not present.
  private void setOptionalFieldText(Label optionalField, String valueOrNull) {
    if (valueOrNull != null && !valueOrNull.isEmpty()) {
      optionalField.setText(valueOrNull);
      optionalField.removeStyleName(style.missing());
    } else {
      optionalField.setText(MISSING_FIELD_STRING);
      optionalField.addStyleName(style.missing());
    }
  }
  
  /**
   * @param firstName String or null
   */
  public void setFirstName(String firstName) {
    setOptionalFieldText(this.firstNameField, firstName);
  }
  
  /**
   * @param lastName String or null
   */
  public void setLastName(String lastName) {
    setOptionalFieldText(this.lastNameField, lastName);
  }
  
  /**
   * @param organization String or null
   */
  public void setOrganization(String organization) {
    setOptionalFieldText(this.organizationField, organization);
  }
  
  /**
   * @param personalId String or null
   */
  public void setPersonalId(String personalId) {
    setOptionalFieldText(this.personalIdField, personalId);
  }
  
  /**
   * @param email String or null
   */
  public void setEmail(String email) {
    setOptionalFieldText(this.emailField, email);
  }
  
  public void setEnabledFlag(boolean isEnabled) {
    this.enabledField.setText(isEnabled ? "yes" : "no");
  }
  
  public void setCanCreateFlag(boolean canCreate) {
    this.canCreateCampaignsField.setText(canCreate ? "yes" : "no");
  }
  
  public void setAdminFlag(boolean isAdmin) {
    this.adminField.setText(isAdmin ? "yes" : "no");
  }
  
  public void setClassList(Map<String, String> classIdToNameMap) {
    List<String> classIds = MapUtils.getKeysSortedByValues(classIdToNameMap);
    for (int i = 0; i < classIds.size(); i++) {
      String className = classIdToNameMap.get(classIds.get(i));
      String url = HistoryTokens.classDetail(classIds.get(i));
      Hyperlink link = new Hyperlink(className, url);
      this.classListFlexTable.setWidget(i, 0, link);
    }
  }
  
  public void setCampaignList(Map<String, String> campaignIdToNameMap) {
    List<String> campaignIds = MapUtils.getKeysSortedByValues(campaignIdToNameMap);
    for (int i = 0; i < campaignIds.size(); i++) {
      String campaignName = campaignIdToNameMap.get(campaignIds.get(i));
      String url = HistoryTokens.campaignDetail(campaignIds.get(i));
      Hyperlink link = new Hyperlink(campaignName, url);
      this.campaignListFlexTable.setWidget(i, 0, link);
    }
  }
  
  public String getUsername() {
    return this.usernameField.getText();
  }
  
  public HasClickHandlers getActionLinkEnableUser() {
    return this.actionLinkEnableUser;
  }
  
  public HasClickHandlers getActionLinkDisableUser() {
    return this.actionLinkDisableUser;
  }
  
  public HasClickHandlers getActionLinkDeleteUser() {
    return this.actionLinkDeleteUser;
  }
  
  public HasClickHandlers getActionLinkResetPassword() {
    return this.actionLinkChangePassword;
  }
  
  public void showError(String msg, String detail) {
    ErrorDialog.show(msg, detail);
  }

  public void showMessage(String title, String message) {
    final DialogBox dialog = new DialogBox();
    dialog.setAutoHideEnabled(true);
    dialog.setGlassEnabled(true);
    dialog.setModal(true);
    dialog.setText(title);
    HTML msg = new HTML(message);
    
    Button okButton = new Button("OK");
    okButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        dialog.hide();
      }
    });
    
    FlowPanel panel = new FlowPanel();
    panel.add(msg);
    panel.add(okButton);
    
    dialog.add(panel);
    dialog.center();
  }  

  public void showPasswordChangeDialog(String username, final ClickHandler submitHandler) {
    final DialogBox dialog = new DialogBox();
    dialog.setText("Changing password for " + username);
    this.passwordChangeForm.clear();
    this.passwordChangeForm.setUsername(username);
    this.passwordChangeForm.getChangePasswordButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        dialog.hide();
        submitHandler.onClick(event);
      }
    });
    this.passwordChangeForm.getCancelButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        dialog.hide();
      }
    });
    
    dialog.add(this.passwordChangeForm);
    dialog.showRelativeTo(this.actionLinkChangePassword);
  }
  
  
  public String passwordChangeGetAdminPassword() {
    return this.passwordChangeForm.getAdminPassword();
  }
  
  public String passwordChangeGetNewPassword() {
    return this.passwordChangeForm.getNewPassword();
  }
  
}
