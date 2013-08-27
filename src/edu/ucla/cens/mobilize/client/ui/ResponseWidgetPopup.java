package edu.ucla.cens.mobilize.client.ui;

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
import edu.ucla.cens.mobilize.client.model.PromptResponse;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.utils.AwUrlBasedResourceUtils;

public class ResponseWidgetPopup extends Composite {

  private static ResponseWidgetPopupUiBinder uiBinder = GWT
      .create(ResponseWidgetPopupUiBinder.class);

  interface ResponseWidgetPopupStyle extends CssResource {
    String promptContainer();
    String prompt();
    String promptImage();
    String promptQuestion();
    String promptResponse();
  }
  
  interface ResponseWidgetPopupUiBinder extends
      UiBinder<Widget, ResponseWidgetPopup> {
  }
  
  /**
   * Allows the calling code to install events on an element, necessary to
   * add DOM event handlers.
   */
  public interface ElementHandlerCallback {
	  public void addingElement(Element element, String url);
  }

  @UiField InlineLabel date;
  @UiField InlineLabel campaign;
  @UiField InlineLabel survey;
  @UiField InlineLabel username;
  @UiField FlowPanel prompts;
  @UiField InlineLabel location;
  
  @UiField ResponseWidgetPopupStyle style;
  
  public ResponseWidgetPopup() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public void setResponse(SurveyResponse surveyResponse, ElementHandlerCallback callback) {
    date.setText(surveyResponse.getResponseDate().toString());
    campaign.setText(surveyResponse.getCampaignName());
    survey.setText(surveyResponse.getSurveyName());
    username.setText(surveyResponse.getUserName());
    NumberFormat locationFormat = NumberFormat.getFormat("####.000");
    String latString = locationFormat.format(surveyResponse.getLatitude());
    String longString = locationFormat.format(surveyResponse.getLongitude());
    location.setText(latString + ", " + longString);
    
    for (PromptResponse promptResponse : surveyResponse.getPromptResponses()) {
      Widget responseDisplayWidget = null;
      switch (promptResponse.getPromptType()) {
      case PHOTO:
        String raw = promptResponse.getResponseRaw();
        if (raw.equals("SKIPPED") || raw.equals("NOT_DISPLAYED") || raw.equals("MEDIA_NOT_UPLOADED"))  {
          responseDisplayWidget = new HTML(raw);
        } else {
          // generate urls for thumbnail and full sized photo and pass to widget
          String thumbUrl = AwUrlBasedResourceUtils.getImageUrl(promptResponse.getResponseRaw(), 
              surveyResponse.getUserName(),
              surveyResponse.getCampaignId(),
              AwUrlBasedResourceUtils.ImageSize.SMALL);
          final String fullSizedImageUrl = AwUrlBasedResourceUtils.getImageUrl(promptResponse.getResponseRaw(), 
              surveyResponse.getUserName(),
              surveyResponse.getCampaignId(),
              AwUrlBasedResourceUtils.ImageSize.ORIGINAL);
          Image img = new Image(thumbUrl);
          img.setStyleName(style.promptImage());
          
          // Locking in the width and height here stops the Image from resizing on image
          // load which causes the InfoWindow to refresh and flicker
          img.setPixelSize(AwConstants.MAPS_THUMBNAIL_WIDTH, AwConstants.MAPS_THUMBNAIL_HEIGHT);
          
          // Let's the calling code do whatever with the image, specifically used to avoid dependencies
          // on the google maps API
          if (callback != null) {
        	  callback.addingElement(img.getElement(), fullSizedImageUrl);
          }
          
          FlowPanel panel = new FlowPanel();
          panel.add(img);
          responseDisplayWidget = panel;
        }
        break;
      // TODO: special case timestamp?
      default:
        // anything other than a photo, just copy it verbatim
        responseDisplayWidget = new HTML(promptResponse.getResponsePrepared());
        break;
      }
      
      // set up and style question
      HTML question = new HTML(promptResponse.getText());
      question.setStyleName(style.promptQuestion());

      // add style to response
      responseDisplayWidget.setStyleName(style.promptResponse());
      
      // add question and response to styled div
      FlowPanel panel = new FlowPanel();
      panel.setStyleName(style.prompt());
      panel.add(question);
      panel.add(responseDisplayWidget);
      
      // add the whole thing to prompt list
      prompts.add(panel);
      
    }
  }
  
}
