package edu.ucla.cens.mobilize.client.ui;


import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.model.PromptResponse;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.utils.AwUrlBasedResourceUtils;
import edu.ucla.cens.mobilize.client.utils.DateUtils;

public class ResponseDisclosurePanel extends Composite
	implements HasMouseOverHandlers, HasMouseOutHandlers, ResponseDisplayWidget {
  
  public interface ResponseDisclosurePanelStyle extends CssResource {
    String privacyPrivate();
    String privacyShared();
    String privacyInvisible();
    String promptResponse();
    String promptText();
    String promptValue();
    String clickable();
  }

  @UiField ResponseDisclosurePanelStyle style;
	@UiField Label campaignName;
	@UiField CheckBox checkbox;
	@UiField Label surveyName;
	@UiField Label responseDateLabel;
	@UiField Label responsePrivacy;
	@UiField Hidden responseKey;
	@UiField VerticalPanel promptResponseVerticalPanel;
	@UiField DisclosurePanel disclosurePanel;
	
	private DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
	
	private static ResponseDisclosurePanelUiBinder uiBinder = GWT
			.create(ResponseDisclosurePanelUiBinder.class);

	interface ResponseDisclosurePanelUiBinder extends
			UiBinder<Widget, ResponseDisclosurePanel> {
	}

	public ResponseDisclosurePanel() {
		initWidget(uiBinder.createAndBindUi(this));
		this.disclosurePanel.setAnimationEnabled(true);
	}
	
	public void setCampaignName(String campaignName) {
	  this.campaignName.setText(campaignName);
	}
	
	public void setSurveyName(String surveyName) {
	  this.surveyName.setText(surveyName);
	}
	
	public void setDate(Date date) {
	  String dateString = (date != null) ? this.dateTimeFormat.format(date) : "";
	  this.responseDateLabel.setText(dateString);
	}
	
	public void setPrivacy(Privacy privacy) {
    this.responsePrivacy.setText(privacy.toString());
    switch (privacy) {
      case SHARED:
        setPrivacyStyleShared();
        break;
      case PRIVATE:
        setPrivacyStylePrivate();
        break;
      case INVISIBLE:
        setPrivacyStyleInvisible();
        break;
      default:
        clearPrivacyStyles();        
        break;
    }
	}
	
	public void setSurveyResponseKey(String surveyResponseKey) {
    this.responseKey.setValue(surveyResponseKey);
	}
	
	public void clearPromptResponses() {
	  this.promptResponseVerticalPanel.clear();
	}
	
	
	private void addPromptResponse(String promptText, Widget typeSpecificDisplayWidget) {
    // wrap prompt text in styled div
	  HTML promptTextHtml = new HTML(promptText);
	  promptTextHtml.setStyleName(style.promptText());
	  
	  // add style to the value widget
	  typeSpecificDisplayWidget.addStyleName(style.promptValue());
	  
	  // wrap in a div with style
	  FlowPanel promptResponseContainer = new FlowPanel();
	  promptResponseContainer.setStyleName(style.promptResponse());
	  promptResponseContainer.add(promptTextHtml);
	  promptResponseContainer.add(typeSpecificDisplayWidget);
	  // add the whole thing to the prompt list
	  this.promptResponseVerticalPanel.add(promptResponseContainer);
	 
	}
	
	public void addPromptResponseTimestamp(String promptText, Date timestamp) {
	  // TODO: try/catch?
	  HTML timestampHtml = new HTML(this.dateTimeFormat.format(timestamp));
	  addPromptResponse(promptText, timestampHtml);
	}
	
	/*
	// integers only
	public void addPromptResponseNumber(String promptText, int number) {
	  // TODO: try/catch/
	  HTML numberHtml = new HTML(Integer.toString(number));
	  addPromptResponse(promptText, numberHtml);
	}*/
	
	public void addPromptResponseText(String promptText, String userInputText) {
	  addPromptResponse(promptText, new HTML(userInputText));
	}
	/*
	// also works for multi-choice custom
	public void addPromptResponseMultiChoice(String promptText, 
	                                         List<String> choiceKeys, 
	                                         Map<String, String> glossary) {
	  List<String> choiceValues = new ArrayList<String>();
	  for (String choiceKey : choiceKeys) {
	    if (glossary.containsKey(choiceKey)) {
	      choiceValues.add(glossary.get(choiceKey));
	    } else {
	      choiceValues.add("unrecognized choice");
	    }
	  }
	  HTML listOfChoices = new HTML(CollectionUtils.join(choiceValues, ","));
	  addPromptResponse(promptText, listOfChoices);
	}
	
	// also works for single choice custom
	public void addPromptResponseSingleChoice(String promptText,
	                                          String choiceKey,
	                                          Map<String, String> glossary) {
	  String promptValue = glossary.containsKey(choiceKey) ? 
	                       glossary.get(choiceKey) : "unrecognized choice";
	  addPromptResponse(promptText, new HTML(promptValue));
	}*/
	
	public void addPromptResponsePhoto(String promptText, final String imageUrl, String thumbnailUrl) {
	  Image image = new Image();
	  image.setUrl(thumbnailUrl);
	  // clicking on small image opens full size image in a new window
	  image.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        Window.open(imageUrl, "_blank", "");
      }
	  });
	  image.setStyleName(style.clickable());
	  addPromptResponse(promptText, image);
	}
	

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}
	
	@Override
	public void setSelected(boolean isChecked) {
	  checkbox.setValue(isChecked);
	}
	
	public void setOpen(boolean isOpen) {
	  this.disclosurePanel.setOpen(isOpen);
	}
	
	private void setPrivacyStylePrivate() {
	  clearPrivacyStyles();
	  this.responsePrivacy.addStyleName(style.privacyPrivate());
	}
	
	private void setPrivacyStyleShared() {
	  clearPrivacyStyles();
	  this.responsePrivacy.addStyleName(style.privacyShared());
	}
	
	private void setPrivacyStyleInvisible() {
	  clearPrivacyStyles();
	  this.responsePrivacy.addStyleName(style.privacyInvisible());
	}
	
	// remove privacy specific style but leave underlying campaign name style alone
	public void clearPrivacyStyles() {
	  this.responsePrivacy.removeStyleName(style.privacyInvisible());
	  this.responsePrivacy.removeStyleName(style.privacyPrivate());
	  this.responsePrivacy.removeStyleName(style.privacyShared());
	}
	
	public boolean isSelected() {
	  return this.checkbox.getValue();
	}
	
	public String getResponseKey() {
	  return this.responseKey.getValue();
	}

  @Override
  public void setResponse(SurveyResponse response) {
    this.setCampaignName(response.getCampaignName());
    this.setDate(response.getResponseDate());
    this.setSurveyName(response.getSurveyName());
    this.setSurveyResponseKey(response.getResponseKey());
    this.setPrivacy(response.getPrivacyState());
    for (PromptResponse promptResponse : response.getPromptResponses()) {
      switch (promptResponse.getPromptType()) {
        case TIMESTAMP:
          Date timestamp = DateUtils.translateFromServerFormat(promptResponse.getResponseRaw());
          addPromptResponseTimestamp(promptResponse.getText(), timestamp);
          break;
        case PHOTO:
          String rawResponse = promptResponse.getResponseRaw();
          // special case skipped/invalid photos by copying over the text
          if ("NOT_DISPLAYED".equals(rawResponse) || "SKIPPED".equals(rawResponse)) {
            addPromptResponseText(promptResponse.getText(), rawResponse);
          } else {
            // generate urls for thumbnail and full sized photo and pass to widget
            String thumbUrl = AwUrlBasedResourceUtils.getImageUrl(promptResponse.getResponseRaw(), 
                response.getUserName(),
                response.getCampaignId(),
                AwUrlBasedResourceUtils.ImageSize.SMALL);
            String fullSizedImageUrl = AwUrlBasedResourceUtils.getImageUrl(promptResponse.getResponseRaw(), 
                response.getUserName(),
                response.getCampaignId(),
                AwUrlBasedResourceUtils.ImageSize.ORIGINAL);
            addPromptResponsePhoto(promptResponse.getText(), 
                                   fullSizedImageUrl,
                                   thumbUrl);
          }
          break;
        default:
          addPromptResponseText(promptResponse.getText(), promptResponse.getResponsePrepared());
          break;
      }
    }
  }

  @Override
  public void expand() {
    this.disclosurePanel.setOpen(true);
  }

  @Override
  public void collapse() {
    this.disclosurePanel.setOpen(false);
  }
	
}
