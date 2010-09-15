package edu.ucla.cens.AndWellnessVisualizations.client.view;

import java.util.Date;
import java.util.List;

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
    
    // Very simple constructor now that the UI is defined in XML
    public CalendarVisualizationViewImpl() {
      initWidget(uiBinder.createAndBindUi(this));
      
      // Inject the CSS
      FrontendResources.INSTANCE.calendarVisualizationViewCss().ensureInjected();
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
     * Updates the View with a new set of selected data.
     * 
     * @param dayData A List of data on days, used to determine opacity values.
     */
    public void setDayData(List<T> dayData) {
        // TODO Auto-generated method stub
        
    }

    public Widget asWidget() {
        return this;
    }

    
        
    
}
