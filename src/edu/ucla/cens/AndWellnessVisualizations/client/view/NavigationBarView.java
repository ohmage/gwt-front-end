package edu.ucla.cens.AndWellnessVisualizations.client.view;

import com.google.gwt.user.client.ui.Widget;


public interface NavigationBarView {
    // Any Presenter that uses this View must implement these functions to
    // handle events from the View
    public interface Presenter {
        void logoutClicked();
    }
  
    // Sets the presenter so the view can call the presenter in response to events
    void setPresenter(Presenter presenter);
    
    // Changes the set user name
    void updateUserName(String userName);
    
    // Changes the logged in status
    void setLoggedIn(boolean loggedIn);
    
    // Change a link to have the active css style
    void setActiveStyle(int activeIndex);
    
    // Refreshes the nav bar with login/logout changes or new user changes
    void rebuild();
    
    Widget asWidget();
}
