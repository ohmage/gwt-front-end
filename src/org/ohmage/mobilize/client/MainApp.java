package org.ohmage.mobilize.client;

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
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TabPanel;

import org.ohmage.mobilize.client.common.HistoryTokens;
import org.ohmage.mobilize.client.common.TokenLoginManager;
import org.ohmage.mobilize.client.dataaccess.DataService;
import org.ohmage.mobilize.client.dataaccess.MockDataService;
import org.ohmage.mobilize.client.dataaccess.AndWellnessDataService;
import org.ohmage.mobilize.client.dataaccess.requestparams.CampaignReadParams;
import org.ohmage.mobilize.client.event.CampaignDataChangedEvent;
import org.ohmage.mobilize.client.event.CampaignDataChangedEventHandler;
import org.ohmage.mobilize.client.event.CampaignInfoUpdatedEvent;
import org.ohmage.mobilize.client.event.ClassDataChangedEvent;
import org.ohmage.mobilize.client.event.ClassDataChangedEventHandler;
import org.ohmage.mobilize.client.event.UserInfoUpdatedEvent;
import org.ohmage.mobilize.client.presenter.AccountPresenter;
import org.ohmage.mobilize.client.presenter.AdminAuditLogPresenter;
import org.ohmage.mobilize.client.presenter.AdminClassDetailPresenter;
import org.ohmage.mobilize.client.presenter.AdminClassEditPresenter;
import org.ohmage.mobilize.client.presenter.AdminClassListPresenter;
import org.ohmage.mobilize.client.presenter.AdminPresenter;
import org.ohmage.mobilize.client.presenter.AdminUserCreatePresenter;
import org.ohmage.mobilize.client.presenter.AdminUserDetailPresenter;
import org.ohmage.mobilize.client.presenter.AdminUserEditPresenter;
import org.ohmage.mobilize.client.presenter.AdminUserListPresenter;
import org.ohmage.mobilize.client.presenter.CampaignPresenter;
import org.ohmage.mobilize.client.presenter.ClassPresenter;
import org.ohmage.mobilize.client.presenter.DashboardPresenter;
import org.ohmage.mobilize.client.presenter.DocumentPresenter;
import org.ohmage.mobilize.client.presenter.ExploreDataPresenter;
import org.ohmage.mobilize.client.presenter.LoginPresenter;
import org.ohmage.mobilize.client.presenter.ResponsePresenter;
import org.ohmage.mobilize.client.ui.ErrorDialog;
import org.ohmage.mobilize.client.ui.Header;
import org.ohmage.mobilize.client.ui.LoginRecovery;
import org.ohmage.mobilize.client.ui.LoginSelfRegistration;
import org.ohmage.mobilize.client.utils.AwErrorUtils;
import org.ohmage.mobilize.client.utils.StopWatch;
import org.ohmage.mobilize.client.view.AccountViewImpl;
import org.ohmage.mobilize.client.view.AdminAuditLogView;
import org.ohmage.mobilize.client.view.AdminClassDetailView;
import org.ohmage.mobilize.client.view.AdminClassEditView;
import org.ohmage.mobilize.client.view.AdminClassListView;
import org.ohmage.mobilize.client.view.AdminUserCreateView;
import org.ohmage.mobilize.client.view.AdminUserDetailView;
import org.ohmage.mobilize.client.view.AdminUserListView;
import org.ohmage.mobilize.client.view.AdminUserEditView;
import org.ohmage.mobilize.client.view.AdminView;
import org.ohmage.mobilize.client.view.AdminViewImpl;
import org.ohmage.mobilize.client.view.CampaignView;
import org.ohmage.mobilize.client.view.CampaignViewImpl;
import org.ohmage.mobilize.client.view.ClassView;
import org.ohmage.mobilize.client.view.ClassViewImpl;
import org.ohmage.mobilize.client.view.DashboardViewImpl;
import org.ohmage.mobilize.client.view.DocumentView;
import org.ohmage.mobilize.client.view.DocumentViewImpl;
import org.ohmage.mobilize.client.view.ExploreDataView;
import org.ohmage.mobilize.client.view.ExploreDataViewImpl;
import org.ohmage.mobilize.client.view.HelpView;
import org.ohmage.mobilize.client.view.LoginView;
import org.ohmage.mobilize.client.view.LoginViewAndWellnessImpl;
import org.ohmage.mobilize.client.view.LoginViewMobilizeImpl;
import org.ohmage.mobilize.client.view.LoginViewOhmageImpl;
import org.ohmage.mobilize.client.view.ResponseView;
import org.ohmage.mobilize.client.view.ResponseViewImpl;

