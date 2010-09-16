package edu.ucla.cens.AndWellnessVisualizations.client.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

import edu.ucla.cens.AndWellnessVisualizations.client.resources.FrontendResources;


public class CalendarVisualizationViewImpl<T> extends Composite implements
        CalendarVisualizationView<T> {
    @UiTemplate("CalendarVisualizationView.ui.xml")
    interface CalendarVisualizationViewUiBinder extends UiBinder<Widget, CalendarVisualizationViewImpl<?>> {}
    private static CalendarVisualizationViewUiBinder uiBinder =
      GWT.create(CalendarVisualizationViewUiBinder.class);
    
    // Fields defined in the ui XML
    @UiField DatePicker calendarVisualizationDatePicker;
    
    // Call the presenter in response to events (user clicks)
    private Presenter<T> presenter;
    
    // Data
    Date currentMonth = null;
    Map<Date, Double> dayData;
    
    // Very simple constructor now that the UI is defined in XML
    public CalendarVisualizationViewImpl() {
      initWidget(uiBinder.createAndBindUi(this));
      
      // Inject the CSS
      FrontendResources.INSTANCE.calendarVisualizationViewCss().ensureInjected();
      
      // Initialize the data
      dayData = new HashMap<Date, Double>();
    }
    
    public void setPresenter(Presenter<T> presenter) {
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
     * Updates the View with a new current month.
     * Clear out the current dayData.
     */
    public void setCurrentMonth(Date month) {
        currentMonth = month;
        dayData.clear();
        
        calendarVisualizationDatePicker.setCurrentMonth(month);
    }
    
    /**
     * Updates the View with a new set of selected data.
     * 
     * @param dayData A List of data on days, used to determine opacity values.
     */
    public void setDayData(Map<Date, Double> dayData) {
        this.dayData = dayData;
        
        // Enable the days
        Set<Date> daySet = dayData.keySet();
    }

    public Widget asWidget() {
        return this;
    }

    
    // Private utility functions
    
    /**
     * Disables all shown dates in the calendar.
     */
    private void disableAllShownDates() {
        Date firstShownDate = calendarVisualizationDatePicker.getFirstDate();
        Date lastShownDate = calendarVisualizationDatePicker.getLastDate();
        List<Date> datesToDisable = new ArrayList<Date>();
        
        // Run through the dates one by one disabling
        for (Date i = (Date)firstShownDate.clone(); i.before(lastShownDate); i.setTime(i.getTime() + 1 * 24 * 60 * 60 * 1000)) {
            datesToDisable.add((Date)i.clone());
        }
        
        calendarVisualizationDatePicker.setTransientEnabledOnDates(false, datesToDisable);
    }
    
}
