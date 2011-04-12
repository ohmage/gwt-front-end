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
import edu.ucla.cens.mobilize.client.presenter.CampaignPresenter;
import edu.ucla.cens.mobilize.client.presenter.DashboardPresenter;
import edu.ucla.cens.mobilize.client.presenter.ExploreDataPresenter;
import edu.ucla.cens.mobilize.client.presenter.LoginPresenter;
import edu.ucla.cens.mobilize.client.presenter.ResponsePresenter;
import edu.ucla.cens.mobilize.client.rpcservice.AndWellnessRpcService;
import edu.ucla.cens.mobilize.client.rpcservice.ServerAndWellnessRpcService;
import edu.ucla.cens.mobilize.client.ui.Header;
import edu.ucla.cens.mobilize.client.view.AccountView;
import edu.ucla.cens.mobilize.client.view.CampaignView;
import edu.ucla.cens.mobilize.client.view.CampaignViewImpl;
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
  AndWellnessRpcService rpcService = new ServerAndWellnessRpcService();  
  DataService dataService = new MockDataService(); // FIXME: use real service
  
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
	AccountView accountView;
	HelpView helpView;
	
	// presenters
	DashboardPresenter dashboardPresenter;
	CampaignPresenter campaignPresenter;
	ResponsePresenter responsePresenter;
	ExploreDataPresenter exploreDataPresenter;

  // Logging utility
  private static Logger _logger = Logger.getLogger(MainApp.class.getName());	
	
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    if (loginManager.isCurrentlyLoggedIn()) {
      initUser();
      tabPanel.addTabListener(this);
    } else {
      initLogin();
    }
  }
  
  private void initAppForUser(UserInfo userInfo) {
    this.userInfo = userInfo;
    initComponents();
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
    loginPresenter = new LoginPresenter(rpcService, eventBus, loginView, loginManager);
    RootPanel.get("main-content").add(loginView);
  }
  
  private void initUser() {    
    final String userName = loginManager.getLoggedInUserName();
    dataService.fetchUserInfo(userName, new AsyncCallback<UserInfo>() {

      @Override
      public void onFailure(Throwable caught) {
        Window.alert(caught.getMessage()); // FIXME
      }

      @Override
      public void onSuccess(UserInfo user) {
        if (user != null) {
          initAppForUser(user);
        } else {
          _logger.severe("Failed to fetch user info for user " + userName + ". Forcing logout.");
          logout();
        }
      }
    });
  }
  
  private void initComponents() {
    header = new Header();
    tabPanel = new TabPanel();
    tabHistoryTokens = new ArrayList<String>();
    
    // views
    dashboardView = new DashboardViewImpl();
    campaignView = new CampaignViewImpl();
    responseView = new ResponseViewImpl();
    exploreDataView = new ExploreDataViewImpl();
    accountView = new AccountView();
    helpView = new HelpView();
    
    // presenters
    dashboardPresenter = new DashboardPresenter(userInfo);
    campaignPresenter = new CampaignPresenter(userInfo, dataService, eventBus);
    responsePresenter = new ResponsePresenter(userInfo, dataService, eventBus);
    exploreDataPresenter = new ExploreDataPresenter();
    
    // FIXME: move this stuff into history mgmt
    dashboardPresenter.setView(dashboardView);
    campaignPresenter.setView(campaignView);
    campaignPresenter.showAllCampaigns();
    responsePresenter.setView(responseView);
    responsePresenter.onFilterChange();
    exploreDataPresenter.setView(exploreDataView);
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

    // the nth string in tabHistoryTokens corresponds to the nth tab
    tabHistoryTokens.add("dashboard");
    tabHistoryTokens.add("campaigns");
    tabHistoryTokens.add("responses");
    tabHistoryTokens.add("explore_data");
    
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