import org.ohmage.mobilize.client.model.AppConfig;
import org.ohmage.mobilize.client.model.CampaignShortInfo;
import org.ohmage.mobilize.client.model.UserInfo;

/**
 * Main controller. Creates views, presenters, and eventbus and wires them
 * together. Responds to top-level navigation events (e.g., selection changes in
 * the tab panel.) Maps history tokens to views.
 */
@SuppressWarnings("deprecation")
public class MainApp implements EntryPoint, HistoryListener {

  // event management
  EventBus eventBus = new SimpleEventBus();
  // classes for accessing data store
  DataService mockDataService = new MockDataService(); // for testing new data methods
  DataService awDataService = new AndWellnessDataService();
  // login management
  TokenLoginManager loginManager = new TokenLoginManager(eventBus);
  LoginView loginView;
  LoginPresenter loginPresenter;
  LoginSelfRegistration loginSelfRegistrationView;
  LoginRecovery loginRecovery;
  // data that's used throughout the app
  UserInfo userInfo;
  List<CampaignShortInfo> campaigns;
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
  // admin classes will only be instantiated if user is an admin
  AdminView adminView;
  AdminAuditLogView adminAuditLogView;
  AdminUserListView adminUserListView;
  AdminUserDetailView adminUserDetailView;
  AdminUserEditView adminUserEditView;
  AdminUserCreateView adminUserCreateView;
  AdminClassListView adminClassListView;
  AdminClassDetailView adminClassDetailView;
  AdminClassEditView adminClassEditView;
  AdminPresenter adminPresenter;
  AdminAuditLogPresenter adminAuditLogPresenter;
  AdminUserListPresenter adminUserListPresenter;
  AdminUserDetailPresenter adminUserDetailPresenter;
  AdminUserEditPresenter adminUserEditPresenter;
  AdminUserCreatePresenter adminUserCreatePresenter;
  AdminClassListPresenter adminClassListPresenter;
  AdminClassDetailPresenter adminClassDetailPresenter;
  AdminClassEditPresenter adminClassEditPresenter;

  // convenience class for readability
  private static class TabIndex {

    public static int DASHBOARD, CAMPAIGNS, RESPONSES, EXPLORE_DATA, DOCUMENTS, CLASSES, ADMIN = 0;
  }
  // Logging utility
  private static Logger _logger = Logger.getLogger(MainApp.class.getName());

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    // Initialize History manager *** MUST BE CALLED ***
    initHistory();

    String initialToken = History.getToken();

    // MobilizeWeb.html#logout will log user out immediately (useful for troubleshooting) 
    if ("logout".equals(initialToken)) {
      logout(); // logout and refresh
    }

    // enable stopwatch for debug mode. (all stopwatch methods are no-ops otherwise)
    //if (AwConstants.status.getStatus().equals(Status.DEBUG)) StopWatch.enable();
    //StopWatch.enable();

