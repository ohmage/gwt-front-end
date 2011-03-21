package edu.ucla.cens.AndWellnessVisualizations.client.view;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.AndWellnessVisualizations.client.AndWellnessConstants;
import edu.ucla.cens.AndWellnessVisualizations.client.model.ChunkedMobilityAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.MobilityDataPointAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.MobilityLocationAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.widget.IFrameForm;

public class MobilityChartVisualizationViewImpl extends Composite 
	implements MobilityChartVisualizationView {

    private static Logger _logger = Logger.getLogger(MobilityChartVisualizationViewImpl.class.getName());
    
	private Presenter presenter;
	
	// Our main chart widget
	IFrameForm frame = new IFrameForm("https://chart.googleapis.com/chart");
	
	// Stores the location data list
	private List<ChunkedMobilityAwData> locationData = null;
	
	public MobilityChartVisualizationViewImpl() {
		initWidget(frame);
	}
	
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	public void setDataList(List<ChunkedMobilityAwData> data) {
		this.locationData = data;
		
		_logger.fine("Received " + data.size() + " chunked mobility points.");
	}
	
	public Widget asWidget() {
		return this;
	}
}
