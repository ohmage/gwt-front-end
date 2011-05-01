package edu.ucla.cens.mobilize.client.view;

import com.google.gwt.user.client.ui.IsWidget;

public interface AccountView extends IsWidget {

  public interface Presenter {
    void onPasswordChange();
    void onPasswordChangeSubmit();
    void setView(AccountView view);
  }
  
  void setPresenter(Presenter presenter);
  void showPasswordChangeForm();
  void showUserDetails(String login, String email);
  
}