    if (loginManager.isCurrentlyLoggedIn()) {
      initDataService(loginManager.getLoggedInUserName(), loginManager.getAuthorizationToken());
      loadAppConfigAndInitApp();
      bind();
    } else {
      // NOTE #1: These tokens need to be handled here because they are separate from the main dashboard tokens
      // NOTE #2: History will fire the initial token, so we call History.fireCurrentHistoryState() manually
      if (HistoryTokens.register().equals(extractView(initialToken))) {
        History.fireCurrentHistoryState();
      } else if (HistoryTokens.reset_password().equals(extractView(initialToken))) {
        History.fireCurrentHistoryState();
      } else if (HistoryTokens.activate().equals(extractView(initialToken))) {
        History.fireCurrentHistoryState();
      } else if (HistoryTokens.login().equals(extractView(initialToken))) {
        History.fireCurrentHistoryState();
      } else {	// All unrecognized tokens go to "login"
        History.newItem(HistoryTokens.login());	// Special case: newItem(...) fires automatically
      }
    }
  }

  private void bind() {
    eventBus.addHandler(CampaignDataChangedEvent.TYPE, new CampaignDataChangedEventHandler() {

      @Override
      public void onCampaignDataChanged(CampaignDataChangedEvent event) {
        refreshUserInfo(); // because campaigns listed in userInfo may have changed
        refreshCampaignList();
      }
    });

    eventBus.addHandler(ClassDataChangedEvent.TYPE, new ClassDataChangedEventHandler() {

      @Override
      public void onClassDataChanged(ClassDataChangedEvent event) {
        refreshUserInfo(); // because classes listed in userInfo may have changed
      }
    });
  }

  // must be called before any data service fetches so data access class
  // has the auth token
  private void initDataService(String userName, String authToken) {
    this.awDataService.init(userName, authToken);
  }

  private void loadAppConfigAndInitApp() {
    this.awDataService.fetchAppConfig(new AsyncCallback<AppConfig>() {

      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        ErrorDialog.show("Could not obtain server app config data", "The server may be unavailable or undergoing maintenance. Please try again at a later time.");
      }

      @Override
      public void onSuccess(AppConfig result) {
        Window.setTitle(AppConfig.getAppDisplayName());
        loadUserInfoAndInitApp(result);
      }
    });
  }

  private void initApp(UserInfo userInfo, List<CampaignShortInfo> campaigns) {
    this.userInfo = userInfo;
    this.campaigns = campaigns;
    Window.setTitle(AppConfig.getAppDisplayName());
    try {
      initComponents(userInfo);
    } catch (Exception e) {
      _logger.fine("Exception in initComponents: " + e.getMessage());
    }
    initLayoutAndNavigation(userInfo);

    // Set "dashboard" as first view after login
    History.newItem("dashboard", false);
    History.fireCurrentHistoryState();
  }

  private void initHistory() {
    History.addHistoryListener(this);
  }

  private void initLoginSelfRegistration() {
    this.awDataService.fetchAppConfig(new AsyncCallback<AppConfig>() {

      @Override
      public void onFailure(Throwable caught) {
        ErrorDialog.show("Could not obtain server app config data", "The server may be down or undergoing maintenance. Please try again at a later time.");
      }

      @Override
      public void onSuccess(AppConfig appConfig) {
        if (appConfig.getSelfRegistrationEnabled() == false) {
          showLogin(null);
          return;
        }

        Window.setTitle(AppConfig.getAppDisplayName());
        loginSelfRegistrationView = new LoginSelfRegistration(awDataService);
        RootLayoutPanel.get().add(loginSelfRegistrationView);
      }
    });
  }

  private void initLoginRecovery() {
    this.awDataService.fetchAppConfig(new AsyncCallback<AppConfig>() {

      @Override
      public void onFailure(Throwable caught) {
        ErrorDialog.show("Could not obtain server app config data", "The server may be down or undergoing maintenance. Please try again at a later time.");
      }

      @Override
      public void onSuccess(AppConfig appConfig) {
        Window.setTitle(AppConfig.getAppDisplayName());
        loginRecovery = new LoginRecovery(awDataService);
        RootLayoutPanel.get().add(loginRecovery);
      }
    });
  }

  // Loads app config from db and uses it to construct login page 
  private void initLogin(final String loginMessage) {
    this.awDataService.fetchAppConfig(new AsyncCallback<AppConfig>() {

      @Override
      public void onFailure(Throwable caught) {
        ErrorDialog.show("Could not obtain server app config data", "The server may be down or undergoing maintenance. Please try again at a later time.");
      }

      @Override
      public void onSuccess(AppConfig appConfig) {
        Window.setTitle(AppConfig.getAppDisplayName());
        String appName = AppConfig.getAppName();

        if (appName.equalsIgnoreCase("mobilize")) { // show "mobilize" login page
          loginView = new LoginViewMobilizeImpl();
        } else if (appName.equalsIgnoreCase("ohmage")) { // show "ohmage" login page
          loginView = new LoginViewOhmageImpl();
        } else if (appName.equalsIgnoreCase("andwellness")) { // show "andwellness" login page
          loginView = new LoginViewAndWellnessImpl();
        } else { // DEFAULT: show "ohmage" login page
          loginView = new LoginViewOhmageImpl();
        }

        // Set login message and self registration modes
        loginView.setNotificationMessage(loginMessage);
        loginView.setSelfRegistrationEnabled(appConfig.getSelfRegistrationEnabled());

        loginPresenter = new LoginPresenter(awDataService,
                eventBus,
                loginView,
                loginManager,
                appConfig);
        RootLayoutPanel.get().add(loginView);
      }
    });
  }

  // Loads UserInfo and list of CampaignShortInfos that are passed to presenters and
  // used throughout the app. Updated when a data change event is detected on the event bus.
  // AppConfig should already be loaded.
  private void loadUserInfoAndInitApp(final AppConfig config) {
    if (!loginManager.isCurrentlyLoggedIn()) {
      _logger.warning("Cannot fetch user info if not logged in.");
    }

    // set up data service for this user
    final String userName = loginManager.getLoggedInUserName();
    final String authToken = loginManager.getAuthorizationToken();
    awDataService.init(userName, authToken);

    // Load user info. When done, load campaign info and initialize app.
    awDataService.fetchUserInfo(userName, new AsyncCallback<UserInfo>() {

      @Override
      public void onFailure(Throwable caught) {
        _logger.severe("Failed to fetch user info: " + caught.getMessage());
        logout();
      }

      @Override
      public void onSuccess(UserInfo result) {
        loadCampaignDataAndInitApp(config, result);
      }
    });
  }

  // Depends on userInfo and appConfig.
  private void loadCampaignDataAndInitApp(final AppConfig config, final UserInfo userInfo) {
    awDataService.fetchCampaignListShort(new CampaignReadParams(),
            new AsyncCallback<List<CampaignShortInfo>>() {

              @Override
              public void onFailure(Throwable caught) {
                AwErrorUtils.logoutIfAuthException(caught);
                _logger.severe("Failed to fetch campaign short infos or init app: " + caught.getMessage());
              }

              @Override
              public void onSuccess(List<CampaignShortInfo> campaignInfos) {
                initApp(userInfo, campaignInfos);
              }
            });
  }

  // useful for responding to an event that might have changed userinfo data
  private void refreshUserInfo() {
    final String username = userInfo.getUserName();
    awDataService.fetchUserInfo(username, new AsyncCallback<UserInfo>() {

      @Override
      public void onFailure(Throwable caught) {
        _logger.severe("Failed to fetch info for user " + username
                + "Error was: " + caught.getMessage());
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

  private void refreshCampaignList() {
    awDataService.fetchCampaignListShort(new CampaignReadParams(), new AsyncCallback<List<CampaignShortInfo>>() {

      @Override
      public void onFailure(Throwable caught) {
        _logger.severe("Failed to refresh campaign list: " + caught.getMessage());
      }

      @Override
      public void onSuccess(List<CampaignShortInfo> result) {
        campaigns = result;
        eventBus.fireEvent(new CampaignInfoUpdatedEvent(result));
      }
    });
  }

  private void initComponents(UserInfo userInfo) {
    header = new Header();
    tabPanel = new TabPanel();
    tabHistoryTokens = new ArrayList<String>();

    // header
    header.setAppName(AppConfig.getAppDisplayName());
    boolean useLogo = AppConfig.getAppDisplayName().equalsIgnoreCase("ohmage");
    header.useAppLogo(useLogo);
    header.setUserName(loginManager.getLoggedInUserName());

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
    exploreDataPresenter = new ExploreDataPresenter(userInfo, awDataService, eventBus, campaigns);
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

    // avoid unneccessary work by only instantiating admin classes if user is an admin
    if (userInfo.isAdmin()) {
      adminView = new AdminViewImpl();
      adminAuditLogView = new AdminAuditLogView();
      adminUserListView = new AdminUserListView();
      adminUserDetailView = new AdminUserDetailView();
      adminUserEditView = new AdminUserEditView();
      adminUserCreateView = new AdminUserCreateView();
      adminClassListView = new AdminClassListView();
      adminClassDetailView = new AdminClassDetailView();
      adminClassEditView = new AdminClassEditView();
      adminPresenter = new AdminPresenter(userInfo, awDataService, eventBus);
      adminAuditLogPresenter = new AdminAuditLogPresenter(userInfo, awDataService, eventBus);
      adminUserListPresenter = new AdminUserListPresenter(userInfo, awDataService, eventBus);
      adminUserDetailPresenter = new AdminUserDetailPresenter(userInfo, awDataService, eventBus);
      adminUserEditPresenter = new AdminUserEditPresenter(userInfo, awDataService, eventBus);
      adminUserCreatePresenter = new AdminUserCreatePresenter(userInfo, awDataService, eventBus);
      adminClassListPresenter = new AdminClassListPresenter(userInfo, awDataService, eventBus);
      adminClassDetailPresenter = new AdminClassDetailPresenter(userInfo, awDataService, eventBus);
      adminClassEditPresenter = new AdminClassEditPresenter(userInfo, awDataService, eventBus);
      adminPresenter.setView(adminView);
      adminAuditLogPresenter.setView(adminAuditLogView);
      adminUserListPresenter.setView(adminUserListView);
      adminUserDetailPresenter.setView(adminUserDetailView);
      adminUserEditPresenter.setView(adminUserEditView);
      adminUserCreatePresenter.setView(adminUserCreateView);
      adminClassListPresenter.setView(adminClassListView);
      adminClassDetailPresenter.setView(adminClassDetailView);
      adminClassEditPresenter.setView(adminClassEditView);
    }
  }

  // NOTE: userInfo is needed here because some users see tabs that others don't (e.g., Admin tab)
  private void initLayoutAndNavigation(UserInfo userInfo) {
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
    tabPanel.add(classView, "Classes");
    tabHistoryTokens.add("classes");
    TabIndex.CLASSES = index++;
    tabPanel.add(documentView, "Documents");
    tabHistoryTokens.add("documents");
    TabIndex.DOCUMENTS = index++;
    if (userInfo.isAdmin()) {
      tabPanel.add(adminView, "Admin");
      tabHistoryTokens.add("admin");
      TabIndex.ADMIN = index++;
    }

    // Clicking on a tab fires history token. tab will be selected when the 
    // history token is processed. 
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
    mainDockLayoutPanel.addNorth(header, 56);
    mainDockLayoutPanel.add(tabPanel);
    RootLayoutPanel.get().add(mainDockLayoutPanel);

    // NOTE: tabPanel, accountView, and helpView all live in the center
    // panel of the dock layout but are attached/removed so only one
    // is visible at a time
  }

  /**
   * ****** Methods to control visible widgets ***********
   */
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

  private void showAdmin() {
    setMainContentTabPanel();
    tabPanel.remove(TabIndex.ADMIN);
    tabPanel.insert(adminView, "Admin", TabIndex.ADMIN);
    // since it's a new tab, clickhandler must be re-added
    tabPanel.getTabBar().getTab(TabIndex.ADMIN).addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        History.newItem(HistoryTokens.admin());
      }
    });
    tabPanel.selectTab(TabIndex.ADMIN);
  }

  // The historyToken arg is the token that triggered this view, used in the
  // tab click handler to make the view sticky. (So search results are not
  // reset every time you click away from the page and click back.)
  private void showAdminAuditLog(final String historyToken) {
    setMainContentTabPanel();
    tabPanel.remove(TabIndex.ADMIN);
    tabPanel.insert(adminAuditLogView, "Admin", TabIndex.ADMIN);
    // since it's a new tab, clickhandler must be re-added
    tabPanel.getTabBar().getTab(TabIndex.ADMIN).addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        History.newItem(historyToken);
      }
    });
    tabPanel.selectTab(TabIndex.ADMIN);
  }

  // The historyToken arg is the token that triggered this view, used in the
  // tab click handler to make the view sticky. (So search results are not
  // reset every time you click away from the page and click back.)
  private void showAdminUserList(final String historyToken) {
    setMainContentTabPanel();
    tabPanel.remove(TabIndex.ADMIN);
    tabPanel.insert(adminUserListView, "Admin", TabIndex.ADMIN);
    // since it's a new tab, clickhandler must be re-added
    tabPanel.getTabBar().getTab(TabIndex.ADMIN).addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        History.newItem(historyToken);
      }
    });
    tabPanel.selectTab(TabIndex.ADMIN);
  }

  // The historyToken arg is the token that triggered this view, used in the
  // tab click handler to make the view sticky. 
  private void showAdminUserDetail(final String historyToken) {
    setMainContentTabPanel();
    tabPanel.remove(TabIndex.ADMIN);
    tabPanel.insert(adminUserDetailView, "Admin", TabIndex.ADMIN);
    // since it's a new tab, clickhandler must be re-added
    tabPanel.getTabBar().getTab(TabIndex.ADMIN).addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        History.newItem(historyToken);
      }
    });
    tabPanel.selectTab(TabIndex.ADMIN);
  }

  // The historyToken arg is the token that triggered this view, used in the
  // tab click handler to make the view sticky. 
  private void showAdminUserEdit(final String historyToken) {
    setMainContentTabPanel();
    tabPanel.remove(TabIndex.ADMIN);
    tabPanel.insert(adminUserEditView, "Admin", TabIndex.ADMIN);
    // since it's a new tab, clickhandler must be re-added
    tabPanel.getTabBar().getTab(TabIndex.ADMIN).addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        History.newItem(historyToken);
      }
    });
    tabPanel.selectTab(TabIndex.ADMIN);
  }

  private void showAdminUserCreate() {
    setMainContentTabPanel();
    tabPanel.remove(TabIndex.ADMIN);
    tabPanel.insert(adminUserCreateView, "Admin", TabIndex.ADMIN);
    // since it's a new tab, clickhandler must be re-added
    tabPanel.getTabBar().getTab(TabIndex.ADMIN).addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        History.newItem(HistoryTokens.adminUserCreate());
      }
    });
    tabPanel.selectTab(TabIndex.ADMIN);
  }

  private void showAdminClassList() {
    setMainContentTabPanel();
    tabPanel.remove(TabIndex.ADMIN);
    tabPanel.insert(adminClassListView, "Admin", TabIndex.ADMIN);
    // since it's a new tab, clickhandler must be re-added
    tabPanel.getTabBar().getTab(TabIndex.ADMIN).addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        History.newItem(HistoryTokens.adminClassList());
      }
    });
    tabPanel.selectTab(TabIndex.ADMIN);
  }

  private void showAdminClassDetail(final String historyToken) {
    setMainContentTabPanel();
    tabPanel.remove(TabIndex.ADMIN);
    tabPanel.insert(adminClassDetailView, "Admin", TabIndex.ADMIN);
    // since it's a new tab, clickhandler must be re-added
    tabPanel.getTabBar().getTab(TabIndex.ADMIN).addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        History.newItem(historyToken);
      }
    });
    tabPanel.selectTab(TabIndex.ADMIN);
  }

  private void showAdminClassEdit(final String historyToken) {
    setMainContentTabPanel();
    tabPanel.remove(TabIndex.ADMIN);
    tabPanel.insert(adminClassEditView, "Admin", TabIndex.ADMIN);
    // since it's a new tab, clickhandler must be re-added
    tabPanel.getTabBar().getTab(TabIndex.ADMIN).addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        History.newItem(historyToken);
      }
    });
    tabPanel.selectTab(TabIndex.ADMIN);
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
    if (accountView.isAttached()) {
      accountView.removeFromParent();
    }
    if (helpView.isAttached()) {
      helpView.removeFromParent();
    }
    if (!tabPanel.isAttached()) {
      mainDockLayoutPanel.add(tabPanel);
    }
  }

  private void setMainContentAccountView() {
    if (tabPanel.isAttached()) {
      tabPanel.removeFromParent();
    }
    if (helpView.isAttached()) {
      helpView.removeFromParent();
    }
    if (!accountView.isAttached()) {
      mainDockLayoutPanel.add(accountView);
    }
  }

  private void setMainContentHelpView() {
    if (tabPanel.isAttached()) {
      tabPanel.removeFromParent();
    }
    if (accountView.isAttached()) {
      accountView.removeFromParent();
    }
    if (!helpView.isAttached()) {
      mainDockLayoutPanel.add(helpView);
    }
  }

  private void showSelfRegistration() {
    // TODO: TURN THIS INTO A LIST?
    // Remove all other views first
    if (loginView != null) {
      loginView.asWidget().removeFromParent();
    }
    if (loginRecovery != null) {
      loginRecovery.removeFromParent();
    }

    if (loginSelfRegistrationView == null) {
      initLoginSelfRegistration();
    } else {
      loginSelfRegistrationView.resetAll();

      if (!loginSelfRegistrationView.isAttached()) {
        RootLayoutPanel.get().add(loginSelfRegistrationView);
      }
    }
  }

  private void showLoginRecovery() {
    // TODO: TURN THIS INTO A LIST?
    // Remove all other views first
    if (loginView != null) {
      loginView.asWidget().removeFromParent();
    }
    if (loginSelfRegistrationView != null) {
      loginSelfRegistrationView.removeFromParent();
    }

    if (loginRecovery == null) {
      initLoginRecovery();
    } else {
      loginRecovery.resetAll();

      if (!loginRecovery.isAttached()) {
        RootLayoutPanel.get().add(loginRecovery);
      }
    }
  }

  private void showLogin(final String message) {
    // TODO: TURN THIS INTO A LIST?
    // Remove all other views first
    if (loginSelfRegistrationView != null) {
      loginSelfRegistrationView.removeFromParent();
    }
    if (loginRecovery != null) {
      loginRecovery.removeFromParent();
    }

    if (loginView == null || loginPresenter == null) {
      initLogin(message);
    } else {
      loginView.setNotificationMessage(message);

      loginView.asWidget().removeFromParent();	// Remove any old instances
      RootLayoutPanel.get().add(loginView);
    }
  }

  private void activateUserAndShowLogin(String registration_id) {
    this.awDataService.activateUser(registration_id, new AsyncCallback<String>() {

      @Override
      public void onFailure(Throwable caught) {
        String message = "Sorry, your activation link has expired (" + caught.getMessage() + "). Please register again to create an account.";
        showLogin(message);
      }

      @Override
      public void onSuccess(String result) {
        String message = "Your account has been successfully activated. You may now sign into ohmage. Welcome!";
        showLogin(message);
      }
    });
  }

  /**
   * ****** End methods to control visible widgets *******
   */
  /**
   * ******* History Management **********
   */
  @Override
  public void onHistoryChanged(String historyToken) {
    String view = extractView(historyToken);
    final Map<String, String> params = extractParams(historyToken);

    _logger.fine("History Token Fired = \'" + view + "\'");

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
    } else if (view.equals("admin")) {
      adminPresenter.go(params);
      showAdmin();
    } else if (view.equals("admin_audit")) {
      adminAuditLogPresenter.go(params);
      showAdminAuditLog(History.getToken());
    } else if (view.equals("admin_user_list")) {
      adminUserListPresenter.go(params);
      showAdminUserList(History.getToken());
    } else if (view.equals("admin_user_edit")) {
      adminUserEditPresenter.go(params);
      showAdminUserEdit(History.getToken());
    } else if (view.equals("admin_user_create")) {
      adminUserCreatePresenter.go(params);
      showAdminUserCreate();
    } else if (view.equals("admin_user_detail")) {
      showAdminUserDetail(History.getToken());
      adminUserDetailPresenter.go(params);
    } else if (view.equals("admin_class_list")) {
      adminClassListPresenter.go(params);
      showAdminClassList();
    } else if (view.equals("admin_class_edit") || view.equals("admin_class_create")) {
      adminClassEditPresenter.go(params);
      showAdminClassEdit(History.getToken());
    } else if (view.equals("admin_class_detail")) {
      showAdminClassDetail(History.getToken());
      adminClassDetailPresenter.go(params);
    } else if (view.equals("account")) {
      accountPresenter.go(params);
      showAccount();
    } else if (view.equals("help")) {
      showHelp();
    } else if (view.equals("logout")) {
      logout();
    } else if (view.equals("register")) {
      showSelfRegistration();
    } else if (view.equals("reset_password")) {
      showLoginRecovery();
    } else if (view.equals("login")) {
      showLogin(null);
    } else if (view.equals("activate")) {
      if (params.containsKey("registration_id")) {
        activateUserAndShowLogin(params.get("registration_id"));
      } else {
        showLogin(null);
      }
    }
  }

  private void logout() {
    loginManager.logOut();
    History.newItem("login", false); // logging in again will show dashboard
    Window.Location.reload(); // reload to clear state
  }

  private String extractView(String historyToken) {
    return historyToken.split("\\?")[0]; // everything before the ?
  }
  private static RegExp urlWithQueryParams = RegExp.compile(".+\\?.+");

  private Map<String, String> extractParams(String historyToken) {
    Map<String, String> params = new HashMap<String, String>();
    //if (historyToken.contains("?")) {
    if (urlWithQueryParams.test(historyToken)) {
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
  /**
   * ******* End of History Management **********
   */
}
