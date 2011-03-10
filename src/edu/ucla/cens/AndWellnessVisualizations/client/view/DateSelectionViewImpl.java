package edu.ucla.cens.AndWellnessVisualizations.client.view;

import java.util.Date;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;


public class DateSelectionViewImpl extends Composite implements DateSelectionView {
    @UiTemplate("DateSelectionView.ui.xml")
    interface DateSelectionViewUiBinder extends UiBinder<Widget, DateSelectionViewImpl> {}
    private static DateSelectionViewUiBinder uiBinder =
      GWT.create(DateSelectionViewUiBinder.class);
    
    // Logging capability
    private static Logger _logger = Logger.getLogger(DateSelectionViewImpl.class.getName());
    
    // Call the presenter in response to events (user clicks)
    private Presenter presenter;
    
    @UiField DatePicker dateSelectionPicker;
    
    // Very simple constructor now that the UI is defined in XML
    public DateSelectionViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    /**
     * Sets the views presenter to call to handle events.
     * 
     * @param presenter The presenter to bind to.
     */
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    // Whenever a new date is selected, notify the Presenter
    @UiHandler("dateSelectionPicker")
    void calendarVisualizationDatePickerValueChanged(ValueChangeEvent<Date> event) {
        _logger.info("User clicked on date " + event.getValue());
        
        if (presenter != null) {
            presenter.onDaySelection(event.getValue());
        }
    }
    
    /**
     * Sets the current month to display.
     * 
     * @param month The new current month
     */
    public void setCurrentMonth(Date month) {
        DateTimeFormat monthFormat = DateTimeFormat.getFormat("MMMM yyyy");
        
        _logger.fine("Setting current month to " + monthFormat.format(month));
        
        dateSelectionPicker.setCurrentMonth(month);
    }
    
    public Widget asWidget() {
        return this;
    }
}
