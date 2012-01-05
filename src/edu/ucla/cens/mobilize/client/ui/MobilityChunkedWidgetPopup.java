package edu.ucla.cens.mobilize.client.ui;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.AwConstants;
import edu.ucla.cens.mobilize.client.common.LocationStatus;
import edu.ucla.cens.mobilize.client.common.MobilityMode;
import edu.ucla.cens.mobilize.client.model.MobilityChunkedInfo;
import edu.ucla.cens.mobilize.client.model.PromptResponse;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.utils.AwUrlBasedResourceUtils;

public class MobilityChunkedWidgetPopup extends Composite {

	private static MobilityChunkedWidgetPopupUiBinder uiBinder = GWT
		.create(MobilityChunkedWidgetPopupUiBinder.class);

	interface MobilityChunkedWidgetPopupStyle extends CssResource {
		String mode_still();
		String mode_walk();
		String mode_run();
		String mode_bike();
		String mode_drive();
		String mode_error();
		String hidden();
	}

	interface MobilityChunkedWidgetPopupUiBinder extends
		UiBinder<Widget, MobilityChunkedWidgetPopup> {
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
	@UiField InlineLabel duration;
	@UiField InlineLabel locStatus;
	@UiField InlineLabel locCoords;
	@UiField InlineLabel locProvider;
	@UiField InlineLabel locTimestamp;
	@UiField InlineLabel locAccuracy;

	@UiField MobilityChunkedWidgetPopupStyle style;
  
	public MobilityChunkedWidgetPopup() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setResponse(MobilityChunkedInfo mob) {
		// Set modes
		Map<MobilityMode, Integer> mc = mob.getModeCount();
		if (mc.containsKey(MobilityMode.STILL)) {
			mode_still.setText(Integer.toString(mc.get(MobilityMode.STILL)));
			mode_still.setStyleName(style.mode_still());
		}
		if (mc.containsKey(MobilityMode.WALK)) {
			mode_walk.setText(Integer.toString(mc.get(MobilityMode.WALK)));
			mode_walk.setStyleName(style.mode_walk());
		}
		if (mc.containsKey(MobilityMode.RUN)) {
			mode_run.setText(Integer.toString(mc.get(MobilityMode.RUN)));
			mode_run.setStyleName(style.mode_run());
		}
		if (mc.containsKey(MobilityMode.BIKE)) {
			mode_bike.setText(Integer.toString(mc.get(MobilityMode.BIKE)));
			mode_bike.setStyleName(style.mode_bike());
		}
		if (mc.containsKey(MobilityMode.DRIVE)) {
			mode_drive.setText(Integer.toString(mc.get(MobilityMode.DRIVE)));
			mode_drive.setStyleName(style.mode_drive());
		}
		if (mc.containsKey(MobilityMode.ERROR)) {
			mode_error.setText(Integer.toString(mc.get(MobilityMode.ERROR)));
			mode_error.setStyleName(style.mode_error());
		}
		
		//username.setText(...);
		timestamp.setText(mob.getDate().toString());
		duration.setText(Integer.toString(mob.getDuration()) + " milliseconds");
		locStatus.setText(mob.getLocationStatus().toString());
		
		if (mob.getLocationStatus() != LocationStatus.UNAVAILABLE) {
			NumberFormat locationFormat = NumberFormat.getFormat("####.000000");
			String latString = locationFormat.format(mob.getLocationLat());
			String longString = locationFormat.format(mob.getLocationLong());
			locCoords.setText(latString + ", " + longString);
			locProvider.setText(mob.getLocationProvider());
			locTimestamp.setText(mob.getLocationTimestamp().toString());
			locAccuracy.setText(Float.toString(mob.getLocationAccuracy()) + " meters");
		}
	}
}
