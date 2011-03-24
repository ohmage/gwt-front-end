package edu.ucla.cens.AndWellnessVisualizations.client;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;


import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import edu.ucla.cens.AndWellnessVisualizations.client.common.DataPointBrowserViewDefinitions;
import edu.ucla.cens.AndWellnessVisualizations.client.common.TokenLoginManager;
import edu.ucla.cens.AndWellnessVisualizations.client.event.CampaignConfigurationEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.DataBrowserSelectionEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.DataBrowserSelectionEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.event.DateSelectionEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.DateSelectionEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.event.NewChunkedMobilityAwDataEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.NewMobilityDataPointAwDataEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.RequestLogoutEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.VisualizationSelectionEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.VisualizationSelectionEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.model.CampaignInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.model.ChunkedMobilityAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.ConfigQueryAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.ConfigurationInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.model.MobilityDataPointAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.PromptInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.model.SurveyInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.model.UserInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.DataPointBrowserPresenter;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.MobilityChartPresenter;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.NavigationBarPresenter;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.VisualizationSelectionPresenter;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.WeekSelectionPresenter;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.AndWellnessRpcService;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.NotLoggedInException;
import edu.ucla.cens.AndWellnessVisualizations.client.utils.AwDataTranslators;
import edu.ucla.cens.AndWellnessVisualizations.client.utils.DateUtils;
import edu.ucla.cens.AndWellnessVisualizations.client.view.DataPointBrowserView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.DataPointBrowserViewImpl;
import edu.ucla.cens.AndWellnessVisualizations.client.view.MobilityChartVisualizationView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.MobilityChartVisualizationViewImpl;
import edu.ucla.cens.AndWellnessVisualizations.client.view.NavigationBarView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.NavigationBarViewImpl;
import edu.ucla.cens.AndWellnessVisualizations.client.view.VisualizationSelectionView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.VisualizationSelectionViewImpl;
import edu.ucla.cens.AndWellnessVisualizations.client.view.WeekSelectionView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.WeekSelectionViewImpl;
import edu.ucla.cens.AndWellnessVisualizations.client.widget.IFrameForm;

/**
 * The main controller for the Calendar visualization.  Its job is two fold.
 * First, we setup and start the presenters and views necessary for the calendar
 * visualization.  Second, we listen for various events and take action.  All
 * rpc service calls are made here.
 * 
 * @author jhicks
 *
 */
public class MobilityChartAppController {
    private final EventBus eventBus;
    private final AndWellnessRpcService rpcService; 
    private final TokenLoginManager loginManager;
    
    // Various views in this controller
    private NavigationBarView navBarView = null;
    private VisualizationSelectionView vizSelView = null;
    private WeekSelectionView weekSelectionView = null;
    private DataPointBrowserView<CampaignInfo,ConfigurationInfo,SurveyInfo,PromptInfo> dataPointBrowserView = null;
    private MobilityChartVisualizationView mobChartView = null;
    
    // Definitions needed for the views to render
    private DataPointBrowserViewDefinitions dataPointBrowserViewDefinitions = null;
    
    // Data necessary to fetch data from the server
    private Date currentDay = new Date();
    private String currentUserName = null;
    
    // Data about the logged in user
    private UserInfo userInfo = null;
    
    // Logging utility
    private static Logger _logger = Logger.getLogger(MobilityChartAppController.class.getName());
    
    public MobilityChartAppController(AndWellnessRpcService rpcService, EventBus eventBus, TokenLoginManager loginManager) {
        this.eventBus = eventBus;
        this.rpcService = rpcService;
        this.loginManager = loginManager;
        
        bind();
    }
    
