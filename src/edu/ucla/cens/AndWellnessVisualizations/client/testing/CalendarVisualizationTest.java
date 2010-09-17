package edu.ucla.cens.AndWellnessVisualizations.client.testing;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

import edu.ucla.cens.AndWellnessVisualizations.client.view.CalendarVisualizationView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.CalendarVisualizationViewImpl;

/**
 * Test of the CalendarVisualization.  For now only initializes the View and attaches
 * it to the test_calendar_visualization div.
 * 
 * @author jhicks
 *
 */
public class CalendarVisualizationTest implements EntryPoint {

    /**
     * Creates the ValendarVisualizationView on the RootPanel
     */
    public void onModuleLoad() {
        CalendarVisualizationView calViz = new CalendarVisualizationViewImpl();
        
        RootPanel.get("test_calendar_visualization").add(calViz.asWidget());
    }

}
