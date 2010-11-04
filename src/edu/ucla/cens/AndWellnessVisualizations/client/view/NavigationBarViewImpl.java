package edu.ucla.cens.AndWellnessVisualizations.client.view;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class NavigationBarViewImpl extends Composite implements
        NavigationBarView, ClickHandler {
    
    interface NavigationBarStyle extends CssResource {
        String logoutLabel();
        String activeLabel();
    }

    @UiField NavigationBarStyle style;
    
    @UiTemplate("NavigationBarView.ui.xml")
    interface NavigationBarViewUiBinder extends UiBinder<Widget, NavigationBarViewImpl> {}
    private static NavigationBarViewUiBinder uiBinder =
      GWT.create(NavigationBarViewUiBinder.class);
    
    // Logging capability
    private static Logger _logger = Logger.getLogger(NavigationBarViewImpl.class.getName());
    
    // Fields defined in the ui XML
    @UiField HorizontalPanel navigationBarPanel;
    
    // Call the presenter in response to events (user clicks)
    private Presenter presenter;
    
    // Private fields to keep around
    private String userName;
    private boolean loggedIn = false;

    public NavigationBarViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }
    
    /**
     * Updates the displayed user name when logged in.
     * 
     * @param userName The user name to display.
     * 
     */
    public void updateUserName(String userName) {
        this.userName = userName;
        
        // Rebuild the nav bar with the new user name
        rebuild();
    }

    /**
     * Switches this view between logged out and logged in states.
     * 
     * @param loggedIn Logged in or not.
     */
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
        
        // Rebuild the nav bar
        rebuild();
    }
    
    /**
     * Sets the currently active link, generally the page we are on.
     * 
     * @param styleName The number of the link to activate.
     */
    public void setActiveStyle(int activeIndex) {
        Widget activeWidget = navigationBarPanel.getWidget(activeIndex);
        
        // Make sure the widget exists
        if (activeWidget != null ) {
            // Set the widget to active
            activeWidget.setStyleName(style.activeLabel());
        }
    }
    
    /**
     * Re-builds the nav bar based on the current state
     */
    public void rebuild() {
        if (loggedIn) {
            buildLoggedInBar();
        }
        else {
            buildLoggedOutBar();
        }
    }
    
    /**
     * Constructs the logged out version of the navigation bar.
     */
    private void buildLoggedOutBar() {
        // Clear out the old stuff.
        navigationBarPanel.clear();
        
        HTML homeLink = new HTML("<a class=\"home-nav\" href=\"/\">Home</a>", true);
        HTML aboutLink = new HTML("<a class=\"about-nav\" href=\"/about/\">About</a>", true);
        HTML helpLink = new HTML("<a class=\"help-nav\" href=\"/help/\">Help</a>", true);
        HTML loginLink = new HTML("<a class=\"login-nav\" href=\"/login/\">Login</a>", true);
        
        // Add the links to the nav bar
        navigationBarPanel.add(homeLink);
        navigationBarPanel.add(aboutLink);
        navigationBarPanel.add(helpLink);
        navigationBarPanel.add(loginLink);
    }
    
    /**
     * Constructs the logged in version of the navigation bar.
     */
    private void buildLoggedInBar() {
        // Clear out the old stuff
        navigationBarPanel.clear();
        
        Label userNameLabel = new Label();
        userNameLabel.setText("Logged in as " + userName);
        HTML aboutLink = new HTML("<a class=\"about-nav\" href=\"/about/\">About</a>", true);
        HTML helpLink = new HTML("<a class=\"help-nav\" href=\"/help/\">Help</a>", true);
        Label logoutLink = new Label();
        logoutLink.setText("Logout");
        logoutLink.addStyleName(style.logoutLabel());
        logoutLink.addClickHandler(this);
        
        // Add the links to the nav bar
        navigationBarPanel.add(userNameLabel);
        navigationBarPanel.add(aboutLink);
        navigationBarPanel.add(helpLink);
        navigationBarPanel.add(logoutLink);
    }
    
    /**
     * Handles a click on the logout link.
     * 
     * @param event The logout click event.
     */
    public void onClick(ClickEvent event) {
        if (presenter != null) {
            presenter.logoutClicked();
        }
        else {
            _logger.warning("Presenter has not been set.");
        }
    }
    
    /**
     * Sets the presenter to which this view is attached.
     * 
     * @param presenter The presenter that handles this view.
     * 
     */
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }
    
    public Widget asWidget() {
        return this;
    }
}
