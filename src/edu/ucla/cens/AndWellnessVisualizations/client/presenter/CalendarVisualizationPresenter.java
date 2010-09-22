package edu.ucla.cens.AndWellnessVisualizations.client.presenter;

import com.google.code.p.gwtchismes.client.GWTCSimpleDatePicker;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.ucla.cens.AndWellnessVisualizations.client.ClientInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.event.DataPointLabelSelectionEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.DataPointLabelSelectionEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.event.MonthSelectionEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.MonthSelectionEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.event.NewDataPointAwDataEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.NewDataPointAwDataEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.model.CampaignInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.model.DataPointAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.DataPointQueryAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.UserInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.AndWellnessRpcService;
import edu.ucla.cens.AndWellnessVisualizations.client.utils.CollectionUtils;
import edu.ucla.cens.AndWellnessVisualizations.client.utils.Predicate;
import edu.ucla.cens.AndWellnessVisualizations.client.view.CalendarVisualizationView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The CalendarVisualizationPresenter handles the the CalendarVisualizationView which is
 * the display of a months worth of data on a calendar.  Each day of the calendar is either grey
 * for no data, or a shade of blue whose opacity is determined by the relative amount of data
 * for that day.  
 * 
 * The Presenter is part of a larger calendar data display presenter which handles the entire
 * calendar display, including this visualization presenter, and various other data input presenters
 * to take in feedback from the user.
 * 
 * This presenter listens for two events: a month selection event and an incoming data event.  The month
 * selection event will trigger the presenter to change the displayed month, and an incoming data event
 * will trigger the presenter to update the view with the new data.
 * 
 * The presenter sends one event: a day selection event.  The event is not handled here.
 * 
 * @author jhicks
 *
 */
