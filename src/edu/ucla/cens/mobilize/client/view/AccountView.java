package edu.ucla.cens.mobilize.client.view;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.IsWidget;

public interface AccountView extends IsWidget {

  public interface Presenter {
    void setView(AccountView view);
  }
  
  void setUserName(String userName);
  void setEmail(String email);
  void setCanCreate(boolean canCreate);
  void clearClassList();
  void addClass(String classId, String string);

  void showPasswordChangeForm();
  void hidePasswordChangeForm();
  void resetPasswordChangeForm();
  void enablePasswordChangeForm();
  void disablePasswordChangeForm();
  void showWaitIndicator();
  void hideWaitIndicator();
  void showMessage(String message);
  void showError(String message, String detail);
  void hideMessage();

  void setPasswordChangeSubmitHandler(SubmitHandler handler);
  HasClickHandlers getPasswordChangeButton();
  HasClickHandlers getPasswordChangeSubmitButton();
  HasClickHandlers getPasswordChangeCancelButton();

  String getUserName();
  String getEmail();
  String getOldPassword();
  String getNewPassword();
  String getNewPasswordConfirm();
  
}