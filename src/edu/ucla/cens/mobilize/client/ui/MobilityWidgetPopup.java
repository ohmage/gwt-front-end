package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.LocationStatus;
import edu.ucla.cens.mobilize.client.common.MobilityMode;
import edu.ucla.cens.mobilize.client.model.MobilityInfo;

public class MobilityWidgetPopup extends Composite {

	private static MobilityWidgetPopupUiBinder uiBinder = GWT
		.create(MobilityWidgetPopupUiBinder.class);

	interface MobilityWidgetPopupStyle extends CssResource {
		String mode_still();
		String mode_walk();
		String mode_run();
		String mode_bike();
		String mode_drive();
		String mode_error();
		String hidden();
	}

	interface MobilityWidgetPopupUiBinder extends
		UiBinder<Widget, MobilityWidgetPopup> {
	}
	
	/**
	 * Allows the calling code to install events on an element, necessary to
	 * add DOM event handlers.
	 */
	public interface ElementHandlerCallback {
		public void addingElement(Element element, String url);
	}

	@UiField InlineLabel mode_still;
	@UiField InlineLabel mode_walk;
	@UiField InlineLabel mode_run;
	@UiField InlineLabel mode_bike;
	@UiField InlineLabel mode_drive;
	@UiField InlineLabel mode_error;
	//@UiField InlineLabel username;
	@UiField InlineLabel timestamp;
	@UiField InlineLabel locStatus;
	@UiField InlineLabel locCoords;
	//@UiField InlineLabel locProvider;
	//@UiField InlineLabel locTimestamp;
	@UiField InlineLabel locAccuracy;

	@UiField MobilityWidgetPopupStyle style;
  
	public MobilityWidgetPopup() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setResponse(MobilityInfo mob) {
		// Set modes
		MobilityMode mode = mob.getMode();
		if (mode.equals(MobilityMode.STILL)) {
			mode_still.setText("Still");
			mode_still.setStyleName(style.mode_still());
		} else if (mode.equals(MobilityMode.WALK)) {
			mode_walk.setText("Walk");
			mode_walk.setStyleName(style.mode_walk());
		} else if (mode.equals(MobilityMode.RUN)) {
			mode_run.setText("Run");
			mode_run.setStyleName(style.mode_run());
		} else if (mode.equals(MobilityMode.BIKE)) {
			mode_bike.setText("Bike");
			mode_bike.setStyleName(style.mode_bike());
		} else if (mode.equals(MobilityMode.DRIVE)) {
			mode_drive.setText("Drive");
			mode_drive.setStyleName(style.mode_drive());
		} else if (mode.equals(MobilityMode.ERROR)) {
			mode_error.setText("Error");
			mode_error.setStyleName(style.mode_error());
		}
		
		//username.setText(...);
		timestamp.setText(mob.getDate().toString());
		locStatus.setText(mob.getLocationStatus().toString());
		
		if (mob.getLocationStatus() != LocationStatus.UNAVAILABLE) {
			NumberFormat locationFormat = NumberFormat.getFormat("####.000000");
			String latString = locationFormat.format(mob.getLocationLat());
			String longString = locationFormat.format(mob.getLocationLong());
			locCoords.setText(latString + ", " + longString);
			//locProvider.setText(mob.getLocationProvider());
			//locTimestamp.setText(mob.getLocationTimestamp().toString());
			locAccuracy.setText(Float.toString(mob.getLocationAccuracy()) + " meters");
		}
	}
}
