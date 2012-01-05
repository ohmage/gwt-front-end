package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.Widget;

public class NewAccountPasswordChange extends Composite {

  private static NewAccountPasswordChangeUiBinder uiBinder = GWT
      .create(NewAccountPasswordChangeUiBinder.class);

  interface NewAccountPasswordChangeUiBinder extends
      UiBinder<Widget, NewAccountPasswordChange> {
  }
  
  @UiField Label username;
  @UiField PasswordTextBox currentPassword;
  @UiField PasswordTextBox newPassword;
  @UiField Label reusedPasswordMessage;
  @UiField PasswordTextBox newPasswordConfirm;
  @UiField Label mismatchedPasswordsMessage;
  @UiField SubmitButton changePasswordButton;
  @UiField Button cancelButton;
  
  public NewAccountPasswordChange() {
    initWidget(uiBinder.createAndBindUi(this));
    this.mismatchedPasswordsMessage.setVisible(false);
    this.reusedPasswordMessage.setVisible(false);
  }

  public void reset() {
    this.username.setText("");
    this.currentPassword.setText("");
    this.newPassword.setText("");
    this.newPasswordConfirm.setText("");
    this.clearValidationErrors();
  }
  
  public void setUsername(String username) {
    this.username.setText(username);
  }
  
  public String getUsername() {
    return this.username.getText();
  }

  public String getCurrentPassword() {
    return this.currentPassword.getText();
  }
  
  public String getNewPassword() {
    return this.newPassword.getText();
  }

  public String getNewPasswordConfirm() {
    return this.newPasswordConfirm.getText();
  }
  
  public void showMismatchedPasswordsMessage() {
    this.mismatchedPasswordsMessage.setVisible(true);
  }
  
  public void showReusedPasswordMessage() {
    this.reusedPasswordMessage.setVisible(true);
  }
  
  public void clearValidationErrors() {
    this.mismatchedPasswordsMessage.setVisible(false);
    this.reusedPasswordMessage.setVisible(false);
  }
  
  public HasClickHandlers getChangePasswordButton() {
    return this.changePasswordButton;
  }
  
  public HasClickHandlers getCancelButton() {
    return this.cancelButton;
  }
}
