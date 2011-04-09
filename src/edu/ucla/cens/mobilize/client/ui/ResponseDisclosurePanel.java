package edu.ucla.cens.mobilize.client.ui;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.model.SurveyResponse;

public class ResponseDisclosurePanel extends Composite
	implements HasMouseOverHandlers, HasMouseOutHandlers {
	@UiField Label campaignName;
	@UiField CheckBox checkbox;
	@UiField Label surveyName;
	@UiField Label responseDateLabel;
	@UiField HTML toolbar;
	@UiField HTML details;
	@UiField DisclosurePanel disclosurePanel;
	
	DateTimeFormat dateTimeFormat = DateTimeFormat.getShortDateFormat();
	
	private static ResponseDisclosurePanelUiBinder uiBinder = GWT
			.create(ResponseDisclosurePanelUiBinder.class);

	interface ResponseDisclosurePanelUiBinder extends
			UiBinder<Widget, ResponseDisclosurePanel> {
	}

	@SuppressWarnings("deprecation")
  public ResponseDisclosurePanel() {
		initWidget(uiBinder.createAndBindUi(this));
		this.addMouseOverHandler(new ResponsePanelMouseEventHandler());
		this.addMouseOutHandler(new ResponsePanelMouseEventHandler());
		this.disclosurePanel.setAnimationEnabled(true);
	}
	
	public ResponseDisclosurePanel setResponse(SurveyResponse response) {
		campaignName.setText(response.getCampaignName()); 
		surveyName.setText(response.getSurveyName());
		// responseDate.setText(response.getDateString()); // todo: get date, not string
		Date date = response.getResponseDate();
		String dateString = (date != null) ? this.dateTimeFormat.format(date) : "";
		responseDateLabel.setText(dateString); 
		details.setHTML(response.getDetails()); // fixme sanitize
		return this; // for chaining
	}

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}
	
	public void ShowToolbar() {
		toolbar.removeStyleName("hidden");
	}
	
	public void HideToolbar() {
		toolbar.addStyleName("hidden");
	}
	
	public class ResponsePanelMouseEventHandler implements MouseOverHandler, MouseOutHandler {

		@Override
		public void onMouseOut(MouseOutEvent event) {
			ResponseDisclosurePanel panel = (ResponseDisclosurePanel)event.getSource();
			if (panel.disclosurePanel.isOpen()) {
				panel.ShowToolbar();
			} else {
				panel.HideToolbar();
			}
		}

		@Override
		public void onMouseOver(MouseOverEvent event) {
			ResponseDisclosurePanel panel = (ResponseDisclosurePanel)event.getSource();
			panel.ShowToolbar();
		}
	}
	
	public void setChecked(boolean isChecked) {
	  checkbox.setValue(isChecked);
	}

}
