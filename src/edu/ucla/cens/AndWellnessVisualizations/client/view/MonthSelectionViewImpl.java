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


public class MonthSelectionViewImpl extends Composite implements MonthSelectionView {
    @UiTemplate("MonthSelectionView.ui.xml")
    interface MonthSelectionViewUiBinder extends UiBinder<Widget, MonthSelectionViewImpl> {}
    private static MonthSelectionViewUiBinder uiBinder =
      GWT.create(MonthSelectionViewUiBinder.class);
    
    // Logging capability
    private static Logger _logger = Logger.getLogger(MonthSelectionViewImpl.class.getName());
    
    // Fields defined in the ui XML
    @UiField Label previousMonthLabel;
    @UiField Label currentMonthLabel;
    @UiField Label nextMonthLabel;
    
    // Call the presenter in response to events (user clicks)
    private Presenter presenter;
    
    // Very simple constructor now that the UI is defined in XML
    public MonthSelectionViewImpl() {
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
     * Sets the current month to display.
     * 
     * @param month The new current month
     */
    public void setCurrentMonth(Date month) {
        DateTimeFormat monthFormat = DateTimeFormat.getFormat("MMMM yyyy");
        
        _logger.fine("Setting current month to " + monthFormat.format(month));
        
        currentMonthLabel.setText(monthFormat.format(month));
    }
    
    // Handlers for clicks
    @UiHandler("previousMonthLabel")
    void onPreviousMonthLabelClicked(ClickEvent event) {
        _logger.info("User clicked on previous month link.");
        
        if (presenter != null) {
            presenter.onPreviousMonthSelected();
        }
    }
    
    @UiHandler("nextMonthLabel")
    void onNextMonthLabelClicked(ClickEvent event) {
        _logger.info("User clicked on next month link.");
        
        if (presenter != null) {
            presenter.onNextMonthSelected();
        }
    }
    
    public Widget asWidget() {
        return this;
    }
}
