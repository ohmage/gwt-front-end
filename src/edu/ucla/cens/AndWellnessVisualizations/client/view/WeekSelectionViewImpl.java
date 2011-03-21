package edu.ucla.cens.AndWellnessVisualizations.client.view;

import java.util.Date;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.AndWellnessVisualizations.client.utils.DateUtils;


public class WeekSelectionViewImpl extends Composite implements WeekSelectionView {
    @UiTemplate("WeekSelectionView.ui.xml")
    interface WeekSelectionViewUiBinder extends UiBinder<Widget, WeekSelectionViewImpl> {}
    private static WeekSelectionViewUiBinder uiBinder =
      GWT.create(WeekSelectionViewUiBinder.class);
    
    // Logging capability
    private static Logger _logger = Logger.getLogger(WeekSelectionViewImpl.class.getName());
    
    // Fields defined in the ui XML
    @UiField Label previousWeekLabel;
    @UiField Label currentWeekLabel;
    @UiField Label nextWeekLabel;
    
    // Call the presenter in response to events (user clicks)
    private Presenter presenter;
    
    // Very simple constructor now that the UI is defined in XML
    public WeekSelectionViewImpl() {
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

    /**
     * Sets the current week to display.
     * 
     * @param week The first day of the 7 day week to display
     */
    public void setCurrentWeek(Date week) {
        DateTimeFormat dayFormat = DateTimeFormat.getFormat("MMM d");
        Date endOfWeek = DateUtils.addDays(week, 7);
        
        _logger.fine("Setting current week to " + dayFormat.format(week));
        
        currentWeekLabel.setText(dayFormat.format(week) + " - " + dayFormat.format(endOfWeek));
    }
    
    // Handlers for clicks
    @UiHandler("previousWeekLabel")
    void onPreviousWeekLabelClicked(ClickEvent event) {
        _logger.info("User clicked on previous week link.");
        
        if (presenter != null) {
            presenter.onPreviousWeekSelected();
        }
    }
    
    @UiHandler("nextWeekLabel")
    void onNextWeekLabelClicked(ClickEvent event) {
        _logger.info("User clicked on next week link.");
        
        if (presenter != null) {
            presenter.onNextWeekSelected();
        }
    }
    
    public Widget asWidget() {
        return this;
    }
}
