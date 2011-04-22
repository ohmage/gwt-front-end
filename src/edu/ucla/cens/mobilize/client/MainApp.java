package edu.ucla.cens.mobilize.client;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;

import edu.ucla.cens.mobilize.client.common.TokenLoginManager;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.MockDataService;
import edu.ucla.cens.mobilize.client.dataaccess.AndWellnessDataService;
import edu.ucla.cens.mobilize.client.presenter.CampaignPresenter;
import edu.ucla.cens.mobilize.client.presenter.ClassPresenter;
import edu.ucla.cens.mobilize.client.presenter.DashboardPresenter;
import edu.ucla.cens.mobilize.client.presenter.ExploreDataPresenter;
import edu.ucla.cens.mobilize.client.presenter.LoginPresenter;
import edu.ucla.cens.mobilize.client.presenter.ResponsePresenter;
import edu.ucla.cens.mobilize.client.ui.Header;
import edu.ucla.cens.mobilize.client.view.AccountView;
import edu.ucla.cens.mobilize.client.view.CampaignView;
import edu.ucla.cens.mobilize.client.view.CampaignViewImpl;
import edu.ucla.cens.mobilize.client.view.ClassView;
import edu.ucla.cens.mobilize.client.view.ClassViewImpl;
import edu.ucla.cens.mobilize.client.view.DashboardViewImpl;
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
public class MainApp implements EntryPoint, TabListener, HistoryListener {
  
  // event management
  EventBus eventBus = new SimpleEventBus();
  
  // classes for accessing data store
  DataService dataService = new MockDataService(); // FIXME: use real service
  DataService awDataService = new AndWellnessDataService();
  
  // login management
  TokenLoginManager loginManager = new TokenLoginManager(eventBus);
  LoginView loginView;
  LoginPresenter loginPresenter;
  UserInfo userInfo;

	// navigation
  Header header;
	TabPanel tabPanel;
	ArrayList<String> tabHistoryTokens;
  Map<String, List<String>> urlParams = new HashMap<String, List<String>>();
	
	// views
	DashboardViewImpl dashboardView;
	CampaignView campaignView;
	ResponseView responseView;
	ExploreDataView exploreDataView;
	ClassView classView;
	AccountView accountView;
	HelpView helpView;
	
	// presenters
	DashboardPresenter dashboardPresenter;
	CampaignPresenter campaignPresenter;
	ResponsePresenter responsePresenter;
	ExploreDataPresenter exploreDataPresenter;
  ClassPresenter classPresenter;
	
