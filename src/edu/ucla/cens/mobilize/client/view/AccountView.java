package edu.ucla.cens.mobilize.client.view;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

public interface AccountView extends IsWidget {

  public interface Presenter {
    void setView(AccountView view);
  }
  
  void setUserName(String userName);
  void setCanCreate(boolean canCreate);
  void clearClassList();
  void addClass(String classId, String string);

  void showPasswordChangeForm();
  void hidePasswordChangeForm();
  void showPasswordMismatchError();
  void showMessage(String message);
  void showError(String message);
  void hideMessage();
  
  HasClickHandlers getPasswordChangeButton();
  HasClickHandlers getPasswordChangeSubmitButton();

  String getUserName();
  String getOldPassword();
  String getNewPassword();
  String getNewPasswordConfirm();
  
}