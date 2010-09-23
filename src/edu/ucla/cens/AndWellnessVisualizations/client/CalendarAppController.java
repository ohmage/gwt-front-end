package edu.ucla.cens.AndWellnessVisualizations.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.code.p.gwtchismes.client.GWTCSimpleDatePicker;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import edu.ucla.cens.AndWellnessVisualizations.client.event.DataPointLabelSelectionEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.DataPointLabelSelectionEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.event.MonthSelectionEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.MonthSelectionEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.event.NewDataPointAwDataEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.model.CampaignInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.model.DataPointAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.UserInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.CalendarVisualizationPresenter;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.MonthSelectionPresenter;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.AndWellnessRpcService;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.AuthorizationRpcServiceException;
import edu.ucla.cens.AndWellnessVisualizations.client.view.CalendarVisualizationView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.CalendarVisualizationViewImpl;
import edu.ucla.cens.AndWellnessVisualizations.client.view.MonthSelectionView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.MonthSelectionViewImpl;

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
    private final HandlerManager eventBus;
    private final AndWellnessRpcService rpcService; 
    
    // Various views in this controller
    private CalendarVisualizationView calVizView = null;
    private MonthSelectionView monthView = null;
    
    // Various data we need to maintain
    private Date currentMonth = new Date();
    private String currentDataPoint = null;
    private UserInfo userInfo;
    private CampaignInfo campaignInfo = CampaignInfo.getInstance();
    
    // Logging utility
    private static Logger _logger = Logger.getLogger(CalendarAppController.class.getName());
    
    
    public CalendarAppController(AndWellnessRpcService rpcService, HandlerManager eventBus) {
        this.eventBus = eventBus;
        this.rpcService = rpcService;
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
        // Initialize and run the month selection
        if (monthView == null) {
            monthView = new MonthSelectionViewImpl();
        }
        MonthSelectionPresenter monthPres = new MonthSelectionPresenter(rpcService, eventBus, monthView);
        monthPres.go(RootPanel.get("monthSelectionView"));
        
        // Initialize and run the calendar visualization
        if (calVizView == null) {
            calVizView = new CalendarVisualizationViewImpl();
        }
        CalendarVisualizationPresenter calVizPresenter = new CalendarVisualizationPresenter(rpcService, eventBus, calVizView);
        calVizPresenter.go(RootPanel.get("calendarVisualizationView"));
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
      
        // Find the first and last day of the requested month
        startDate = GWTCSimpleDatePicker.getFirstDayOfMonth(currentMonth);
        endDate = GWTCSimpleDatePicker.getLastDayOfMonth(currentMonth);
        /*
        userName = userInfo.getUserName();
        campaignId = campaignInfo.getCampaignId();
        */
        
        userName = "testUser";
        campaignId = "testCampaign";
        clientName = ClientInfo.getClientName();
        
        dataPointLabels.add(currentDataPoint);
        
        _logger.info("Asking server for data about label " + currentDataPoint);
        
        // Send our request to the rpcService and handle the result
        rpcService.fetchDataPoints(startDate, endDate, userName, dataPointLabels, campaignId, clientName, new AsyncCallback<List<DataPointAwData>>() {
            
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
                catch (AuthorizationRpcServiceException e) {
                    _logger.warning("Authorization problem, send the user back to the login screen");
                } 
                // Don't know what to do here, uh oh
                catch (Throwable e) {
                    _logger.warning(e.getMessage());
                }
            }
            
        });
    }
}
