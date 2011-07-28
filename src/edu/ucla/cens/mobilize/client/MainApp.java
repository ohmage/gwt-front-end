package edu.ucla.cens.mobilize.client;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TabPanel;

import edu.ucla.cens.mobilize.client.common.TokenLoginManager;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
//import edu.ucla.cens.mobilize.client.dataaccess.MockDataService;
import edu.ucla.cens.mobilize.client.dataaccess.AndWellnessDataService;
import edu.ucla.cens.mobilize.client.event.CampaignDataChangedEvent;
import edu.ucla.cens.mobilize.client.event.CampaignDataChangedEventHandler;
import edu.ucla.cens.mobilize.client.event.UserInfoUpdatedEvent;
import edu.ucla.cens.mobilize.client.exceptions.AuthenticationException;
import edu.ucla.cens.mobilize.client.presenter.AccountPresenter;
import edu.ucla.cens.mobilize.client.presenter.CampaignPresenter;
import edu.ucla.cens.mobilize.client.presenter.ClassPresenter;
import edu.ucla.cens.mobilize.client.presenter.DashboardPresenter;
import edu.ucla.cens.mobilize.client.presenter.DocumentPresenter;
import edu.ucla.cens.mobilize.client.presenter.ExploreDataPresenter;
import edu.ucla.cens.mobilize.client.presenter.LoginPresenter;
import edu.ucla.cens.mobilize.client.presenter.ResponsePresenter;
import edu.ucla.cens.mobilize.client.ui.Header;
import edu.ucla.cens.mobilize.client.view.AccountViewImpl;
import edu.ucla.cens.mobilize.client.view.CampaignView;
import edu.ucla.cens.mobilize.client.view.CampaignViewImpl;
import edu.ucla.cens.mobilize.client.view.ClassView;
import edu.ucla.cens.mobilize.client.view.ClassViewImpl;
import edu.ucla.cens.mobilize.client.view.DashboardViewImpl;
import edu.ucla.cens.mobilize.client.view.DocumentView;
import edu.ucla.cens.mobilize.client.view.DocumentViewImpl;
import edu.ucla.cens.mobilize.client.view.ExploreDataView;
import edu.ucla.cens.mobilize.client.view.ExploreDataViewImpl;
import edu.ucla.cens.mobilize.client.view.HelpView;
import edu.ucla.cens.mobilize.client.view.LoginView;
import edu.ucla.cens.mobilize.client.view.LoginViewImpl;
import edu.ucla.cens.mobilize.client.view.ResponseView;
import edu.ucla.cens.mobilize.client.view.ResponseViewImpl;

import edu.ucla.cens.mobilize.client.model.UserInfo;

/**
 * Main controller. Creates views, presenters, and eventbus and 
 * wires them together. Responds to top-level navigation events
 * (e.g., selection changes in the tab panel.) Maps history tokens
 * to views. 
 */
@SuppressWarnings("deprecation")
public class MainApp implements EntryPoint, HistoryListener {
  
  // event management
  EventBus eventBus = new SimpleEventBus();
  
  // classes for accessing data store
  //DataService mockDataService = new MockDataService(); // for testing new data methods
  DataService awDataService = new AndWellnessDataService();
  
  // login management
  TokenLoginManager loginManager = new TokenLoginManager(eventBus);
  LoginView loginView;
  LoginPresenter loginPresenter;
  UserInfo userInfo;

	// navigation
  DockLayoutPanel mainDockLayoutPanel;
  Header header;
	TabPanel tabPanel;
	ArrayList<String> tabHistoryTokens;
  Map<String, List<String>> urlParams = new HashMap<String, List<String>>();
	
	// views
	DashboardViewImpl dashboardView;
	CampaignView campaignView;
	ResponseView responseView;
	ExploreDataView exploreDataView;
	DocumentView documentView;
	ClassView classView;
	AccountViewImpl accountView;
	HelpView helpView;
	
