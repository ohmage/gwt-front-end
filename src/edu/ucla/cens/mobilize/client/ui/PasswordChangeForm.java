package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PasswordChangeForm extends Composite {

  private static PasswordChangeDialogUiBinder uiBinder = GWT
      .create(PasswordChangeDialogUiBinder.class);

  interface PasswordChangeDialogUiBinder extends
      UiBinder<Widget, PasswordChangeForm> {
  }

  @UiField InlineLabel username;
  @UiField PasswordTextBox adminPasswordTextBox;
  @UiField TextBox newPasswordTextBox;
  @UiField Button changePasswordButton;
  @UiField Button cancelButton;
  
  public PasswordChangeForm() {
    initWidget(uiBinder.createAndBindUi(this));
  }
  
  public void clear() {
    this.username.setText("");
    this.adminPasswordTextBox.setText("");
    this.newPasswordTextBox.setText("");
  }
  
  public void setUsername(String username) {
    this.username.setText(username);
  }

  public String getAdminPassword() {
    return this.adminPasswordTextBox.getText();
  }
  
  public String getNewPassword() {
    return this.newPasswordTextBox.getText();
  }
  
  public HasClickHandlers getChangePasswordButton() {
    return this.changePasswordButton;
  }
  
  public HasClickHandlers getCancelButton() {
    return this.cancelButton;
  }
  
}
