package edu.ucla.cens.AndWellnessVisualizations.client.presenter;

import com.google.code.p.gwtchismes.client.GWTCSimpleDatePicker;
import com.google.gwt.event.shared.HandlerManager;
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
import edu.ucla.cens.AndWellnessVisualizations.client.utils.DateUtils;
import edu.ucla.cens.AndWellnessVisualizations.client.utils.Predicate;
import edu.ucla.cens.AndWellnessVisualizations.client.view.CalendarVisualizationView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
    
    // Nice logging utility
    private static Logger _logger = Logger.getLogger(CalendarVisualizationPresenter.class.getName());
    
    public CalendarVisualizationPresenter(AndWellnessRpcService rpcService, 
            HandlerManager eventBus, CalendarVisualizationView view) {
        this.rpcService = rpcService;
        this.eventBus = eventBus;
        this.view = view;
        this.view.setPresenter(this);
    }    
    
    /**
     * Binds this presenter to listen for various events on the event bus.
     */
    public void bind() {
        // Listen for a new data point label selection
        eventBus.addHandler(DataPointLabelSelectionEvent.TYPE,
            new DataPointLabelSelectionEventHandler() {
                public void onSelection(DataPointLabelSelectionEvent event) {
                    _logger.fine("Receveived a data point label selection event with label " + event.getDataPointLabelSelection());
                    
                    currentDataPointLabel = event.getDataPointLabelSelection();                   
                }            
        });
        
        // Listen for a new month selection.  Change to the new month and fetch new data from
        // the server.
        eventBus.addHandler(MonthSelectionEvent.TYPE,
            new MonthSelectionEventHandler() {
                public void onSelection(MonthSelectionEvent event) {
                    _logger.fine("Received a month selection event with month " + currentMonth);
                    
                    currentMonth = event.getMonthSelection();
                    
                    if (view != null) {
                        view.updateMonth(currentMonth);
                        // Also update the data in case there is something new
                        view.updateDayData(currentDayData);
                    }
                }
        });
        
        // Listen for any incoming data, process and pass to the view
        eventBus.addHandler(NewDataPointAwDataEvent.TYPE,
            new NewDataPointAwDataEventHandler() {
                public void onNewData(NewDataPointAwDataEvent event) {
                    _logger.fine("Received a new data point event");
                    
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
        
        // Setup some default data
        currentMonth = new Date();
        currentDayData = new HashMap<Date, Double>();
        
        view.updateMonth(currentMonth);
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
                _logger.finest("Checking data point with label " + type.getLabel() + " and value " + type.getValue());
                
                // Check to be sure the label exists
                if (type.getLabel() == null) {
                    _logger.warning("Label field does not exist in data type");
                    return false;
                }
                
                return type.getLabel().equals(currentDataPointLabel);
            } 
        });
        
        // Filter out data points that are not in the current month
        filteredData = CollectionUtils.filter(filteredData, new Predicate<DataPointAwData>() {
            public boolean apply(DataPointAwData type) {
                _logger.finest("Checking data point with timestamp " + type.getTimeStamp());
                
                // Check to be sure the timestamp exists
                if (type.getTimeStamp() == null) {
                    _logger.warning("Timestamp field does not exist in data type.");
                    return false;
                }
                
                Date dataPointDate = DateUtils.translateFromServerFormat(type.getTimeStamp());
                
                // Check to make sure this is within the correct date range
                return (DateUtils.isDateInMonth(dataPointDate, currentMonth));
            } 
        });
        
        // Check to see if we found any data
        if (filteredData.size() == 0) {
            _logger.warning("Found no data in month " + currentMonth + " with data label " + currentDataPointLabel);
            
            // If no data, return an empty map
            return new HashMap<Date,Double>();
        }
        
        // Find the display type of the data
        //displayType = CampaignInfo.getDisplayType(currentDataPointLabel);
        displayType = "count";
        
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
     * data.  Days that have no data are left out of the processed data.
     * 
     * @param filteredData
     * @return
     */
    private Map<Date, Double> processCountData(
            Collection<DataPointAwData> countData) {
        _logger.fine("processCountData(): Processing count data.");
        
        Map<Date, Double> processedData = new HashMap<Date, Double>();
        
        Date startDate = GWTCSimpleDatePicker.getFirstDayOfMonth(currentMonth);
        Date endDate = GWTCSimpleDatePicker.getLastDayOfMonth(currentMonth);
        
        int maximumCountPerDay = 0;
        // A for loop that increases one day at a time
        for (Date i = startDate; i.before(endDate); i = GWTCSimpleDatePicker.increaseDate(i, 1)) {
            // For every Date in the month, check all Dates in the collection to see if any are in the day.
            // This is fairly inefficient (O(days_in_month * Dates_in_Collection)) and if this is a problem
            // we can rework is later
            List<DataPointAwData> singleDayAwData = new ArrayList<DataPointAwData>();
            for (DataPointAwData singleCountDataPoint: countData) {
                Date dataPointDate = DateUtils.translateFromServerFormat(singleCountDataPoint.getTimeStamp());
                if (DateUtils.isDateInDay(dataPointDate, i)) {
                    // Since we are a count, this must be greater than 0 to make a difference (what's a count of 0 mean?)
                    if (Integer.parseInt(singleCountDataPoint.getValue()) > 0) {                    
                        singleDayAwData.add(singleCountDataPoint);
                        
                        _logger.finest("processCountData(): Found date " + dataPointDate + " in day " + i + " with value " + singleCountDataPoint.getValue());
                    }
                }
            }
            
            // If we found at any datapoints for this day
            if (singleDayAwData.size() > 0) {
                // Now add up all the values in the singleDayList and add it into the processedData Map
                int totalValue = 0;
                for (DataPointAwData day: singleDayAwData) {
                    totalValue += Integer.parseInt(day.getValue());
                }
                
                // See if we have a new maximum count in a single day for later normalization
                if (totalValue > maximumCountPerDay) {
                    maximumCountPerDay = totalValue;
                }
                
                // Add the data to the Map
                processedData.put(i, (double)totalValue);
            }
        }
        
        // Now normalize if necessary
        if (maximumCountPerDay > 1) {
            for (Date day: processedData.keySet()) {
                processedData.put(day, processedData.get(day) / (double) maximumCountPerDay);
            }
        }
        
        return processedData;
    }

    private Map<Date, Double> processMeasurementData(
            Collection<DataPointAwData> filteredData) {
        _logger.fine("processMeasurementData(): Processing measurement data.");
        
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
     * Is called whenever a day is clicked in the registered view.  Sends out
     * a day clicked event in response, but is otherwise not handled here.
     */
    public void onDayClicked(Date selectedDate) {
        // TODO Auto-generated method stub
        
    }
    
}
