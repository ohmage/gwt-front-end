package edu.ucla.cens.AndWellnessVisualizations.client.testing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.code.p.gwtchismes.client.GWTCSimpleDatePicker;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import edu.ucla.cens.AndWellnessVisualizations.client.ClientInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.event.DataPointLabelSelectionEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.NewDataPointAwDataEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.model.AuthorizationTokenQueryAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.DataPointAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.DataPointQueryAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.CalendarVisualizationPresenter;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.LocalAndWellnessRpcService;
import edu.ucla.cens.AndWellnessVisualizations.client.view.CalendarVisualizationView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.CalendarVisualizationViewImpl;

/**
 * Test of the CalendarVisualization.  For now only initializes the View and attaches
 * it to the test_calendar_visualization div.
 * 
 * @author jhicks
 *
 */
public class CalendarVisualizationTest implements EntryPoint {
    private Date currentMonth = new Date();
    private String currentDataLabel = "alcoholNumberOfDrinks";
    
    public LocalAndWellnessRpcService rpcService;
    public HandlerManager eventBus;
    
    private static Logger _logger = Logger.getLogger(CalendarVisualizationTest.class.getName());
    
    /**
     * Creates the ValendarVisualizationView on the RootPanel
     */
    public void onModuleLoad() {
        // Initialize the rpc service and event bus for the app
        rpcService = new LocalAndWellnessRpcService();
        eventBus = new HandlerManager(null);
        
        // Create a new view and presenter
        CalendarVisualizationView calViz = new CalendarVisualizationViewImpl();
        CalendarVisualizationPresenter calPres = new CalendarVisualizationPresenter(rpcService, eventBus, calViz);
        
        // Tell the presenter to GO
        calPres.go(RootPanel.get("calendarVisualizationView"));
        
        // Now call some testing functions to see if everything works
        //testMonthSwitch();
        
        setDataPointLabel();
        
        testDataFetch();
    }
    
    private void testMonthSwitch() {
        // Send out a monthselection event
        
    }
    
    private void setDataPointLabel() {
        eventBus.fireEvent(new DataPointLabelSelectionEvent(currentDataLabel));
    }
    
    private void testDataFetch() {
        _logger.info("Attempting to login...");
        
        // First login
        rpcService.fetchAuthorizationToken("abc", "123", new AsyncCallback<AuthorizationTokenQueryAwData>() {

            @Override
            public void onFailure(Throwable caught) {
                _logger.warning("Authorization failed with reason " + caught.getMessage());
            }

            @Override
            public void onSuccess(AuthorizationTokenQueryAwData result) {
                _logger.info("Successfully logged in");
                
                fetchData();
            }
            
        });
        

    }
    
    private void fetchData() {
        // Data for the rpc request
        Date startDate, endDate;
        String userName, campaignId, clientName;
        List<String> dataPointLabels = new ArrayList<String>();
        
        // Find the first and last day of the requested month
        startDate = GWTCSimpleDatePicker.getFirstDayOfMonth(currentMonth);
        endDate = GWTCSimpleDatePicker.getLastDayOfMonth(currentMonth);
        userName = "testUser";
        campaignId = "testCampaign";
        clientName = "testGWTClient";
        dataPointLabels.add(currentDataLabel);
        
        _logger.info("Sending out a request for data label " + currentDataLabel);
        
        // Send our request to the rpcService and handle the result
        rpcService.fetchDataPoints(startDate, endDate, userName, dataPointLabels, campaignId, clientName, new AsyncCallback<List<DataPointAwData>>() {

            public void onSuccess(List<DataPointAwData> data) {
                _logger.fine("Received " + data.size() + " data points, sending to event bus.");
                
                eventBus.fireEvent(new NewDataPointAwDataEvent(data));
            }
            
            public void onFailure(Throwable error) {
                _logger.warning("fetchDataPoints called onFailure with reason " + error.getMessage());
            }
        });
    }
}
