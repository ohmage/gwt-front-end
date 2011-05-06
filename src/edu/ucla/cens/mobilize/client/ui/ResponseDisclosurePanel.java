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
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.model.PromptResponse;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;

public class ResponseDisclosurePanel extends Composite
	implements HasMouseOverHandlers, HasMouseOutHandlers {
  
  public interface ResponseDisclosurePanelStyle extends CssResource {
    String privacyPrivate();
    String privacyPublic();
    String privacyInvisible();
    String promptResponse();
    String promptText();
    String promptValue();
  }

  @UiField ResponseDisclosurePanelStyle style;
	@UiField Label campaignName;
	@UiField CheckBox checkbox;
	@UiField Label surveyName;
	@UiField Label responseDateLabel;
	@UiField Label responsePrivacy;
	@UiField Hidden responseKey;
	@UiField HTML details;
	@UiField DisclosurePanel disclosurePanel;
	
	DateTimeFormat dateTimeFormat = DateTimeFormat.getMediumDateFormat(); 
	
	private static ResponseDisclosurePanelUiBinder uiBinder = GWT
			.create(ResponseDisclosurePanelUiBinder.class);

	interface ResponseDisclosurePanelUiBinder extends
			UiBinder<Widget, ResponseDisclosurePanel> {
	}

	@SuppressWarnings("deprecation")
  public ResponseDisclosurePanel() {
		initWidget(uiBinder.createAndBindUi(this));
		this.disclosurePanel.setAnimationEnabled(true);
	}
	
	public ResponseDisclosurePanel setResponse(SurveyResponse response) {
		campaignName.setText(response.getCampaignName());
		surveyName.setText(response.getSurveyName());
		Date date = response.getResponseDate();
		String dateString = (date != null) ? this.dateTimeFormat.format(date) : "";
		responseDateLabel.setText(dateString);
    responsePrivacy.setText(response.getPrivacyState().toString());
    responseKey.setValue(Integer.toString(response.getResponseKey()));

		StringBuilder sb = new StringBuilder();
		for (PromptResponse promptResponse : response.getPromptResponses()) {
	    sb.append("<div class='").append(style.promptResponse()).append("'>");
	    sb.append("<div class='").append(style.promptText()).append("'>");
        sb.append(promptResponse.getText());
      sb.append("</div>");
      sb.append("<div class='").append(style.promptValue()).append("'>");
        // FIXME: handle different types (e.g., images)
        String value = promptResponse.getResponse();
        sb.append((value != null) ? value : "unavailable");
      sb.append("</div>");
      sb.append("</div>");
		}
		details.setHTML(sb.toString());
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
	
	public void setChecked(boolean isChecked) {
	  checkbox.setValue(isChecked);
	}
	
	public void setPrivacyStylePrivate() {
	  clearPrivacyStyles();
	  this.responsePrivacy.addStyleName(style.privacyPrivate());
	}
	
	public void setPrivacyStylePublic() {
	  clearPrivacyStyles();
	  this.responsePrivacy.addStyleName(style.privacyPublic());
	}
	
	public void setPrivacyStyleInvisible() {
	  clearPrivacyStyles();
	  this.responsePrivacy.addStyleName(style.privacyInvisible());
	}
	
	// remove privacy specific style but leave underlying campaign name style alone
	public void clearPrivacyStyles() {
	  this.responsePrivacy.removeStyleName(style.privacyInvisible());
	  this.responsePrivacy.removeStyleName(style.privacyPrivate());
	  this.responsePrivacy.removeStyleName(style.privacyPublic());
	}
	
	public boolean isSelected() {
	  return this.checkbox.getValue();
	}
	
	public String getResponseKey() {
	  return this.responseKey.getValue();
	}
	
}