  // Logging utility
  private static Logger _logger = Logger.getLogger(MainApp.class.getName());	
	
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
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
    RootPanel.get("main-content").add(loginView);
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
        _logger.severe("Failed to fetch user info for user " + userName);
        // TODO: show error message to user
      }

      @Override
      public void onSuccess(UserInfo user) {
        if (user != null) {
          initAppForUser(user);
        } else {
          _logger.severe("Failed to fetch user info for user " + userName);
          // TODO: show error message to user
        }
      }
    });
  }
  
  private void initComponents(UserInfo userInfo) {
    header = new Header();
    tabPanel = new TabPanel();
    tabPanel.addTabListener(this);
    tabHistoryTokens = new ArrayList<String>();
    
    // views
    dashboardView = new DashboardViewImpl();
    campaignView = new CampaignViewImpl();
    responseView = new ResponseViewImpl();
    exploreDataView = new ExploreDataViewImpl();
    classView = new ClassViewImpl();
    accountView = new AccountView();
    helpView = new HelpView();
    
    // presenters
    dashboardPresenter = new DashboardPresenter(userInfo);
    campaignPresenter = new CampaignPresenter(userInfo, awDataService, eventBus);
    responsePresenter = new ResponsePresenter(userInfo, dataService, eventBus);
    exploreDataPresenter = new ExploreDataPresenter();
    classPresenter = new ClassPresenter();

    // connect views and presenters
    dashboardPresenter.setView(dashboardView);
    campaignPresenter.setView(campaignView);
    campaignPresenter.showAllCampaigns();
    responsePresenter.setView(responseView);
    responsePresenter.onFilterChange();
    exploreDataPresenter.setView(exploreDataView);
    classPresenter.setView(classView);
  }
  
  private void initLayoutAndNavigation() {
    header.setAppName("MOBILIZE"); // FIXME: dynamic based on config
    header.setUserName(loginManager.getLoggedInUserName());
    RootPanel.get("header").add(header);
    
    // create tabs
    tabPanel.add(dashboardView, "Dashboard"); // 0 = dashboard
    tabPanel.add(campaignView, "Campaigns"); // 1 = campaigns
    tabPanel.add(responseView, "Responses"); // 2 = responses
    tabPanel.add(exploreDataView, "Explore Data"); // 3 = explore data
    tabPanel.add(classView, "Classes"); // 4 = classes

    // the nth string in tabHistoryTokens corresponds to the nth tab
    tabHistoryTokens.add("dashboard");
    tabHistoryTokens.add("campaigns");
    tabHistoryTokens.add("responses");
    tabHistoryTokens.add("explore_data");
    tabHistoryTokens.add("classes");
    
    // tab panel, account page, and help all appear in the same
    // place but only one of them will be visible at a time
    RootPanel.get("main-content").add(tabPanel);
    RootPanel.get("main-content").add(accountView);
    RootPanel.get("main-content").add(helpView);

  }
  
  /******** Methods to control visible widgets ************/
  private void showDashboard() {
    hideAll();
    tabPanel.setVisible(true);
    if (tabPanel.getTabBar().getSelectedTab() != 0) {
      tabPanel.selectTab(0);
    }
  }
 
  private void showCampaigns() {
    hideAll();
    tabPanel.setVisible(true);
    if (tabPanel.getTabBar().getSelectedTab() != 1) {
      tabPanel.selectTab(1);
    }
  }
  
  private void showResponses() {
    hideAll();
    tabPanel.setVisible(true);
    if (tabPanel.getTabBar().getSelectedTab() != 2) {
      tabPanel.selectTab(2);
    }
  }
  
  private void showExploreData() {
    hideAll();
    tabPanel.setVisible(true);
    if (tabPanel.getTabBar().getSelectedTab() != 3) {
      tabPanel.selectTab(3);
    }
  }
  
  private void showClasses() {
    hideAll();
    tabPanel.setVisible(true);
    if (tabPanel.getTabBar().getSelectedTab() != 4) {
      tabPanel.selectTab(4);
    }
  }

  private void showAccount() {
    hideAll();
    accountView.setVisible(true);
  }
  
  private void showHelp() {
    hideAll();
    helpView.setVisible(true);
  }
  
  private void hideAll() {
    tabPanel.setVisible(false);
    accountView.setVisible(false);
    helpView.setVisible(false);
  }
  
  /******** End methods to control visible widgets ********/

  /********* Top Level Navigation ****************/
  @Override
  public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
    return true;
  }

  @Override
  public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
    try {
      // saves token to enabled navigating back but does not fire event
      //History.newItem(tabHistoryTokens.get(tabIndex), false); // false broke browse campaigns link 
      
      History.newItem(tabHistoryTokens.get(tabIndex));
      
    } catch (Exception e) {
      // FIXME: error handling
    }
  }
  /********* End of Top Level Navigation *********/
  
  /********* History Management ***********/
  @Override
  public void onHistoryChanged(String historyToken) {
    String view = extractView(historyToken); 
    Map<String, List<String>> params = extractParams(historyToken);

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
      exploreDataPresenter.go(params);
      showExploreData();
    } else if (view.equals("classes")) {
      classPresenter.go(params);
      showClasses();
    } else if (view.equals("account")) {
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
  
  private Map<String, List<String>> extractParams(String historyToken) {
    Map<String, List<String>> params = new HashMap<String, List<String>>();
    if (historyToken.contains("?")) {
      String[] paramPairs = historyToken.split("\\?")[1].split("&");
      String paramName = null, paramValue = null;
      // todo: sanitize input?
      for (String paramNameAndValue : paramPairs) {
        if (paramNameAndValue.contains("=")) {
          String[] nameAndValueArray = paramNameAndValue.split("=");
          paramName = nameAndValueArray[0];
          paramValue = nameAndValueArray[1];
        } else {
          // if just "myparam" instead of "myparam=value" treat 
          // param as a flag set to true
          paramName = paramNameAndValue;
          paramValue = "true";
        }
        if (!params.containsKey(paramName)) {
          params.put(paramName, new ArrayList<String>());
        }
        params.get(paramName).add(paramValue);
      }
    }
    return params;
  }
  
  /********* End of History Management ***********/
}
