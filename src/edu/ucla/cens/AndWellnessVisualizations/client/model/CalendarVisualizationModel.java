package edu.ucla.cens.AndWellnessVisualizations.client.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The data Model for the CalendarVisualizationView.  Holds the displayed month along with
 * a List of Dates and a list of the Dates opacities.
 * 
 * @author jhicks
 *
 */
public class CalendarVisualizationModel {
    Date currentMonth;  // The currently selected month
    Map<Date, Double> dateData;  // Dates with data and their associated data
    
    /**
     * Initializes the Model with the current month and an empty list of enabled dates
     */
    public CalendarVisualizationModel() {
        currentMonth = new Date();
        dateData = new HashMap<Date, Double>();
    }
    
    // Setters and getters for the Model
    
    /**
     * Sets the currently selected month.
     */
    public void setCurrentMonth(Date month) {
        currentMonth = month;
    }
    
    /**
     * Gets the currently selected month.
     * 
     * @return The current month.
     */
    public Date getCurrentMonth() {
        return currentMonth;
    }
    
    /**
     * Adds normalized data (0 < data <= 1.0) for the given Date.  Replaces any old
     * data that Date may have had.
     * 
     * @param date The Date to add.
     * @param data The normalized data to add.
     */
    public void addDateData(Date date, Double data) {
        // Check to be sure the data is between 0 and 1
        if (! (data > 0.0 && data <= 1.0)) {
            throw new IllegalArgumentException("Data must be between 0 and 1.");
        }
        
        dateData.put(date, data);
    }
    
    /**
     * Removes any data for the given date.
     * 
     * @param date The Date to remove.
     */
    public void removeDateData(Date date) {
        dateData.remove(date);
    }
    
    /**
     * Returns the set of dates and associated data.
     * 
     * @return The date data.
     */
    public Map<Date, Double> getDateData() {
        return dateData;
    }
}
