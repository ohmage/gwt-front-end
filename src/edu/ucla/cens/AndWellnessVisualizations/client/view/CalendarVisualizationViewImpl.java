package edu.ucla.cens.AndWellnessVisualizations.client.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

import edu.ucla.cens.AndWellnessVisualizations.client.resources.FrontendResources;


public class CalendarVisualizationViewImpl extends Composite implements
        CalendarVisualizationView {
    @UiTemplate("CalendarVisualizationView.ui.xml")
    interface CalendarVisualizationViewUiBinder extends UiBinder<Widget, CalendarVisualizationViewImpl> {}
    private static CalendarVisualizationViewUiBinder uiBinder =
      GWT.create(CalendarVisualizationViewUiBinder.class);
    
    // Fields defined in the ui XML
    @UiField DatePicker calendarVisualizationDatePicker;
    
    // Call the presenter in response to events (user clicks)
    private Presenter presenter;
    
    // Very simple constructor now that the UI is defined in XML
    public CalendarVisualizationViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
      
        // Inject the CSS
        FrontendResources.INSTANCE.calendarVisualizationViewCss().ensureInjected();
      
        // The default state should be all days disabled, only enable days that have data
        disableAllVisibleDates();
    }
    
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    // Whenever a new date is selected, notify the Presenter
    @UiHandler("calendarVisualizationDatePicker")
    void calendarVisualizationDatePickerValueChanged(ValueChangeEvent<Date> event) {
        if (presenter != null) {
            presenter.onDayClicked(event.getValue());
        }
    }
    
    /**
     * Changes the calendar display to a new month.
     */
    public void updateMonth(Date month) {
        calendarVisualizationDatePicker.setCurrentMonth(month);
    }

    /**
     * Updates the calendar display with new opacity data.
     * 
     * @param dayData The data to updates.  Needs to be in the calendar display's 
     *        current month or it will be ignored.
     */
    public void updateDayData(Map<Date, Double> dayData) {
        Set<Date> days;  // Used to iterate through the days
        Iterator<Date> daysIterator;
        
        // Start by disabling all the days, then enable the ones in the data
        disableAllVisibleDates();

        // Enable the days, and set the opacity based on the day data
        days = dayData.keySet();
        daysIterator = days.iterator();
        while (daysIterator.hasNext()) {
            Date day = daysIterator.next();
            
            // Make sure the day is shown
            if (calendarVisualizationDatePicker.isDateVisible(day)) {
                // Enable the day
                calendarVisualizationDatePicker.setTransientEnabledOnDates(true, day);
                
                // Set the opacity of the day
                calendarVisualizationDatePicker.addTransientStyleToDates(getOpacityStyleName(dayData.get(day)), day);
            }
        }
    }

    public Widget asWidget() {
        return this;
    }

    
    // Private utility functions
    
    /**
     * Translates the incoming opacity to an opacity style name from calendarvisualizationview.css.
     * 
     * @param opacity Ranges from 0.0 to 1.0.
     * @return The style name to use.
     */
    private String getOpacityStyleName(Double opacityDouble) {
        // Hacky, we have a bunch of styles defined in the style sheet, return the closest
        // one here.  Can we instead change the opacity on the fly?
        // TODO: Implement a new DatePickerView that can do this, not worth the time investment right now
        double opacity = opacityDouble.doubleValue();
        
        // Round up to the nearest 5
        opacity *= 100;
        opacity += 5 - (opacity % 5);
        
        // Return the style name
        return "opacity" + (int) opacity;
    }
    
    
    /**
     * Disables all visible dates in the calendar.
     */
    private void disableAllVisibleDates() {
        // Grab the dates to disable
        Date firstVisibleDate = calendarVisualizationDatePicker.getFirstDate();
        Date lastVisibleDate = calendarVisualizationDatePicker.getLastDate();
        
        // Run through the dates one by one adding to a list to disable
        List<Date> datesToDisable = new ArrayList<Date>();
        for (Date i = (Date)firstVisibleDate.clone(); i.before(lastVisibleDate); i.setTime(i.getTime() + 1 * 24 * 60 * 60 * 1000)) {
            datesToDisable.add((Date)i.clone());
        }
        
        // Disable all the dates
        calendarVisualizationDatePicker.setTransientEnabledOnDates(false, datesToDisable);
    }
}
