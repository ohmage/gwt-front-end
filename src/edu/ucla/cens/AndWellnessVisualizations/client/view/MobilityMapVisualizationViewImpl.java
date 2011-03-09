package edu.ucla.cens.AndWellnessVisualizations.client.view;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.AndWellnessVisualizations.client.model.MobilityDataPointAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.MobilityLocationAwData;

public class MobilityMapVisualizationViewImpl extends Composite 
	implements MobilityMapVisualizationView {
	@UiTemplate("MobilityMapVisualizationView.ui.xml")
    interface MobilityMapVisualizationViewUiBinder extends UiBinder<Widget, MobilityMapVisualizationViewImpl> {}
    private static MobilityMapVisualizationViewUiBinder uiBinder =
      GWT.create(MobilityMapVisualizationViewUiBinder.class);

    private static Logger _logger = Logger.getLogger(MobilityMapVisualizationViewImpl.class.getName());
    
    @UiField SimplePanel mapPanel;
    
	private Presenter presenter;
	
	// Stores the google map
	private MapWidget map = null;
	// Icons for the map
	private Icon bikeIcon = null;
	private Icon walkIcon = null;
	private Icon runIcon = null;
	private Icon driveIcon = null;
	private Icon stillIcon = null;
	
	// Stores the location data list
	private List<MobilityDataPointAwData> locationData;
	
	public MobilityMapVisualizationViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		
		// Init the maps api
		Maps.loadMapsApi("ABQIAAAA5ZXjE5Rq-KGomi3qK8oshxRi_j0U6kJrkFvY4-OX2XYmEAa76BQ2ZkOydhEh44vXPVI_djTFw81U0w", "2", false, new Runnable() {
			public void run() {
				buildMap();
		    }
		});
	}
	
	private void buildMap() {
	    map = new MapWidget();
	    map.setSize("600px", "500px");
	    // Add some controls for the zoom level
	    map.addControl(new LargeMapControl());
	    
	    mapPanel.add(map);
	    
	    // Setup the icons
	    bikeIcon = Icon.newInstance("../images/bikeicon.png");
    	walkIcon = Icon.newInstance("../images/walkicon.png");
    	runIcon = Icon.newInstance("../images/runicon.png");
    	driveIcon = Icon.newInstance("../images/driveicon.png");
    	stillIcon = Icon.newInstance("../images/stillicon.png");
	}
	
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	public void setDataList(List<MobilityDataPointAwData> data) {
		this.locationData = data;
		
		if (map != null) {
			// Clear the current data
			map.clearOverlays();
			LatLngBounds bounds = LatLngBounds.newInstance();
			
			for (MobilityDataPointAwData dataPoint : this.locationData) {
				// Grab the data from the dataPoint
				String mode = dataPoint.getMode();
            	String locationStatus = dataPoint.getLocationStatus();
            	double lat, lon;
            	
            	// Check if the lat/lon data is valid
            	if (locationStatus.equals("valid")) {
            		MobilityLocationAwData mobLoc = dataPoint.getLocation();
            		lat = mobLoc.getLatitude();
            		lon = mobLoc.getLongitude();
            		
            		_logger.finer("Mobility: lat " + lat + " lon " + lon + " mode " + mode);
            	}
            	// Do nothing for now if non valid loc data
            	else {
            		continue;
            	}
				
				Icon iconToUse;
                if (mode.equals("still")) {
                	iconToUse = stillIcon;
                }
                else if (mode.equals("walk")) {
                	iconToUse = walkIcon;
                }
                else if (mode.equals("run")) {
                	iconToUse = runIcon;
                }
                else if (mode.equals("bike")) {
                	iconToUse = bikeIcon;
                }
                else if (mode.equals("drive")) {
                	iconToUse = driveIcon;
                }
                else {
                	iconToUse = Icon.newInstance();
                }
                
                // Set basic icon parameters
                iconToUse.setIconSize(Size.newInstance(20,20));
                
                // Set marker options
                MarkerOptions options = MarkerOptions.newInstance();
                options.setIcon(iconToUse);
                
                // Set the point location
                LatLng point = LatLng.newInstance(lat, lon);
                
                // Increase the bounds
                bounds.extend(point);
                
                // Add to the map
                map.addOverlay(new Marker(point, options));
			}
			
			// Zoom and center the map to the new bounds
			map.setZoomLevel(map.getBoundsZoomLevel(bounds));
			map.setCenter(bounds.getCenter());
		}
	}

	public Widget asWidget() {
		return this;
	}





}
