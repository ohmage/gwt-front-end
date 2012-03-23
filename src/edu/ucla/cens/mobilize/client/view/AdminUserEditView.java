package edu.ucla.cens.mobilize.client.view;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.ui.AdminMenu;
import edu.ucla.cens.mobilize.client.ui.ConfirmDeleteDialog;

public class AdminUserEditView extends Composite {

  private static AdminUserEditViewUiBinder uiBinder = GWT
      .create(AdminUserEditViewUiBinder.class);

  interface AdminUserEditViewUiBinder extends
      UiBinder<Widget, AdminUserEditView> {
  }

  interface AdminUserEditViewStyles extends CssResource {
    String invalid();
  }
  
  @UiField AdminUserEditViewStyles style;
  @UiField AdminMenu adminMenu;  
  @UiField Anchor backLink;
  @UiField Label header;
  @UiField Label usernameLabel; 
  @UiField TextBox firstNameTextBox;
  @UiField TextBox lastNameTextBox;
  @UiField TextBox organizationTextBox;
  @UiField TextBox personalIdTextBox;
  @UiField TextBox emailTextBox;
  @UiField InlineLabel usernameInvalidMsg;
  @UiField InlineLabel firstNameInvalidMsg;
  @UiField InlineLabel lastNameInvalidMsg;
  @UiField InlineLabel organizationInvalidMsg;
  @UiField InlineLabel personalIdInvalidMsg;
  @UiField InlineLabel emailInvalidMsg;
  @UiField CheckBox isEnabledCheckBox;
  @UiField CheckBox canCreateCampaignsCheckBox;
  @UiField CheckBox isAdminCheckBox;
  @UiField Button saveChangesButton;
  @UiField Button removePersonalInfoButton;
  @UiField Button deleteUserButton;
  @UiField Button cancelButton;

  private List<TextBox> textBoxes;

  private List<InlineLabel> invalidMsgs;

  public AdminUserEditView() {
    initWidget(uiBinder.createAndBindUi(this));
    initComponents();
  }
  
  private void initComponents() {
    this.textBoxes = Arrays.asList(this.firstNameTextBox,
                                   this.lastNameTextBox,
                                   this.organizationTextBox,
                                   this.personalIdTextBox,
                                   this.emailTextBox);
    
    this.invalidMsgs = Arrays.asList(this.usernameInvalidMsg,
                                     this.firstNameInvalidMsg,
                                     this.lastNameInvalidMsg,
                                     this.organizationInvalidMsg,
                                     this.personalIdInvalidMsg,
                                     this.emailInvalidMsg);
  }
  
  /**
   * Clear form fields
   */
  public void resetForm() {
    clearInvalidFieldMarkers();
    this.usernameLabel.setText("");
    this.emailTextBox.setText(""); 
    this.firstNameTextBox.setText("");
    this.lastNameTextBox.setText("");
    this.organizationTextBox.setText("");
    this.personalIdTextBox.setText("");
    this.emailTextBox.setText("");
  }

  // Methods for setting data in form
  
  public void setUsername(String username) {
    this.usernameLabel.setText(username);
    this.header.setText("Editing " + username);
  }

  public void setFirstName(String firstName) {
    this.firstNameTextBox.setText(firstName);
  }
  
  public void setLastName(String lastName) {
    this.lastNameTextBox.setText(lastName);
  }
  
  public void setOrganization(String organization) {
    this.organizationTextBox.setText(organization);
  }
  
  public void setPersonalId(String personalId) {
    this.personalIdTextBox.setText(personalId);
  }
  
  public void setEmail(String email) {
    this.emailTextBox.setText(email);
  }
  
  public void setEnabledFlag(boolean isEnabled) {
    this.isEnabledCheckBox.setValue(isEnabled);
  }
  
  public void setCanCreateCampaignsFlag(boolean canCreate) {
    this.canCreateCampaignsCheckBox.setValue(canCreate);
  }
  
  public void setIsAdminFlag(boolean isAdmin) {
    this.isAdminCheckBox.setValue(isAdmin);
  }
  
  // Methods for getting data from form
  
  public String getUsername() {
    return this.usernameLabel.getText();
  }

  public String getFirstName() {
    return this.firstNameTextBox.getText();
  }
  
  public String getLastName() {
    return this.lastNameTextBox.getText();
  }
  
  public String getOrganization() {
    return this.organizationTextBox.getText();
  }
  
  public String getPersonalId() {
    return this.personalIdTextBox.getText();
  }
  
  public String getEmail() {
    return this.emailTextBox.getText();
  }
  
  public boolean getEnabledFlag() {
    return this.isEnabledCheckBox.getValue();
  }
  
  public boolean getCanCreateCampaignsFlag() {
    return this.canCreateCampaignsCheckBox.getValue();
  }
  
  public boolean getIsAdminFlag() {
    return this.isAdminCheckBox.getValue();
  }
  
  // Methods for marking form fields invalid
  
  public void markFirstNameInvalid(String message) {
    this.firstNameTextBox.addStyleName(style.invalid());
    this.firstNameInvalidMsg.setVisible(true);
    this.firstNameInvalidMsg.setText(message);
  }
  
  public void markLastNameInvalid(String message) {
    this.lastNameTextBox.addStyleName(style.invalid());
    this.lastNameInvalidMsg.setVisible(true);
    this.lastNameInvalidMsg.setText(message);
  }
  
  public void markOrganizationInvalid(String message) {
    this.organizationTextBox.addStyleName(style.invalid());
    this.organizationInvalidMsg.setVisible(true);
    this.organizationInvalidMsg.setText(message);
  }
  
  public void markPersonalIdInvalid(String message) {
    this.personalIdTextBox.addStyleName(style.invalid());
    this.personalIdInvalidMsg.setVisible(true);
    this.personalIdInvalidMsg.setText(message);
  }
  
  public void markEmailInvalid(String message) {
    this.emailTextBox.addStyleName(style.invalid());
    this.emailInvalidMsg.setVisible(true);
    this.emailInvalidMsg.setText(message);
  }
  
  public void clearInvalidFieldMarkers() {
    for (TextBox tb : this.textBoxes) {
      tb.removeStyleName(style.invalid());
    }
    
    for (InlineLabel msg : this.invalidMsgs) {
      msg.setText("");
      msg.setVisible(false);
    }
  }
  
  // Methods for returning controls to the presenter so it can add handlers
  
  public HasClickHandlers getBackLink() {
    return this.backLink;
  }
  
  public HasClickHandlers getSaveChangesButton() {
    return this.saveChangesButton;
  }
  
  public HasClickHandlers getRemovePersonalInfoButton() {
    return this.removePersonalInfoButton;
  }
  
  public HasClickHandlers getDeleteUserButton() {
    return this.deleteUserButton;
  }
  
  public HasClickHandlers getCancelButton() {
    return this.cancelButton;
  }
  
  public void showConfirmRemovePersonalInfo(ClickHandler onConfirmRemovePersonalInfo) {
    ConfirmDeleteDialog.show("This will delete the user's first name, last name, organization, and personal ID. Are you sure you want to delete all personal information for this user?", onConfirmRemovePersonalInfo);
  }
  
  public void showConfirmDelete(ClickHandler onConfirmDelete) {
    ConfirmDeleteDialog.show("Are you sure you want to permanently delete this user?", onConfirmDelete);
  }
}
