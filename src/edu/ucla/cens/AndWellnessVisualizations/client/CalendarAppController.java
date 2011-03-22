package edu.ucla.cens.AndWellnessVisualizations.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.code.p.gwtchismes.client.GWTCSimpleDatePicker;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import edu.ucla.cens.AndWellnessVisualizations.client.common.DataPointBrowserViewDefinitions;
import edu.ucla.cens.AndWellnessVisualizations.client.common.TokenLoginManager;
import edu.ucla.cens.AndWellnessVisualizations.client.event.CampaignConfigurationEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.DateSelectionEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.DateSelectionEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.event.NewDataPointAwDataEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.DataBrowserSelectionEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.DataBrowserSelectionEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.event.RequestLogoutEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.VisualizationSelectionEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.VisualizationSelectionEventHandler;
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
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.VisualizationSelectionPresenter;
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
import edu.ucla.cens.AndWellnessVisualizations.client.view.VisualizationSelectionView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.VisualizationSelectionViewImpl;

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
    private VisualizationSelectionView vizSelView = null;
    private DataPointBrowserView<CampaignInfo,ConfigurationInfo,SurveyInfo,PromptInfo> dataPointBrowserView = null;
    
    // Definitions needed for the views to render
    private DataPointBrowserViewDefinitions dataPointBrowserViewDefinitions = null;
    
    // Data necessary to fetch data from the server
    private Date currentMonth = new Date();
    private List<String> currentDataPointList = new ArrayList<String>();
    private String currentUserName = null;
    private String currentCampaignName = null;
    private String currentCampaignVersion = null;
    
    // Data about the logged in user
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
        eventBus.addHandler(DateSelectionEvent.TYPE, new DateSelectionEventHandler() {
            public void onSelection(DateSelectionEvent event) {
            	switch (event.getType()) {
            	case Month:
	                currentMonth = event.getSelection();
	                
	                fetchDataPoints();
	                
	                break;
            	}
            }   
        });
        
        // Listen for a new data point label selection, call for new data
        eventBus.addHandler(DataBrowserSelectionEvent.TYPE, new DataBrowserSelectionEventHandler() {
            public void onSelect(DataBrowserSelectionEvent event) {
                switch(event.getType()) {
                case campaignName:
                	currentCampaignName = event.getData().get(0);
                	break;
                case campaignVersion:
                	currentCampaignVersion = event.getData().get(0);
                	break;
                case userName:
                	currentUserName = event.getData().get(0);
                	// If a new user is selected, update our data
                	fetchDataPoints();
                	break;
                case promptIdList:
                	currentDataPointList = event.getData();
                	// If a new prompt id is selected, update our data
                	fetchDataPoints();
                	break;
                }
            }
        });
        
        // Listen for a visualization selection, redirect to the new page
        eventBus.addHandler(VisualizationSelectionEvent.TYPE, new VisualizationSelectionEventHandler() {
 			public void onSelect(VisualizationSelectionEvent event) {
 				_logger.fine("Handling viz selection event of type " + event.getSelection().toString());
 				
 				switch (event.getSelection()) {
 				case CALENDAR:
 					// Do nothing
 					break;
 				case MAP:
 					// Redirect to the map
 					Window.Location.assign("../" + AndWellnessConstants.getMapUrl());
 					break;
 				case CHART:
 					// Redirect to the chart
 					Window.Location.assign("../" + AndWellnessConstants.getChartUrl());
 					break;
 				}
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
        
        // Initialize and run the viz selection view
        if (vizSelView == null) {
        	vizSelView = new VisualizationSelectionViewImpl();
        }
        VisualizationSelectionPresenter vizSelPres = new VisualizationSelectionPresenter(rpcService, eventBus, vizSelView); 
        vizSelPres.go(RootPanel.get("visualizationSelectionView"));
        
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
      
        // Check to be sure the user information is set
        if (userInfo == null) {
            _logger.warning("No user info has been set, cannot fetch data points.");
            return;
        }
        
        // Make sure all necessary selection information is net
        if (currentUserName == null || currentCampaignName == null || currentCampaignVersion == null ||
                currentDataPointList.size() == 0) {
            _logger.warning("Not all necessary information has been set to fetch data.");
            return;
        }
        
        // Find the first and last day of the requested month
        startDate = GWTCSimpleDatePicker.getFirstDayOfMonth(currentMonth);
        endDate = GWTCSimpleDatePicker.getLastDayOfMonth(currentMonth);
          
        // Send our request to the rpcService and handle the result
        rpcService.fetchDataPoints(startDate, endDate, currentUserName, currentDataPointList, currentCampaignName, currentCampaignVersion, loginManager.getAuthorizationToken(), 
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
