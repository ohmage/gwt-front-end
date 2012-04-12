package edu.ucla.cens.mobilize.client.view;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;

public interface LoginView extends IsWidget {
    // Any Presenter that uses this View must implement these functions to
    // handle events from the View
    public interface Presenter {
        void onSubmit();
    }
  
    // Getters
    String getUsername();
    String getPassword();
    
    // Changes the appearance of the LoginBox
    void setLoginFailed(String msg);
    void showError(String errorMsg);
    
    // Sets the presenter so the view can call the presenter in response to events
    void setPresenter(Presenter presenter);
    void disableLoginForm();
    void enableLoginForm();
    
    // Login Notification Message
    void setNotificationMessage(String message);
    
    // Registration
    void setSelfRegistrationEnabled(boolean isEnabled);
    void setLoginRecoveryEnabled(boolean isEnabled);
}