	// presenters
	DashboardPresenter dashboardPresenter;
	CampaignPresenter campaignPresenter;
	ResponsePresenter responsePresenter;
	ExploreDataPresenter exploreDataPresenter;
	DocumentPresenter documentPresenter;
  ClassPresenter classPresenter;
  AccountPresenter accountPresenter;

  // convenience class for readability
  private static class TabIndex {
    public static int DASHBOARD, CAMPAIGNS, RESPONSES, EXPLORE_DATA, DOCUMENTS, CLASSES = 0;
  }
  
  // Logging utility
  private static Logger _logger = Logger.getLogger(MainApp.class.getName());	
	
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    // MobilizeWeb.html#logout will log user out immediately (useful for troubleshooting)
    String initialToken = History.getToken(); 
    if ("logout".equals(initialToken)) {
      logout(); // logout and refresh
    } 
    
    if (loginManager.isCurrentlyLoggedIn()) {
      initDataService(loginManager.getLoggedInUserName(), loginManager.getAuthorizationToken());
      initUser();
    } else {
      initLogin();
    }
    
    // if user is looking at explore_data tab when the window is resized,
    // refresh so the plot will be redrawn in the new size
    // FIXME: is this fired continuously while the window resizes or just at
    // the end? (don't want to send a big stream of unneeded queries to the db...)
    /*
    Window.addResizeHandler(new ResizeHandler() {
      @Override
      public void onResize(ResizeEvent event) {
        if (History.getToken().contains("explore_data")) {
          History.fireCurrentHistoryState();
        }
      }
    });*/
    
