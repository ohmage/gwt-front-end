package edu.ucla.cens.AndWellnessVisualizations.client.view;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;



public class VisualizationSelectionViewImpl extends Composite implements VisualizationSelectionView {
    @UiTemplate("VisualizationSelectionView.ui.xml")
    interface VisualizationSelectionViewUiBinder extends UiBinder<Widget, VisualizationSelectionViewImpl> {}
    private static VisualizationSelectionViewUiBinder uiBinder =
      GWT.create(VisualizationSelectionViewUiBinder.class);
    
    // Logging capability
    private static Logger _logger = Logger.getLogger(VisualizationSelectionViewImpl.class.getName());
    
    // Fields defined in the ui XML
    @UiField Image calendarSelection;
    @UiField Image mapSelection;
    @UiField Image chartSelection;
    
    // Call the presenter in response to events (user clicks)
    private Presenter presenter;
    
    // Very simple constructor now that the UI is defined in XML
    public VisualizationSelectionViewImpl() {
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

    // Handlers for clicks
    @UiHandler("calendarSelection")
    void onCalendarSelectionClicked(ClickEvent event) {
        _logger.info("User clicked on calendar selection.");
        
        if (presenter != null) {
            presenter.onSelection(Presenter.VizType.CALENDAR);
        }
    }
    
    @UiHandler("mapSelection")
    void onMapSelectionClicked(ClickEvent event) {
        _logger.info("User clicked on map selection.");
        
        if (presenter != null) {
            presenter.onSelection(Presenter.VizType.MAP);
        }
    }
    
    @UiHandler("chartSelection")
    void onChartSelectionClicked(ClickEvent event) {
        _logger.info("User clicked on chart viz.");
        
        if (presenter != null) {
            presenter.onSelection(Presenter.VizType.CHART);
        }
    }
    
    public Widget asWidget() {
        return this;
    }
}