public class CalendarVisualizationPresenter implements Presenter,
        CalendarVisualizationView.Presenter {
   
    private UserInfo currentUserInfo;
    private CampaignInfo currentCampaignInfo;
    private Date currentMonth;
    private Map<Date, Double> currentDayData;
    private String currentDataPointLabel;  // Assume we only use one label for now
    private final AndWellnessRpcService rpcService;   // Used to make calls to the server for data
    private final HandlerManager eventBus;  
    private final CalendarVisualizationView view;
    
    public CalendarVisualizationPresenter(AndWellnessRpcService rpcService, 
            HandlerManager eventBus, CalendarVisualizationView view) {
        this.rpcService = rpcService;
        this.eventBus = eventBus;
        this.view = view;
        this.view.setPresenter(this);
        
        // Setup some default data
        currentMonth = new Date();
        currentDayData = new HashMap<Date, Double>();
        
        // Now initialize the default view
        view.updateMonth(currentMonth);
    }    
    
    /**
     * Binds this presenter to listen for various events on the event bus.
     */
    public void bind() {
        // Listen for a new data point label selection
        eventBus.addHandler(DataPointLabelSelectionEvent.TYPE,
            new DataPointLabelSelectionEventHandler() {
                public void onSelection(DataPointLabelSelectionEvent event) {
                    currentDataPointLabel = event.getDataPointLabelSelection();
                }            
        });
        
        // Listen for a new month selection.  Change to the new month and fetch new data from
        // the server.
        eventBus.addHandler(MonthSelectionEvent.TYPE,
            new MonthSelectionEventHandler() {
                public void onSelection(MonthSelectionEvent event) {
                    currentMonth = event.getMonthSelection();
                    
                    if (view != null) {
                        view.updateMonth(currentMonth);
                    }
                }
        });
        
        // Listen for any incoming data, process and pass to the view
        eventBus.addHandler(NewDataPointAwDataEvent.TYPE,
            new NewDataPointAwDataEventHandler() {
                public void onNewData(NewDataPointAwDataEvent event) {
                    // reset the current data
                    currentDayData.clear();
                    currentDayData = processNewDataPointAwData(event.getData());
                    
                    // Make sure the view exists
                    if (view != null) {
                        view.updateDayData(currentDayData);
                    }
                }
        });
    }
    
    public void go(HasWidgets container) {
        bind();
        container.clear();
        container.add(view.asWidget());
    }

    /**
     * Processes incoming server data into a model the view understands.
     * 
     * @param data The data to process.
     * @return The processed data.
     */
    private Map<Date, Double> processNewDataPointAwData(List<DataPointAwData> data) {
        Map<Date,Double> processedData;
        String displayType = "";
        Collection<DataPointAwData> filteredData;
        
        // Filter out data points that are not the current label
        filteredData = CollectionUtils.filter(data, new Predicate<DataPointAwData>() {
            public boolean apply(DataPointAwData type) {
                return type.getLabel().equals(currentDataPointLabel);
            } 
        });
        
        // Filter out data points that are not in the current month
        final Date startDate = GWTCSimpleDatePicker.getFirstDayOfMonth(currentMonth);
        final Date endDate = GWTCSimpleDatePicker.getLastDayOfMonth(currentMonth);
        final DateTimeFormat dateFormat = DateTimeFormat.getFormat(ClientInfo.timeStampFormat);
        filteredData = CollectionUtils.filter(filteredData, new Predicate<DataPointAwData>() {
            public boolean apply(DataPointAwData type) {
                Date dataPointDate = dateFormat.parse(type.getTimeStamp());
                
                // Check to make sure this is within the correct date range
                return (dataPointDate.after(startDate) && dataPointDate.before(endDate));
            } 
        });
        
        // Check to see if we found any data
        if (filteredData.size() == 0) {
            // If no data, return an empty map
            return new HashMap<Date,Double>();
        }
        
        // Process the data based on the display type
        if ("count".equals(displayType)) {
            processedData = processCountData(filteredData);
        }
        else if ("measurement".equals(displayType)) {
            processedData = processMeasurementData(filteredData);
        }
        else if ("event".equals(displayType)) {
            processedData = processEventData(filteredData);
        }
        else if ("category".equals(displayType)) {
            processedData = processCategoryData(filteredData);
        }
        else {
            processedData = new HashMap<Date,Double>();
        }
        
        return processedData;
    }
    
    /**
     * Processes a collection of DataPointAwData into a Map of dates and opacities.
     * Works day by day, adds up all counts from the day, then inserts into the processed
     * data.  Days that have no data are not inserted into the processed data.
     * 
     * @param filteredData
     * @return
     */
    private Map<Date, Double> processCountData(
            Collection<DataPointAwData> countData) {
        Map<Date, Double> processedData = new HashMap<Date, Double>();
        
        Date startDate = GWTCSimpleDatePicker.getFirstDayOfMonth(currentMonth);
        Date endDate = GWTCSimpleDatePicker.getLastDayOfMonth(currentMonth);
        
        // A for loop that increases one day at a time
        int maximumCountPerDay = 0;
        for (Date i = startDate; i.before(endDate); i = GWTCSimpleDatePicker.increaseMonth(i, 1)) {
            // Let's implement this as N^2 for now, if this is a problem we can optimize in
            // the future
            Collection<DataPointAwData> singleDayAwData = CollectionUtils.filter(countData, new Predicate<DataPointAwData>() {
                public boolean apply(DataPointAwData type) {
                    
                }
            });
        }
        
        return null;
    }

    private Map<Date, Double> processMeasurementData(
            Collection<DataPointAwData> filteredData) {
        // TODO Auto-generated method stub
        return null;
    }

    private Map<Date, Double> processEventData(
            Collection<DataPointAwData> filteredData) {
        // TODO Auto-generated method stub
        return null;
    }

    private Map<Date, Double> processCategoryData(
            Collection<DataPointAwData> filteredData) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Fetches new data points based on the locally stored fields.  Checks to be sure
     * we have all the necessary data before sending the request.
     */
    private void fetchDataPoints() {
        // Data for the rpc request
        Date startDate, endDate;
        String userName, campaignId, clientName;
        
        // If we have no data to fetch, don't bother
        if (currentDayData.size() == 0) {
            return;
        }
        
        // Find the first and last day of the requested month
        startDate = GWTCSimpleDatePicker.getFirstDayOfMonth(currentMonth);
        endDate = GWTCSimpleDatePicker.getLastDayOfMonth(currentMonth);
        userName = currentUserInfo.getUserName();
        campaignId = currentCampaignInfo.getCampaignId();
        clientName = ClientInfo.getClientName();
        
        // Send our request to the rpcService and handle the result
        rpcService.fetchDataPoints(startDate, endDate, userName, currentDataPointLabel, campaignId, clientName, new AsyncCallback<DataPointQueryAwData>() {
            
            /**
             * Called when the server successfully transmits back data.  Save the data in local models
             * and send the updated models to the view for display.
             * 
             * @param awData The data returned from the server.
             */
            public void onSuccess(DataPointQueryAwData awData) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(Throwable error) {
                // TODO Auto-generated method stub
                
            }
            
        });
    }

    /**
     * Is called whenever a day is clicked in the registered view.  Sends out
     * a day clicked event in response, but is otherwise not handled here.
     */
    public void onDayClicked(Date selectedDate) {
        // TODO Auto-generated method stub
        
    }
    
}
