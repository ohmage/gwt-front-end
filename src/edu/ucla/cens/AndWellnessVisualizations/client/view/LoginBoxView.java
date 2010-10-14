package edu.ucla.cens.AndWellnessVisualizations.client.view;

import com.google.gwt.user.client.ui.Widget;

public interface LoginBoxView {
    // Any Presenter that uses this View must implement these functions to
    // handle events from the View
    public interface Presenter {
        void onUserNameChange(String userName);
        void onPasswordChange(String password);
        void onLoginButtonClicked();
    }
  
    // Changes the appearance of the LoginBox
    void setLoginFailed();
    void setInvalidUserName(String message);
    void setInvalidPassword(String message);
    
    // Sets the presenter so the view can call the presenter in response to events
    void setPresenter(Presenter presenter);
    Widget asWidget();
}
