package edu.ucla.cens.AndWellnessVisualizations.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.code.p.gwtchismes.client.GWTCSimpleDatePicker;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import edu.ucla.cens.AndWellnessVisualizations.client.common.DataPointBrowserViewDefinitions;
import edu.ucla.cens.AndWellnessVisualizations.client.common.TokenLoginManager;
import edu.ucla.cens.AndWellnessVisualizations.client.event.CampaignConfigurationEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.DataPointLabelSelectionEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.DataPointLabelSelectionEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.event.MonthSelectionEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.MonthSelectionEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.event.NewDataPointAwDataEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.RequestLogoutEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.model.CampaignInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.model.ConfigQueryAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.ConfigurationInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.model.DataPointAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.PromptInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.model.SurveyInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.model.UserInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.CalendarVisualizationPresenter;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.DataPointBrowserPresenter;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.MonthSelectionPresenter;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.NavigationBarPresenter;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.AndWellnessRpcService;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.NotLoggedInException;
import edu.ucla.cens.AndWellnessVisualizations.client.utils.AwDataTranslators;
import edu.ucla.cens.AndWellnessVisualizations.client.view.CalendarVisualizationView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.CalendarVisualizationViewImpl;
import edu.ucla.cens.AndWellnessVisualizations.client.view.DataPointBrowserView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.DataPointBrowserViewImpl;
import edu.ucla.cens.AndWellnessVisualizations.client.view.MonthSelectionView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.MonthSelectionViewImpl;
import edu.ucla.cens.AndWellnessVisualizations.client.view.NavigationBarView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.NavigationBarViewImpl;

/**
 * The main controller for the Calendar visualization.  Its job is two fold.
 * First, we setup and start the presenters and views necessary for the calendar
 * visualization.  Second, we listen for various events and take action.  All
 * rpc service calls are made here.
 * 
 * @author jhicks
 *
 */
public class CalendarAppController {
    private final EventBus eventBus;
    private final AndWellnessRpcService rpcService; 
    private final TokenLoginManager loginManager;
    
    // Various views in this controller
    private CalendarVisualizationView calVizView = null;
    private MonthSelectionView monthView = null;
    private NavigationBarView navBarView = null;
    private DataPointBrowserView<CampaignInfo,ConfigurationInfo,SurveyInfo,PromptInfo> dataPointBrowserView = null;
    
    // Definitions needed for the views to render
    private DataPointBrowserViewDefinitions dataPointBrowserViewDefinitions = null;
    
    // Various data we need to maintain
    private Date currentMonth = new Date();
    private String currentDataPoint = null;
    private UserInfo userInfo = null;
    
    // Logging utility
    private static Logger _logger = Logger.getLogger(CalendarAppController.class.getName());
    
    public CalendarAppController(AndWellnessRpcService rpcService, EventBus eventBus, TokenLoginManager loginManager) {
        this.eventBus = eventBus;
        this.rpcService = rpcService;
        this.loginManager = loginManager;
        
        bind();
    }
    
    // Listen for events, take action
    private void bind() {
        // Listen for a month selection event, if we have a data point selected, call for new data
        // for the new month
        eventBus.addHandler(MonthSelectionEvent.TYPE, new MonthSelectionEventHandler() {
            public void onSelection(MonthSelectionEvent event) {
                currentMonth = event.getMonthSelection();
                
                // Call for new data
                if (currentDataPoint != null) {
                    fetchDataPoints();
                }
                else {
                    _logger.fine("MonthSelectionEvent detected but currentDataPoint is null.");
                }
            }   
        });
        
        // Listen for a new data point label selection, call for new data
        eventBus.addHandler(DataPointLabelSelectionEvent.TYPE, new DataPointLabelSelectionEventHandler() {
            public void onSelection(DataPointLabelSelectionEvent event) {
                currentDataPoint = event.getDataPointLabelSelection();
                
                fetchDataPoints();
            }
        });
    }
    