    bind();
  }
  
  private void bind() {
    eventBus.addHandler(CampaignDataChangedEvent.TYPE, new CampaignDataChangedEventHandler() {
      @Override
      public void onCampaignDataChanged(CampaignDataChangedEvent event) {
        refreshUserInfo(); // because campaigns listed in userInfo may have changed
      }
    });
  }
  
  // must be called before any data service fetches so data access class
  // has the auth token
  private void initDataService(String userName, String authToken) {
    this.awDataService.init(userName, authToken);
  }
  
  private void initAppForUser(UserInfo userInfo) {
    this.userInfo = userInfo;
    initComponents(userInfo);
    initLayoutAndNavigation();
    initHistory();
  }
  
  private void initHistory() {
    String initialToken = History.getToken();
    if (initialToken.length() == 0) {
      History.newItem("dashboard", false);
    }
    History.addHistoryListener(this);
    History.fireCurrentHistoryState();
  }
  
  private void initLogin() {
    loginView = new LoginViewImpl();
    loginPresenter = new LoginPresenter(awDataService, eventBus, loginView, loginManager);
    RootLayoutPanel.get().add(loginView);
  }
  
  private void initUser() {
    if (!loginManager.isCurrentlyLoggedIn()) {
      _logger.warning("Cannot fetch user info if not logged in.");
    }
    
    // set up data service for this user
    final String userName = loginManager.getLoggedInUserName();
    final String authToken = loginManager.getAuthorizationToken();
    awDataService.init(userName, authToken);
    
    // get user info 
    awDataService.fetchUserInfo(userName, new AsyncCallback<UserInfo>() {

      @Override
      public void onFailure(Throwable caught) {
        // auth exception would be thrown here if user still has login cookie 
        // in the browser but has been logged out on the server
        if (caught.getClass().equals(AuthenticationException.class)) {
          logout(); // update cookie and refresh so user sees login page
        } else {
          _logger.severe("Failed to fetch user info: " + caught.getMessage());
        }
      }

      @Override
      public void onSuccess(UserInfo user) {
        if (user != null) {
          initAppForUser(user);
        } else {
          _logger.severe("Failed to fetch user info for user " + userName);
        }
      }
    });
  }

  // useful for responding to an event that might have changed userinfo data
  private void refreshUserInfo() {
    final String username = userInfo.getUserName();
    awDataService.fetchUserInfo(username, new AsyncCallback<UserInfo>() {
      @Override
      public void onFailure(Throwable caught) {
        _logger.severe("Failed to fetch info for user " + username);
      }

      @Override
      public void onSuccess(UserInfo result) {
        // save new userInfo object
        userInfo = result; 
        // notify all subscribed presenters that userInfo has changed
        eventBus.fireEvent(new UserInfoUpdatedEvent(result));
      }
    });
  }
  
  private void initComponents(UserInfo userInfo) {
    header = new Header();
    tabPanel = new TabPanel();
    tabHistoryTokens = new ArrayList<String>();
    
    // views
    dashboardView = new DashboardViewImpl();
    campaignView = new CampaignViewImpl();
    responseView = new ResponseViewImpl();
    exploreDataView = new ExploreDataViewImpl();
    documentView = new DocumentViewImpl();
    classView = new ClassViewImpl();
    accountView = new AccountViewImpl();
    helpView = new HelpView();
    
    // presenters
    dashboardPresenter = new DashboardPresenter(userInfo, awDataService, eventBus);
    campaignPresenter = new CampaignPresenter(userInfo, awDataService, eventBus);
    responsePresenter = new ResponsePresenter(userInfo, awDataService, eventBus);
    exploreDataPresenter = new ExploreDataPresenter(userInfo, awDataService, eventBus);
    documentPresenter = new DocumentPresenter(userInfo, awDataService, eventBus);
    classPresenter = new ClassPresenter(userInfo, awDataService, eventBus);
    accountPresenter = new AccountPresenter(userInfo, awDataService, eventBus);

    // connect views and presenters
    dashboardPresenter.setView(dashboardView);
    campaignPresenter.setView(campaignView);
    responsePresenter.setView(responseView);
    exploreDataPresenter.setView(exploreDataView);
    documentPresenter.setView(documentView);
    classPresenter.setView(classView);
    accountPresenter.setView(accountView);
  }
  
  private void initLayoutAndNavigation() {
    header.setAppName("MOBILIZE"); // FIXME: dynamic based on config
    header.setUserName(loginManager.getLoggedInUserName());
    
    // create tabs (use class to keep track of tab index b/c it may change for different users)
    int index = 0;
    tabPanel.add(dashboardView, "Dashboard");
    tabHistoryTokens.add("dashboard");
    TabIndex.DASHBOARD = index++;
    tabPanel.add(campaignView, "Campaigns");
    tabHistoryTokens.add("campaigns");    
    TabIndex.CAMPAIGNS = index++;
    tabPanel.add(responseView, "Responses");
    tabHistoryTokens.add("responses");
    TabIndex.RESPONSES = index++;
    tabPanel.add(exploreDataView, "Explore Data");
    tabHistoryTokens.add("explore_data");
    TabIndex.EXPLORE_DATA = index++;
    tabPanel.add(documentView, "Documents");
    tabHistoryTokens.add("documents");
    TabIndex.DOCUMENTS = index++;
    tabPanel.add(classView, "Classes");
    tabHistoryTokens.add("classes");
    TabIndex.CLASSES = index++;

    // Clicking on a tab fires history token. tab will be selected when the 
    // history token is processed. NOTE: app used to use TabListener but it
    // didn't play well with History management
    for (int i = 0; i < tabPanel.getTabBar().getTabCount(); i++) {
      final int tabIndex = i;
      tabPanel.getTabBar().getTab(i).addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          History.newItem(tabHistoryTokens.get(tabIndex));
        }
      });
    }
    
    mainDockLayoutPanel = new DockLayoutPanel(Unit.PX);
    mainDockLayoutPanel.addNorth(header, 66);
    mainDockLayoutPanel.add(tabPanel);
    RootLayoutPanel.get().add(mainDockLayoutPanel);

    // NOTE: tabPanel, accountView, and helpView all live in the center
    // panel of the dock layout but are attached/removed so only one
    // is visible at a time
  }
  
  /******** Methods to control visible widgets ************/
  private void showDashboard() {
    setMainContentTabPanel();    
    tabPanel.selectTab(TabIndex.DASHBOARD);
  }
 
  private void showCampaigns() {
    setMainContentTabPanel();
    tabPanel.selectTab(TabIndex.CAMPAIGNS);
  }
  
  private void showResponses() {
    setMainContentTabPanel();
    tabPanel.selectTab(TabIndex.RESPONSES);
  }
  
  private void showExploreData() {
    setMainContentTabPanel();
    tabPanel.selectTab(TabIndex.EXPLORE_DATA);
  }
  
  private void showDocuments() {
    setMainContentTabPanel();
    tabPanel.selectTab(TabIndex.DOCUMENTS);
  }
  
  private void showClasses() {
    setMainContentTabPanel();
    tabPanel.selectTab(TabIndex.CLASSES);
  }

  private void showAccount() {
    setMainContentAccountView();
    accountView.setVisible(true);
  }
  
  private void showHelp() {
    setMainContentHelpView();
    helpView.setVisible(true);
  }
    
  private void setMainContentTabPanel() {
    if (accountView.isAttached()) accountView.removeFromParent();
    if (helpView.isAttached()) helpView.removeFromParent();
    if (!tabPanel.isAttached()) mainDockLayoutPanel.add(tabPanel);
  }
  
  private void setMainContentAccountView() {
    if (tabPanel.isAttached()) tabPanel.removeFromParent();
    if (helpView.isAttached()) helpView.removeFromParent();
    if (!accountView.isAttached()) mainDockLayoutPanel.add(accountView);
  }
  
  private void setMainContentHelpView() {
    if (tabPanel.isAttached()) tabPanel.removeFromParent();
    if (accountView.isAttached()) accountView.removeFromParent();
    if (!helpView.isAttached()) mainDockLayoutPanel.add(helpView);
  }
  
  /******** End methods to control visible widgets ********/

  /********* History Management ***********/
  @Override
  public void onHistoryChanged(String historyToken) {
    String view = extractView(historyToken); 
    final Map<String, String> params = extractParams(historyToken);

    if (view.equals("dashboard")) {
      dashboardPresenter.go(params);
      showDashboard();
    } else if (view.equals("campaigns")) {
      // visible subview and filters set from params
      campaignPresenter.go(params);
      showCampaigns();
    } else if (view.equals("responses")) {
      // filters set from params
      responsePresenter.go(params);
      showResponses();
    } else if (view.equals("explore_data")) {
      // filters and data set from params
      showExploreData();
      // waits until page is fully loaded before calling go() because plot 
      // height/width are calculated from dom element size
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {
        @Override
        public void execute() {
          exploreDataPresenter.go(params);
        }
      });
    } else if (view.equals("documents")) {
      documentPresenter.go(params);
      showDocuments();
    } else if (view.equals("classes")) {
      classPresenter.go(params);
      showClasses();
    } else if (view.equals("account")) {
      accountPresenter.go(params);
      showAccount();
    } else if (view.equals("help")) {
      showHelp();
    } else if (view.equals("logout")) {
      logout();
    }
  }
  
  private void logout() {
    loginManager.logOut();
    History.newItem("dashboard", false); // logging in again will show dashboard
    Window.Location.reload(); // reload to clear state
  }
  
  private String extractView(String historyToken) {
    return historyToken.split("\\?")[0]; // everything before the ?
  }
  
  private Map<String, String> extractParams(String historyToken) {
    Map<String, String> params = new HashMap<String, String>();
    if (historyToken.contains("?")) {
      String[] paramPairs = historyToken.split("\\?")[1].split("&");
      String paramName = null, paramValue = null;
      // todo: sanitize input?
      for (String paramNameAndValue : paramPairs) {
        if (paramNameAndValue.contains("=")) {
          String[] nameAndValueArray = paramNameAndValue.split("=");
          paramName = nameAndValueArray[0];
          paramValue = (nameAndValueArray.length > 1) ? nameAndValueArray[1] : ""; // value is empty string if no chars after "="
        } else {
          // if just "myparam" instead of "myparam=value" treat 
          // param as a flag set to true
          paramName = paramNameAndValue;
          paramValue = "true";
        }
        params.put(paramName, paramValue);
      }
    }
    return params;
  }
  
  /********* End of History Management ***********/
}
