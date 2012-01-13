package edu.ucla.cens.mobilize.client.view;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.ui.AdminMenu;
import edu.ucla.cens.mobilize.client.ui.ErrorDialog;

public class AdminUserCreateView extends Composite {

  private static AdminUserCreateViewUiBinder uiBinder = GWT
      .create(AdminUserCreateViewUiBinder.class);

  interface AdminUserCreateViewUiBinder extends
      UiBinder<Widget, AdminUserCreateView> {
  }

  interface AdminUserCreateViewStyles extends CssResource {
    String invalid();
  }

  @UiField AdminUserCreateViewStyles style;
  @UiField AdminMenu adminMenu;
  @UiField Anchor backLink;
  @UiField TextBox usernameTextBox;
  @UiField InlineLabel usernameInvalidMsg;
  @UiField PasswordTextBox passwordTextBox;
  @UiField Button showPasswordButton;
  @UiField TextBox passwordClearTextBox;
  @UiField Button hidePasswordButton;
  @UiField InlineLabel passwordInvalidMsg;
  @UiField PasswordTextBox passwordConfirmTextBox;
  @UiField Button showPasswordConfirmButton;
  @UiField TextBox passwordConfirmClearTextBox;
  @UiField Button hidePasswordConfirmButton;
  @UiField InlineLabel passwordConfirmInvalidMsg;
  @UiField CheckBox isEnabledCheckBox;
  @UiField CheckBox canCreateCampaignsCheckBox;
  @UiField CheckBox isAdminCheckBox;
  @UiField CheckBox isNewAccountCheckBox;
  @UiField Button saveButton;
  @UiField Button cancelButton;
  
  private List<InlineLabel> invalidMsgs;
  private List<TextBox> textBoxes;
  
  public AdminUserCreateView() {
    initWidget(uiBinder.createAndBindUi(this));
    initComponents();
    bind();
  }

  private void initComponents() {
    this.adminMenu.selectCreateUser();
    hidePasswords(); 
    this.textBoxes = Arrays.asList(this.usernameTextBox,
                                   this.passwordTextBox,
                                   this.passwordClearTextBox,
                                   this.passwordConfirmTextBox,
                                   this.passwordConfirmClearTextBox);
    
    this.invalidMsgs = Arrays.asList(this.usernameInvalidMsg,
                                     this.passwordInvalidMsg,
                                     this.passwordConfirmInvalidMsg);
  }
  
  private void bind() {
    this.showPasswordButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        showClearTextPasswords();
      }
    });
    
    this.showPasswordConfirmButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        showClearTextPasswords();
      }
    });
    
    this.hidePasswordButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        hidePasswords();
      }
    });
    
    this.hidePasswordConfirmButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        hidePasswords();
      }
    });
    
    // make sure password box always has up to date password since it's used in form submit
    this.passwordClearTextBox.addKeyUpHandler(new KeyUpHandler() {
      @Override
      public void onKeyUp(KeyUpEvent event) {
        passwordTextBox.setText(passwordClearTextBox.getText());
      }
    });
    
    // make sure password confirm box always has up to date password since it's used in form submit
    this.passwordConfirmClearTextBox.addKeyUpHandler(new KeyUpHandler() {
      @Override
      public void onKeyUp(KeyUpEvent event) {
        passwordConfirmTextBox.setText(passwordConfirmClearTextBox.getText());
      }
    });
  }
  
  private void showClearTextPasswords() {
    // copy values over
    this.passwordClearTextBox.setText(this.passwordTextBox.getText());
    this.passwordConfirmClearTextBox.setText(this.passwordConfirmTextBox.getText());
    // clear text boxes visible
    this.passwordClearTextBox.setVisible(true);
    this.hidePasswordButton.setVisible(true);
    this.passwordConfirmClearTextBox.setVisible(true);
    this.hidePasswordConfirmButton.setVisible(true);
    // password text boxes hidden
    this.passwordTextBox.setVisible(false);
    this.passwordConfirmTextBox.setVisible(false);
    this.showPasswordButton.setVisible(false);
    this.showPasswordConfirmButton.setVisible(false);
  }
  
  private void hidePasswords() {
    // values should already have been copied over by the keypress handler
    // hide clear text boxes
    this.passwordClearTextBox.setVisible(false);
    this.hidePasswordButton.setVisible(false);
    this.passwordConfirmClearTextBox.setVisible(false);
    this.hidePasswordConfirmButton.setVisible(false);
    // show password boxes
    this.passwordTextBox.setVisible(true);
    this.passwordConfirmTextBox.setVisible(true);
    this.showPasswordButton.setVisible(true);
    this.showPasswordConfirmButton.setVisible(true);
  }
  
  /**
   * Clear form fields
   */
  public void resetForm() {
    clearInvalidFieldMarkers();
    this.usernameTextBox.setText("");
    this.passwordTextBox.setText("");
    this.passwordConfirmTextBox.setText("");
    hidePasswords();
  }

  // Methods for getting data from form
  
  public String getUsername() {
    return this.usernameTextBox.getText();
  }

  public String getPassword() {
    return this.passwordTextBox.getText();
  }
  
  public String getPasswordConfirm() {
    return this.passwordConfirmTextBox.getText();
  }
  
  public boolean getEnabledFlag() {
    return this.isEnabledCheckBox.getValue();
  }
  
  public boolean getCanCreateCampaignsFlag() {
    return this.canCreateCampaignsCheckBox.getValue();
  }
  
  public boolean getAdminFlag() {
    return this.isAdminCheckBox.getValue();
  }
  
  public boolean getNewAccountFlag() {
    return this.isNewAccountCheckBox.getValue();
  }
  
  // Methods for marking form fields invalid

  public void markUsernameInvalid(String message) {
    this.usernameTextBox.addStyleName(style.invalid());
    this.usernameInvalidMsg.setVisible(true);
    this.usernameInvalidMsg.setText(message);
  }
  
  public void markPasswordInvalid(String message) {
    this.passwordTextBox.addStyleName(style.invalid());
    this.passwordClearTextBox.addStyleName(style.invalid());
    this.passwordInvalidMsg.setVisible(true);
    this.passwordInvalidMsg.setText(message);
  }
  
  public void markPasswordConfirmInvalid(String message) {
    this.passwordConfirmTextBox.addStyleName(style.invalid());
    this.passwordConfirmClearTextBox.addStyleName(style.invalid());
    this.passwordConfirmInvalidMsg.setVisible(true);
    this.passwordConfirmInvalidMsg.setText(message);
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
  
  public HasClickHandlers getSaveButton() {
    return this.saveButton;
  }
  
  public HasClickHandlers getCancelButton() {
    return this.cancelButton;
  }
  
  public void showError(String msg, String detail) {
    ErrorDialog.show(msg, detail);
  }
}