    /**
     * Initializes the various presenters and views that this controls
     */
    public void go() {        
        // Initialize and run the navigation bar view
        if (navBarView == null) {
            navBarView = new NavigationBarViewImpl();
        }
        NavigationBarPresenter navBarPres= new NavigationBarPresenter(eventBus, navBarView, loginManager);
        navBarPres.go(RootPanel.get("navigationBarView"));
        
        // Initialize and run the month selection
        if (monthView == null) {
            monthView = new MonthSelectionViewImpl();
        }
        MonthSelectionPresenter monthPres = new MonthSelectionPresenter(rpcService, eventBus, monthView);
        monthPres.go(RootPanel.get("monthSelectionView"));
        
        // Initialize and run the data browser view
        if (dataPointBrowserView == null) {
            dataPointBrowserView = new DataPointBrowserViewImpl<CampaignInfo,ConfigurationInfo,SurveyInfo,PromptInfo>();
            // Initialize the render definitions
            if (dataPointBrowserViewDefinitions == null) {
                dataPointBrowserViewDefinitions = new DataPointBrowserViewDefinitions();
            }
            dataPointBrowserView.setDefinitions(
                    dataPointBrowserViewDefinitions.getCampaignInfoDefinition(), 
                    dataPointBrowserViewDefinitions.getConfigurationInfoDefinition(), 
                    dataPointBrowserViewDefinitions.getSurveyInfoDefinition(), 
                    dataPointBrowserViewDefinitions.getDataPointDefinition());
        }
        DataPointBrowserPresenter dpbPres = new DataPointBrowserPresenter(eventBus, dataPointBrowserView);
        dpbPres.go(RootPanel.get("dataPointBrowserView"));
        
        
        // Initialize and run the calendar visualization
        if (calVizView == null) {
            calVizView = new CalendarVisualizationViewImpl();
        }
        CalendarVisualizationPresenter calVizPresenter = new CalendarVisualizationPresenter(rpcService, eventBus, calVizView);
        calVizPresenter.go(RootPanel.get("calendarVisualizationView"));
        
        // Fetch the configuration information, needed for the presenter/views
        fetchConfigData();
    }

    private void fetchConfigData() {   
        // Make sure we are logged in
        if (!loginManager.isCurrentlyLoggedIn()) {
            _logger.warning("Cannot fetch config info if not logged in.");
            return;
        }
        
        rpcService.fetchConfigData(loginManager.getAuthorizationToken(), new AsyncCallback<ConfigQueryAwData>() {
            public void onFailure(Throwable error) {
                _logger.warning("Problem getting configuration information from server: " + error.getMessage());
                
                try {
                    throw error;
                }
                catch (NotLoggedInException e) {
                    _logger.warning("Authorization problem, log us out");
                    
                    eventBus.fireEvent(new RequestLogoutEvent());
                }
                catch (Throwable e) {
                    _logger.severe(e.getMessage());
                }
            }

            public void onSuccess(ConfigQueryAwData result) {
                _logger.fine("Received config query from server, parsing into a userInfo");
                
                userInfo = AwDataTranslators.translateConfigQueryAwDataToUserInfo(loginManager.getLoggedInUserName(), result);
                
                // Now that we have new campaign configuration, send out an event to notify anyone listening
                eventBus.fireEvent(new CampaignConfigurationEvent(userInfo));
            }
        });
    }
    
    /**
     * Fetches new data points based on the locally stored fields.  Checks to be sure
     * we have all the necessary data before sending the request.
     */
    private void fetchDataPoints() {
        // Data for the rpc request
        Date startDate, endDate;
        String userName, campaignId, clientName;
        List<String> dataPointLabels = new ArrayList<String>();
      
        // Check to be sure the user information is set
        if (userInfo == null) {
            _logger.warning("No user info has been set, cannot fetch data points.");
            return;
        }
        
        // Find the first and last day of the requested month
        startDate = GWTCSimpleDatePicker.getFirstDayOfMonth(currentMonth);
        endDate = GWTCSimpleDatePicker.getLastDayOfMonth(currentMonth);
        userName = userInfo.getUserName();
        campaignId = userInfo.getSelectedCampaignId();
        
        clientName = ClientInfo.getClientName();
        
        // The list of data point labels to ask for
        dataPointLabels.add(currentDataPoint);
        
        _logger.info("Asking server for data about label " + currentDataPoint);
        
        // Send our request to the rpcService and handle the result
        rpcService.fetchDataPoints(startDate, endDate, userName, dataPointLabels, campaignId, clientName, loginManager.getAuthorizationToken(), 
                new AsyncCallback<List<DataPointAwData>>() {
            
            public void onSuccess(List<DataPointAwData> awData) {
                _logger.info("Received " + awData.size() + " data points from the server.");
                // If we get data back, send it out in an event
                eventBus.fireEvent(new NewDataPointAwDataEvent(awData));
            }
            
            public void onFailure(Throwable error) {
                // Not good, can't contact server or maybe we are not logged in
                try {
                    throw error;
                }
                // If we have an authorization error, redirect back to the login screen
                catch (NotLoggedInException e) {
                    _logger.warning("Authorization problem, log us out");
                    
                    eventBus.fireEvent(new RequestLogoutEvent());
                } 
                // Don't know what to do here, uh oh
                catch (Throwable e) {
                    _logger.severe(e.getMessage());
                }
            }
            
        });
    }
}
