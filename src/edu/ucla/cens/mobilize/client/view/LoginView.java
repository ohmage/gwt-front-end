package edu.ucla.cens.mobilize.client.view;

import com.google.gwt.user.client.ui.IsWidget;

public interface LoginView extends IsWidget {
    // Any Presenter that uses this View must implement these functions to
    // handle events from the View
    public interface Presenter {
        void onSubmit();
    }
  
    // Getters
    String getUserName();
    String getPassword();
    
    // Changes the appearance of the LoginBox
    void setLoginFailed(String msg);
    
    // Sets the presenter so the view can call the presenter in response to events
    void setPresenter(Presenter presenter);
}