    // Listen for events, take action
    private void bind() {
        // Listen for a new data point label selection, call for new data
        eventBus.addHandler(DataBrowserSelectionEvent.TYPE, new DataBrowserSelectionEventHandler() {
            public void onSelect(DataBrowserSelectionEvent event) {
            	switch(event.getType()) {
            	// Only worry about user names
            	case userName:
            		currentUserName = event.getData().get(0);
            		fetchDataPoints();
            		break;
            	}
            }
        });
        
        // Listen for a new week selection, call for new data
        eventBus.addHandler(DateSelectionEvent.TYPE, new DateSelectionEventHandler() {
			public void onSelection(DateSelectionEvent event) {
				switch(event.getType()) {
				case Week:
					// New week, select new data
					currentDay = event.getSelection();
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
					// Redirect to the calendar
					Window.Location.assign("../" + AndWellnessConstants.getCalendarUrl());
					break;
				case MAP:
					// Redirect to the map
					Window.Location.assign("../" + AndWellnessConstants.getMapUrl());
					break;
				case CHART:
					// We are on the chart already, do nothing
					break;
				}
			}
        });
    }
    
    /**
     * Initializes the various presenters and views that this controls
     */
    @SuppressWarnings("deprecation")
	public void go() {        
        // Initialize and run the navigation bar view
        if (navBarView == null) {
            navBarView = new NavigationBarViewImpl();
        }
        NavigationBarPresenter navBarPres= new NavigationBarPresenter(eventBus, navBarView, loginManager);
        navBarPres.go(RootPanel.get("navigationBarView"));
        
        // Initialize and run the viz selection view
        if (vizSelView == null) {
        	vizSelView = new VisualizationSelectionViewImpl();
        }
        VisualizationSelectionPresenter vizSelPres = new VisualizationSelectionPresenter(rpcService, eventBus, vizSelView); 
        vizSelPres.go(RootPanel.get("visualizationSelectionView"));
        
        // Initialize and run the week selection view
        if (weekSelectionView == null) {
        	weekSelectionView = new WeekSelectionViewImpl();
        }
        WeekSelectionPresenter weekSelPres = new WeekSelectionPresenter(rpcService, eventBus, weekSelectionView);
        weekSelPres.go(RootPanel.get("weekSelectionView"));
        // Set to always end on Sundays
        Date date = new Date();
        // Deprecated, but Calendar is not included in the GWT libraries
        if (date.getDay() != 0) {
        	// Shift to Sunday
        	date = DateUtils.addDays(date, 7 - date.getDay());
        }
        weekSelPres.setCurrentWeek(date);
        currentDay = date;
        
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
        // We don't want to show most of the browser panels
        dpbPres.setCampaignVisibility(false);
        dpbPres.setDPVisibility(false);
        dpbPres.setSurveyVisibility(false);
        
        if (mobChartView == null) {
        	mobChartView = new MobilityChartVisualizationViewImpl();
        }
        MobilityChartPresenter mobChartPres = new MobilityChartPresenter(rpcService, eventBus, mobChartView);
        mobChartPres.go(RootPanel.get("mobilityChartVisualizationView"));
        
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
        // Check to be sure the user information is set
        if (userInfo == null) {
            _logger.warning("No user info has been set, cannot fetch data points.");
            return;
        }
        
        // Make sure all necessary selection information is net
        if (currentUserName == null || currentDay == null) {
            _logger.warning("Not all necessary information has been set to fetch data.");
            return;
        }
          
        // Send our request to the rpcService and handle the result
        // Need to add oen to curentDay to received the correct amount of data
        final Date firstDay = DateUtils.addDays(currentDay, -6);
        final Date lastDay = DateUtils.addDays(currentDay, 1);
        rpcService.fetchChunkedMobility(firstDay, lastDay, currentUserName, "2", loginManager.getAuthorizationToken(), 
                new AsyncCallback<List<ChunkedMobilityAwData>>() {
            
            public void onSuccess(List<ChunkedMobilityAwData> awData) {
                _logger.info("Received " + awData.size() + " data points from the server.");
                
                // Filter the list for out of bounds data
                Iterator<ChunkedMobilityAwData> dataIter = awData.iterator();
                while (dataIter.hasNext()) {
                	ChunkedMobilityAwData dataPoint = dataIter.next();
                	Date day = DateUtils.translateFromServerFormat(dataPoint.getTimeStamp());
                	
                	if (day.compareTo(firstDay) < 0|| day.compareTo(lastDay) > 0)
                		dataIter.remove();
                }
                
                // If we get data back, send it out in an event
                eventBus.fireEvent(new NewChunkedMobilityAwDataEvent(awData));
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
